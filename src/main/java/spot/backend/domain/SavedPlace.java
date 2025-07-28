package spot.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import spot.backend.login.memberService.domain.KakaoMem;

@Entity
@Getter
@Setter
public class SavedPlace {
    @Id @GeneratedValue
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;
    @ManyToOne(fetch = FetchType.LAZY)
    private KakaoMem userid;
}
