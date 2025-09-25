package spot.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spot.backend.domain.Place;
import spot.backend.domain.SavedPlace;
import spot.backend.login.memberService.domain.KakaoMem;

import java.util.List;
import java.util.Optional;


public interface SavedPlaceRepository extends JpaRepository<SavedPlace, Long> {
    Optional<SavedPlace> findByUserAndPlace(KakaoMem user, Place place);
    List<SavedPlace> findTop2ByPlaceOrderByCreatedAtDesc(Place place);
    List<SavedPlace> findByUserIdOrderByCreatedAtDesc(Long userId);
    @Query("SELECT COUNT(sp) FROM SavedPlace sp WHERE sp.place.id = :placeId")
    Long countByPlaceId(@Param("placeId") Long placeId);
    boolean existsByUserIdAndPlace(Long userId, Place place);
    void deleteByUserIdAndPlaceId(Long userId, Long placeId);
    List<SavedPlace> findByUserIdIn(List<Long> userIds);

}
