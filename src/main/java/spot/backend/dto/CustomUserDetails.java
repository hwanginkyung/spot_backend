package spot.backend.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import spot.backend.login.memberService.domain.KakaoMem;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

        private final KakaoMem user;
        public CustomUserDetails(KakaoMem user) {
            this.user = user;
        }
        public Long getId() {
            return user.getId();
        }
        public KakaoMem getUser() {
            return user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            // 예시로 ROLE_USER 권한만 부여
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }

        @Override
        public String getPassword() {
            return user.getPassword();
        }

        @Override
        public String getUsername() {
            return user.getNickname();
        }

        // 계정 만료 여부
        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        // 계정 잠금 여부
        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        // 비밀번호 만료 여부
        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        // 계정 활성화 여부
        @Override
        public boolean isEnabled() {
            return true;
        }
    }
