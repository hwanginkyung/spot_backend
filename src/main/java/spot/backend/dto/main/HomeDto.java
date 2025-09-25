package spot.backend.dto.main;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeDto {
    private List<FriendDto> friends;   // 최근 클릭한 친구 최대 5명
    private List<PlaceDto> places;
}
