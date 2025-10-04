package spot.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spot.backend.domain.Friend;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findByUserid(Long userId);
    @Query("select f.friendid from Friend f where f.userid = :userId")
    List<Long> findFriendIds(@Param("userId") Long userId);
}
