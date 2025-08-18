package spot.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import spot.backend.login.memberService.domain.KakaoMem;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SavedPlace extends BaseEntity{
    @Id @GeneratedValue
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;
    @ManyToOne(fetch = FetchType.LAZY)
    private KakaoMem user;
    private Integer rating=0;
    @Column(nullable = false)
    private String saveType = "SPOT";
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
