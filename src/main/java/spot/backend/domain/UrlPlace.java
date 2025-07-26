package spot.backend.domain;

import jakarta.persistence.*;

@Entity
public class UrlPlace {
    @Id @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Place placeid;
    @ManyToOne(fetch = FetchType.LAZY)
    private InstaUrl instaurl;
}
