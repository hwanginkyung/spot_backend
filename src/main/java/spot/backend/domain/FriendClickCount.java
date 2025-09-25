package spot.backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendClickCount extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 클릭한 사람(나)
    @Column( nullable = false)
    private Long userId;
    // 클릭당한 친구
    @Column(nullable = false)
    private Long friendId;
    private Long cnt;

    public void increment() {
        this.cnt = this.cnt + 1;
    }
}
