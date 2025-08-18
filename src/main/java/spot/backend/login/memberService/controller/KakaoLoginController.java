package spot.backend.login.memberService.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import spot.backend.login.memberService.domain.KakaoMem;
import spot.backend.login.memberService.dto.KakaoUserInfo;
import spot.backend.login.memberService.dto.LoginResponseDto;
import spot.backend.login.memberService.jwt.JwtUtil;
import spot.backend.login.memberService.service.MemberService;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;


@RestController
@RequiredArgsConstructor
public class KakaoLoginController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.client-secret}")
    private String clientSecret;


    @GetMapping("/api/auth/kakao/login")
    public String redirectToKakao() {
        String kakaoUrl = "http://kauth.kakao.com/oauth/authorize?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
        return "redirect:" + kakaoUrl;
    }


    @GetMapping("/api/auth/kakao/callback")
    public void kakaoCallback(@RequestParam String code, HttpServletResponse response) throws IOException {
            String accessToken = getAccessToken(code);
            String userInfoJson = getUserInfo(accessToken);
            JsonNode userInfoNode = objectMapper.readTree(userInfoJson);
            KakaoUserInfo kakaoUser = KakaoUserInfo.from(userInfoNode);

            // DB 등록 or 조회
            KakaoMem member = memberService.registerIfAbsent(kakaoUser);

            // JWT 발급
            String token = jwtUtil.createToken(member.getId());

        String email = member.getEmail();       // 한글 가능
        String nickname = member.getNickname(); // 한글 가능

        String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
        String encodedNickname = URLEncoder.encode(nickname, StandardCharsets.UTF_8);
            System.out.println(token);
            URI deeplink = UriComponentsBuilder
                    .newInstance()
                    .scheme("spot")
                    .path("oauth/kakao")   // path는 맨 앞 / 없이
                    .queryParam("token", token)
                    .queryParam("email", encodedEmail)
                    .queryParam("nickname", encodedNickname)
                    .build(true)           // 쿼리 파라미터까지 인코딩 보장
                    .toUri();
        response.sendRedirect(deeplink.toString());
    }


    private String getAccessToken(String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, String.class);

        System.out.println("카카오 토큰 응답: " + response.getBody());

        try {
            JsonNode json = objectMapper.readTree(response.getBody());
            JsonNode accessTokenNode = json.get("access_token");
            if (accessTokenNode == null) {
                throw new RuntimeException("access_token 필드가 응답에 없습니다. 응답: " + response.getBody());
            }
            return accessTokenNode.asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Kakao 토큰 JSON 파싱 실패", e);
        }
    }

    public String getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                entity,
                String.class
        );
        return response.getBody(); // 사용자 정보 JSON
    }
}
