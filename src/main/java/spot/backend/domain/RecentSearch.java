package spot.backend.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spot.backend.login.memberService.domain.KakaoMem;

@Entity
@Setter
@NoArgsConstructor
public class RecentSearch extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private int pin;
    private String keyword;
    @ManyToOne(fetch = FetchType.LAZY)
    private KakaoMem kakaoMem;

    public RecentSearch(int pin, String kw, KakaoMem user) {
        this.pin = pin;
        this.keyword = kw;
        this.kakaoMem = user;
    }
}
