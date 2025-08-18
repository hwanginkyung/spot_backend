package spot.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Place extends BaseEntity {
    @Id @GeneratedValue
    private long id;
    @Enumerated(EnumType.STRING)
    private PlaceList list;
    private String photo; //S3에 저장된 사진 URL
    private String address;
    private String name;
    private Double latitude;
    private Double longitude;
    private int savedCount;      // 북마크 수
    private int searchCount;     // DB 검색 수
    private double score;
    @Column(nullable = false)
    private Double ratingAvg = 0.00;
    @Column(nullable = false)
    private int ratingCount=0;
    @OneToMany(mappedBy = "place")
    private List<SavedPlace> savedPlaces;
    @OneToMany(mappedBy = "placeid")
    private List<UrlPlace> urlPlaceId;
    @OneToMany(mappedBy = "placeid")
    private List<PlaceLike> placeLike;
}
