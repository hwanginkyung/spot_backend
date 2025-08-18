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

    @Query(value = """
    SELECT 
        p.photo_url AS photoUrl,
        p.name AS name,
        p.address AS address,
        p.rating AS rating,
        p.category AS category,
        (6371 * acos(
            cos(radians(:lat)) * cos(radians(p.latitude)) *
            cos(radians(p.longitude) - radians(:lon)) +
            sin(radians(:lat)) * sin(radians(p.latitude))
        )) AS distance,
        sp.saved_at AS savedAt
    FROM saved_places sp
    JOIN places p ON sp.place_id = p.id
    WHERE sp.user_id = :userId
    ORDER BY distance ASC
""", nativeQuery = true)
    List<Object[]> findPlacesByUserOrderByDistance(
            @Param("userId") Long userId,
            @Param("lat") double lat,
            @Param("lon") double lon
    );

    @Query(value = """
    SELECT 
        p.photo_url AS photoUrl,
        p.name AS name,
        p.address AS address,
        p.rating AS rating,
        p.category AS category,
        NULL AS distance,
        sp.saved_at AS savedAt
    FROM saved_places sp
    JOIN places p ON sp.place_id = p.id
    WHERE sp.user_id = :userId
    ORDER BY sp.saved_at DESC
""", nativeQuery = true)
    List<Object[]> findPlacesByUserOrderByLatest(
            @Param("userId") Long userId
    );

}
