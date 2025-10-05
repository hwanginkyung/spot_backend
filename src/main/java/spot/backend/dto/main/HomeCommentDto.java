package spot.backend.dto.main;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeCommentDto {
    private long id;
    private long placeId;
    private String gid;
    private List<String> photos;     // 장소 사진
    private String name;        // 상호명
    private String address;     // 주소
    private double score;
    private boolean isMarked;
    private String comment;
    private long memId;
    private String memEmail;
    private String commentPhoto;
    private LocalDateTime createdAt;
}
