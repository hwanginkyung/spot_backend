package spot.backend.login.memberService.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import spot.backend.domain.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KakaoMem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String kakaoId;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String nickname;
    private String password;
    private String spotNickname;
    @Column(columnDefinition = "TEXT")
    private String info;
    private String photo; //S3에 저장된 사진 URL
    @OneToMany(mappedBy = "user")
    private List<SavedPlace> placeUserid;
    @OneToMany(mappedBy = "userid")
    private List<PlaceLike> placeLike;
    @OneToMany(mappedBy = "kakaoMem")
    private List<RecentSearch> recentSearch;

    @OneToMany(mappedBy = "member")
    private List<Friend> friends = new ArrayList<>();
    // 친구 관계: 나를 친구로 등록한 목록 (상대방이 나를 친구로 등록)
    @OneToMany(mappedBy = "friend")
    private List<Friend> followers = new ArrayList<>();
    @OneToMany(mappedBy= "kakaoMem")
    private List<Comment> comment;

}
