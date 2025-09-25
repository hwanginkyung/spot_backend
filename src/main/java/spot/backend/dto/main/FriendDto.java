package spot.backend.dto.main;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FriendDto {
    private Long id;
    private String nickname;
    private String profileImageUrl; // S3 URL
}
