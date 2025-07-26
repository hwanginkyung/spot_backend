package spot.backend.login.memberService.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record KakaoUserInfo(String id, String mail, String nickname) {
    public static KakaoUserInfo from(JsonNode json) {
        JsonNode kakaoAccount = json.get("kakao_account");
        JsonNode profile = kakaoAccount.get("profile");

        return new KakaoUserInfo(
                json.get("id").asText(),
                kakaoAccount.get("mail").asText(),
                profile.get("nickname").asText()
        );
    }
}

