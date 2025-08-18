package spot.backend.search.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;
}
