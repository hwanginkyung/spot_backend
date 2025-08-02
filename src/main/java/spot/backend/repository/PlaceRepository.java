package spot.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spot.backend.domain.Place;
import spot.backend.dto.main.MapDto;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    @Query("select up.place from SavedPlace up where up.user.id = :userId")
    List<Place> findFolderNamesByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT p.name, p.list, p.latitude, p.longitude, " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(p.latitude)) * cos(radians(p.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(p.latitude)))) AS distance " +
            "FROM saved_place sp " +
            "JOIN place p ON sp.place_id = p.id " +
            "WHERE sp.userid_id = :userId " +
            "HAVING distance <= :radius " +
            "ORDER BY distance", nativeQuery = true)
    List<MapDto> findNearbyPlaces(
            @Param("userId") Long userId,
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") double radius);
    Optional<Place> findByLatitudeAndLongitude(double latitude, double longitude);
}
