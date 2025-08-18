package spot.backend.service.score;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Getter
public class ScoreRedisService {
    private final StringRedisTemplate redisTemplate;

    private static final String RATING_KEY = "place:ratingDelta";
    private static final String SEARCH_KEY = "place:searchDelta";
    private static final String BOOKMARK_KEY = "place:bookmarkDelta";

    public void incrementBookmarkDelta(Long placeId, double delta) {
        redisTemplate.opsForHash().increment(BOOKMARK_KEY, placeId.toString(), delta);
    }

    public int getBookmarkDelta(Long placeId) {
        Object val = redisTemplate.opsForHash().get(BOOKMARK_KEY, placeId.toString());
        return val == null ? 0 : Integer.parseInt(val.toString());
    }

    public void clearBookmarkDeltas() { redisTemplate.delete(BOOKMARK_KEY); }

    public void incrementSearchDelta(Long placeId, int delta) {
        redisTemplate.opsForHash().increment(SEARCH_KEY, placeId.toString(), delta);
    }

    public int getSearchDelta(Long placeId) {
        Object val = redisTemplate.opsForHash().get(SEARCH_KEY, placeId.toString());
        return val == null ? 0 : Integer.parseInt(val.toString());
    }

    public void incrementRatingDelta(Long placeId, double delta) {
        redisTemplate.opsForHash().increment(RATING_KEY, placeId.toString(), delta);
    }

    public double getRatingDelta(Long placeId) {
        Object val = redisTemplate.opsForHash().get(RATING_KEY, placeId.toString());
        return val == null ? 0.0 : Double.parseDouble(val.toString());
    }

    public Map<Object, Object> getAllBookmarkDeltas() {
        return redisTemplate.opsForHash().entries(BOOKMARK_KEY);
    }

    public Map<Object, Object> getAllSearchDeltas() {
        return redisTemplate.opsForHash().entries(SEARCH_KEY);
    }

    public Map<Object, Object> getAllRatingDeltas() {
        return redisTemplate.opsForHash().entries(RATING_KEY);
    }
    public void clearRatingDeltas() {
        redisTemplate.delete(RATING_KEY);
    }

    public void clearSearchDeltas() {
        redisTemplate.delete(SEARCH_KEY);
    }
}
