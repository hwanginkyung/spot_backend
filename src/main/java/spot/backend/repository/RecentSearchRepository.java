package spot.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spot.backend.domain.RecentSearch;

@Repository
public interface RecentSearchRepository extends JpaRepository<RecentSearch, Long> {

}
