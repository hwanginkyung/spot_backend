package spot.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spot.backend.domain.Place;
import spot.backend.domain.PlaceArea;

public interface PlaceAreaRepository extends JpaRepository<PlaceArea, Long> {
}
