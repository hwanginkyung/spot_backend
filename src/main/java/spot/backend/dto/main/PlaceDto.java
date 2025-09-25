package spot.backend.dto.main;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlaceDto {
        private Long id;
        private String name;
        private String imageUrl; // S3 이미지들
        private int imageCount;         // 사진 개수
        private double distance;        // 사용자와의 거리(km)
}
