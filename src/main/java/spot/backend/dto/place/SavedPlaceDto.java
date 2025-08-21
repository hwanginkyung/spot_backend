package spot.backend.dto.place;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavedPlaceDto {
    private long id;
    private List<String> photos;     // 장소 사진
    private String name;        // 상호명
    private String address;     // 주소
    private double rating;      // 평점
    private String category;    // 업종
    private Long total;
}


