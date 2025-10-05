package spot.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import spot.backend.login.memberService.domain.KakaoMem;

@Entity
@Getter
public class Friend extends BaseEntity{
    @Id
    @GeneratedValue
    private long id;

    @Enumerated(EnumType.STRING)
    private FriendStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private KakaoMem member; // 나

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id")
    private KakaoMem friend; // 친구
}