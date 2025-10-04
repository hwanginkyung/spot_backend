package spot.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import spot.backend.login.memberService.domain.KakaoMem;

@Entity
@Getter
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String content;
    private String userId;
    @ManyToOne(fetch = FetchType.LAZY)
    private KakaoMem kakaoMem;
    @ManyToOne(fetch = FetchType.LAZY)
    private Place place;


}
