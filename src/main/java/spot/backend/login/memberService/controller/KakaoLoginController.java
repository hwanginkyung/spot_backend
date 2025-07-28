package spot.backend.login.memberService.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import spot.backend.login.memberService.domain.KakaoMem;
import spot.backend.login.memberService.dto.KakaoUserInfo;
import spot.backend.login.memberService.dto.LoginResponseDto;
import spot.backend.login.memberService.jwt.JwtUtil;
import spot.backend.login.memberService.service.MemberService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


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

    @PostConstruct
    public void check() {
        System.out.println("clientId = " + clientId);
        System.out.println("clientSecret = " + clientSecret);
    }


    @GetMapping("/api/auth/kakao/login")
    public String redirectToKakao() {
        String kakaoUrl = "https://kauth.kakao.com/oauth/authorize?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
        return "redirect:" + kakaoUrl;
    }


    @GetMapping("/api/auth/kakao/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam String code) {
        try {
            String accessToken = getAccessToken(code);
            String userInfoJson = getUserInfo(accessToken);
            JsonNode userInfoNode = objectMapper.readTree(userInfoJson);
            KakaoUserInfo kakaoUser = KakaoUserInfo.from(userInfoNode);

            // DB 등록 or 조회
            KakaoMem member = memberService.registerIfAbsent(kakaoUser);

            // JWT 발급
            String token = jwtUtil.createToken(String.valueOf(member.getId()));

            System.out.println(token);
            return ResponseEntity.ok(new LoginResponseDto(token, member.getMail(), member.getNickname()));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("로그인 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


    private String getAccessToken(String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String requestBody = "grant_type=authorization_code"
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&redirect_uri=" + redirectUri
                + "&code=" + code;

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

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
