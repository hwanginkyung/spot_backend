package spot.backend.domain;

import jakarta.persistence.*;
import spot.backend.login.memberService.domain.KakaoMem;

@Entity
public class SavedPlace {
    @Id @GeneratedValue
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Place placeid;
    @ManyToOne(fetch = FetchType.LAZY)
    private KakaoMem userid;
}
