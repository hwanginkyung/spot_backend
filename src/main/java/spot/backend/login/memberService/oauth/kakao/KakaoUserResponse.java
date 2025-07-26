package spot.backend.login.memberService.oauth.kakao;

import lombok.Getter;

public class KakaoUserResponse {
    private Long id;
    private KakaoAccount kakao_account;

    @Getter
    public static class KakaoAccount {
        private String email;
        private Profile profile;

        @Getter
        public static class Profile {
            private String nickname;
        }
    }
}
