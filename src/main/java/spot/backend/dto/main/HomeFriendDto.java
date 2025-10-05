package spot.backend.dto.main;


import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeFriendDto {
    String spotNickname;
    String email;
    String info;
    private List<FriendPlaceDto> places;
}
