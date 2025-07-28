package spot.backend.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import spot.backend.dto.CustomUserDetails;
import spot.backend.login.memberService.domain.KakaoMem;
import spot.backend.login.memberService.repository.KakaoMemRepository;
import org.springframework.security.core.userdetails.UserDetails;



@Service
    public class CustomUserDetailsService implements UserDetailsService {

        private final KakaoMemRepository userRepository;

        public CustomUserDetailsService(KakaoMemRepository userRepository) {
            this.userRepository = userRepository;
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            KakaoMem user = userRepository.findByNickname(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            return new CustomUserDetails(user);
        }
    }

