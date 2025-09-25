package spot.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spot.backend.domain.SavedPlace;
import spot.backend.domain.UrlPlace;

public interface UrlPlaceRepository extends JpaRepository<UrlPlace, Long> {
}
