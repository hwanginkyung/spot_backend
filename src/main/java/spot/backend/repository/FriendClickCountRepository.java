package spot.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spot.backend.domain.FriendClickCount;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendClickCountRepository extends JpaRepository<FriendClickCount, Long> {

    Optional<FriendClickCount> findByUserIdAndFriendId(Long userId, Long friendId);

    @Query("SELECT f FROM FriendClickCount f " +
            "WHERE f.userId = :userId AND f.friendId IN :friendIds " +
            "ORDER BY f.cnt DESC")
    List<FriendClickCount> findTopFriends(@Param("userId") Long userId,
            @Param("friendIds") List<Long> friendIds,
            org.springframework.data.domain.Pageable pageable);

}
