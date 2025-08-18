package spot.backend.login.memberService.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spot.backend.login.memberService.domain.KakaoMem;
import spot.backend.login.memberService.dto.KakaoUserInfo;
import spot.backend.login.memberService.repository.KakaoMemRepository;


import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final KakaoMemRepository kakaoMemRepository;

    public KakaoMem registerIfAbsent(KakaoUserInfo userInfo) {
        Long ids= Long.valueOf(userInfo.id());
        Optional<KakaoMem> existingUser = kakaoMemRepository.findById(ids);
        System.out.println("조회된 사용자: " + existingUser);
        int atIndex = userInfo.email().indexOf("@");
        String spotNickname = (atIndex != -1) ? userInfo.email().substring(0, atIndex) : userInfo.email();
        return kakaoMemRepository.findByKakaoId(userInfo.id())
                .orElseGet(() -> {
                    KakaoMem newUser = new KakaoMem();
                    newUser.setKakaoId(userInfo.id());
                    newUser.setEmail(userInfo.email());
                    newUser.setSpotNickname(spotNickname);
                    newUser.setNickname(userInfo.nickname());
                    KakaoMem saved = kakaoMemRepository.save(newUser);
                    System.out.println("새 회원 저장됨: " + saved);
                    return saved;
                });
    }
}
