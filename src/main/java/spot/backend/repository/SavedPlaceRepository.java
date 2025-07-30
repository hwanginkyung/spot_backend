package spot.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spot.backend.domain.Place;
import spot.backend.domain.SavedPlace;
import spot.backend.login.memberService.domain.KakaoMem;

import java.util.List;
import java.util.Optional;

public interface SavedPlaceRepository extends JpaRepository<SavedPlace, Long> {
    Optional<SavedPlace> findByUserAndPlace(KakaoMem user, Place place);
    List<SavedPlace> findTop2ByPlaceOrderByCreatedAtDesc(Place place);
}
