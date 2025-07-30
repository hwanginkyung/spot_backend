package spot.backend.login.memberService.mapper;


import spot.backend.login.memberService.domain.KakaoMem;
import spot.backend.login.memberService.dto.SocialSignupRequestDto;

public class MemberMapper {
    public static KakaoMem toEntity(SocialSignupRequestDto dto) {
        return KakaoMem.builder()
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .build();
    }
}
