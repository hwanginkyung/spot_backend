package spot.backend.login.memberService.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import spot.backend.login.memberService.domain.KakaoMem;
import spot.backend.login.memberService.repository.KakaoMemRepository;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final KakaoMemRepository memberRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, KakaoMemRepository memberRepository) {
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", "");
            try {
                Claims claims = jwtUtil.parseToken(token);
                Long userId = Long.parseLong(claims.getSubject());
                KakaoMem member = memberRepository.findById(userId).orElse(null);

                if (member != null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // 토큰 파싱 실패시 무시 (401 따로 던지지 않음)
            }
        }

        filterChain.doFilter(request, response);
    }
}

