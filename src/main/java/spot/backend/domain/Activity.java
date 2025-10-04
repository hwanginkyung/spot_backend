package spot.backend.domain;

import jakarta.persistence.*;
import spot.backend.login.memberService.domain.KakaoMem;

@Entity
public class Activity extends BaseEntity {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private KakaoMem kakaoMem;   // 누가

    @ManyToOne(fetch = FetchType.LAZY)
    private Place place; // 어떤 장소에

    @Enumerated(EnumType.STRING)
    private ActivityType type; // SAVED, COMMENTED, LIKED ...
}
