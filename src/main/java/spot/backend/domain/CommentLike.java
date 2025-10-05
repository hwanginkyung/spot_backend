package spot.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import spot.backend.login.memberService.domain.KakaoMem;

@Entity
@Getter
public class CommentLike {
    @Id
    @GeneratedValue
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment comment;
    @ManyToOne(fetch = FetchType.LAZY)
    private KakaoMem kakaoMem;
    boolean liked=false;
}
