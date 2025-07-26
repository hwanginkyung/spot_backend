package spot.backend.login.memberService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import spot.backend.login.memberService.domain.KakaoMem;
import spot.backend.login.memberService.oauth.naver.NaverUserResponse;


@Getter
@AllArgsConstructor
public class SocialSignupRequestDto {
    private String provider;     // "kakao", "naver"
    private String providerId;   // 외부 서비스의 유저 ID
    private String mail;
    private String nickname;

    public KakaoMem toEntity() {
        return KakaoMem.builder()
                .mail(mail)
                .nickname(nickname)
                .build();
    }
}
