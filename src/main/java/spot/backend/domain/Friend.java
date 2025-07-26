package spot.backend.domain;

import jakarta.persistence.*;
import spot.backend.login.memberService.domain.KakaoMem;

@Entity
public class Friend {
    @Id
    @GeneratedValue
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private KakaoMem userid;
    @ManyToOne(fetch = FetchType.LAZY)
    private KakaoMem friend_userid;
    @Enumerated(EnumType.STRING)
    private FriendStatus status;
}