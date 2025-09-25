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
    private Long userid;
    private Long friendid;
    @Enumerated(EnumType.STRING)
    private FriendStatus status;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private KakaoMem member;
    @ManyToOne
    @JoinColumn(name = "friend_id")
    private KakaoMem friend;
}