package spot.backend.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spot.backend.search.service.RecentSearchService;

@Component
@RequiredArgsConstructor
public class RecentSearchScheduler {

    private final RecentSearchService recentSearchService;

    @Scheduled(fixedRate = 600000) // 10분마다 실행
    public void flushRedisToDb() {
        recentSearchService.flushAll();
    }
}
