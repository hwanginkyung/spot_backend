package spot.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spot.backend.domain.Place;
import spot.backend.dto.MapDto;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    @Query("select up.place from SavedPlace up where up.userid = :userId")
    List<Place> findFolderNamesByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT name,list,latitude,logitude, " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(latitude)) * cos(radians(longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(latitude)))) " +
            "AS distance " +
            "FROM place " +
            "WHERE user_id = :userId " +
            "HAVING distance <= :radius " +
            "ORDER BY distance", nativeQuery = true)
    List<MapDto> findNearbyPlaces(
            @Param("userId") Long userId,
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radius") double radius);

}
