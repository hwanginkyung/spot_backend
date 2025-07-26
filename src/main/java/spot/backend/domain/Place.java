package spot.backend.domain;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Place {
    @Id @GeneratedValue
    private long id;
    @Enumerated(EnumType.STRING)
    private PlaceList list;
    private String photo; //S3에 저장된 사진 URL
    @Column(nullable = false)
    private String address;
    private String name;
    @OneToMany(mappedBy = "placeid")
    private List<SavedPlace> placeId;
    @OneToMany(mappedBy = "placeid")
    private List<UrlPlace> urlPlaceId;
    @OneToMany(mappedBy = "placeid")
    private List<PlaceLike> placeLike;
}
