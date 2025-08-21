package spot.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spot.backend.domain.RecentSearch;

public interface RecentSearchRepository extends JpaRepository<RecentSearch, Long> {

}
