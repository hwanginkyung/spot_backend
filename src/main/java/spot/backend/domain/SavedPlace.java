package spot.backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spot.backend.login.memberService.domain.KakaoMem;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SavedPlace extends BaseEntity{
    @Id @GeneratedValue
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;
    @ManyToOne(fetch = FetchType.LAZY)
    private KakaoMem user;
    private Integer rating;
    public void updateRating(Integer rating) {
        this.rating = rating;
    }
    public SavedPlace(KakaoMem user, Place place, Integer rating) {
        this.user = user;
        this.place = place;
        this.rating = rating;
    }

    public SavedPlace(KakaoMem user, Place place) {
        this.user = user;
        this.place = place;
    }
}
