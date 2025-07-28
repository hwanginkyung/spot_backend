package spot.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Place {
    @Id @GeneratedValue
    private long id;
    @Enumerated(EnumType.STRING)
    private PlaceList list;
    private String photo; //S3에 저장된 사진 URL
    @Column(nullable = false)
    private String address;
    private String name;
    private Double latitude;
    private Double longitude;
    @OneToMany(mappedBy = "place")
    private List<SavedPlace> savedPlaces;
    @OneToMany(mappedBy = "placeid")
    private List<UrlPlace> urlPlaceId;
    @OneToMany(mappedBy = "placeid")
    private List<PlaceLike> placeLike;
}
