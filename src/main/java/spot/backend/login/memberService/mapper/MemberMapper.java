package spot.backend.login.memberService.mapper;


import spot.backend.login.memberService.domain.KakaoMem;
import spot.backend.login.memberService.dto.SocialSignupRequestDto;

public class MemberMapper {
    public static KakaoMem toEntity(SocialSignupRequestDto dto) {
        return KakaoMem.builder()
                .mail(dto.getMail())
                .nickname(dto.getNickname())
                .build();
    }
}
