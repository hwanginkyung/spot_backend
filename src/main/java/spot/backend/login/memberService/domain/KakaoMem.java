package spot.backend.login.memberService.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import spot.backend.domain.BaseEntity;
import spot.backend.domain.Friend;
import spot.backend.domain.PlaceLike;
import spot.backend.domain.SavedPlace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KakaoMem extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String kakaoId;
    @Column(nullable = false)
    private String mail;
    @Column(nullable = false)
    private String nickname;
    private String password;
    @Column(columnDefinition = "TEXT")
    private String info;
    private String photo; //S3에 저장된 사진 URL
    @OneToMany(mappedBy = "userid")
    @JsonIgnore
    private List<Friend> userid;
    @OneToMany(mappedBy = "friend_userid")
    @JsonIgnore
    private List<Friend> friendUserid;
    @OneToMany(mappedBy = "userid")
    private List<SavedPlace> placeUserid;
    @OneToMany(mappedBy = "userid")
    private List<PlaceLike> placeLike;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한이 없으면 빈 리스트 반환
        return List.of();
    }
    @Override
    public String getPassword() {
        // 카카오 로그인이라 비밀번호 없으면 빈 문자열 반환
        return "";
    }
    @Override
    public String getUsername() {
        return this.mail;  // 또는 로그인에 사용하는 필드 (예: nickname)
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;  // 필요하면 실제 로직 적용
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;  // 필요하면 실제 로직 적용
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // 필요하면 실제 로직 적용
    }

    @Override
    public boolean isEnabled() {
        return true;  // 필요하면 실제 로직 적용
    }

    // UserDetails 구현부 끝
}
