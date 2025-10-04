package spot.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spot.backend.domain.Activity;
import spot.backend.domain.Place;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    @Query("""
        select distinct a.place
        from Activity a
        where a.kakaoMem.id in :friendIds
        and a.place in :places
        order by a.updatedAt desc
    """)
    List<Place> findFriendLatestPlaces(
            @Param("friendIds") List<Long> friendIds,
            @Param("places") List<Place> places
    );

}
