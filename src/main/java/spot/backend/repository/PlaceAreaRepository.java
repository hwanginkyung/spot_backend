package spot.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spot.backend.domain.Place;
import spot.backend.domain.PlaceArea;

@Repository
public interface PlaceAreaRepository extends JpaRepository<PlaceArea, Long> {
}
