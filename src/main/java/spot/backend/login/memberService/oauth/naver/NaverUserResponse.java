package spot.backend.login.memberService.oauth.naver;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverUserResponse {

    private String resultcode;
    private String message;
    private NaverAccount response;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NaverAccount {
        private String id;
        private String email;
        private String nickname;
    }
}
