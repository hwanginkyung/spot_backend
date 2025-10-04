package spot.backend.dto.main;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HomePlaceDto {
    private long id;
    private String gid;
    private List<String> photos;     // 장소 사진
    private String name;        // 상호명
    private String address;     // 주소
    private double rating;      // 평점
    private int ratingCount;
    private String category;    // 업종
    private int savedCount;
    private int searchCount;
    private double score;
    private double distance;
    private boolean isMarked;
    private List<String> memPhotos;
    private String comment;
    private int commentCount;
    private long memId;
    private String commentPhoto;
}
