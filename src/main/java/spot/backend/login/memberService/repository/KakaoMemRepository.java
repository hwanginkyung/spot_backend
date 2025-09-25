package spot.backend.login.memberService.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import spot.backend.login.memberService.domain.KakaoMem;


import java.util.List;
import java.util.Optional;

public interface KakaoMemRepository extends JpaRepository<KakaoMem, Long> {
    Optional<KakaoMem> findByKakaoId(String kakaoId);
    Optional<KakaoMem> findById(Long Id);
    Optional<KakaoMem> findByNickname(String nickname);
    List<KakaoMem> findByIdIn(List<Long> ids);
}