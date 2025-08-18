package spot.backend.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import spot.backend.repository.PlaceRepository;
import spot.backend.service.score.ScoreRedisService;

import java.util.Map;


@Component
@RequiredArgsConstructor
@Slf4j
public class ScoreBatchScheduler {

    private final ScoreRedisService scoreRedisService;
    private final PlaceRepository placeRepository;

    @Scheduled(fixedRate = 600_000) // 10분
    @Transactional
    public void flushDeltasToDB() {
        Map<Object, Object> ratingDeltas = scoreRedisService.getAllRatingDeltas();
        Map<Object, Object> searchDeltas = scoreRedisService.getAllSearchDeltas();

        // 평점 delta 반영
        ratingDeltas.forEach((key, value) -> {
            Long placeId = Long.valueOf((String) key);
            double delta = Double.parseDouble(value.toString());

            placeRepository.findById(placeId).ifPresent(place -> {
                place.setRatingAvg(place.getRatingAvg() + delta);
                placeRepository.save(place);
            });
        });

        // 검색량 delta 반영
        searchDeltas.forEach((key, value) -> {
            Long placeId = Long.valueOf((String) key);
            int delta = Integer.parseInt(value.toString());

            placeRepository.findById(placeId).ifPresent(place -> {
                place.setSearchCount(place.getSearchCount() + delta);
                placeRepository.save(place);
            });
        });

        scoreRedisService.clearRatingDeltas();
        scoreRedisService.clearSearchDeltas();

        log.info("10분 배치: 평점 {}개, 검색량 {}개 반영 완료",
                ratingDeltas.size(), searchDeltas.size());
    }
}

