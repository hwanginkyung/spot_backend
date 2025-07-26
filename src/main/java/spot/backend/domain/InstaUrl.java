package spot.backend.domain;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class InstaUrl {

    @Id
    @GeneratedValue
    private Long id;
    @OneToMany(mappedBy = "instaurl")
    private List<UrlPlace> urlplaces;
    @Column(nullable = false)
    private String url;
    private String texts;
    private String image; //S3에 대표 이미지 url 저장
}
