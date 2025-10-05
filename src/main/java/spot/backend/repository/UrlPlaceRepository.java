package spot.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spot.backend.domain.SavedPlace;
import spot.backend.domain.UrlPlace;

@Repository
public interface UrlPlaceRepository extends JpaRepository<UrlPlace, Long> {
}
