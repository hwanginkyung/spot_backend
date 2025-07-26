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
        Optional<KakaoMem> existingUser = kakaoMemRepository.findByKakaoId(userInfo.id());
        System.out.println("조회된 사용자: " + existingUser);
        int atIndex = userInfo.mail().indexOf("@");
        String kakaoId = (atIndex != -1) ? userInfo.mail().substring(0, atIndex) : userInfo.mail();
        return kakaoMemRepository.findByKakaoId(userInfo.id())
                .orElseGet(() -> {
                    KakaoMem newUser = new KakaoMem();
                    newUser.setKakaoId(userInfo.id());
                    newUser.setMail(userInfo.mail());
                    newUser.setKakaoId(kakaoId);
                    newUser.setNickname(userInfo.nickname());
                    KakaoMem saved = kakaoMemRepository.save(newUser);
                    System.out.println("새 회원 저장됨: " + saved);
                    return saved;
                });
    }
}
