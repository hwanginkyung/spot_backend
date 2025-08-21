package spot.backend.search.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.backend.domain.RecentSearch;
import spot.backend.dto.place.RecentSearchDto;
import spot.backend.login.memberService.domain.KakaoMem;
import spot.backend.login.memberService.repository.KakaoMemRepository;
import spot.backend.repository.RecentSearchRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecentSearchService {
    private final StringRedisTemplate redisTemplate;
    private final RecentSearchRepository recentSearchRepository;
    private final KakaoMemRepository kakaoMemRepository;

    private static final String KEY_PREFIX = "recent:search:";
    public void saveKeyword(Long userId, String keyword, int pin) {
        String key = KEY_PREFIX + userId;
        String value = keyword + "|" + pin;
        redisTemplate.opsForList().leftPush(key, value);
        // 최대 10개까지만 유지
        redisTemplate.opsForList().trim(key, 0, 9);
    }

    public List<RecentSearchDto> getRecentSearches(Long userId) {
        String key = KEY_PREFIX + userId;
        List<String> values = redisTemplate.opsForList().range(key, 0, -1);
        List<RecentSearchDto> result = new ArrayList<>();
        if (values != null) {
            for (String v : values) {
                String[] parts = v.split("\\|");
                String keyword = parts[0];
                int pin = Integer.parseInt(parts[1]);
                result.add(new RecentSearchDto(keyword, pin));
            }
        }
        return result;
    }

    @Transactional
    public void flushToDb(Long userId) {
        String key = KEY_PREFIX + userId;
        List<String> values = redisTemplate.opsForList().range(key, 0, -1);
        if (values != null && !values.isEmpty()) {
            KakaoMem user = kakaoMemRepository.findById(userId).orElse(null);
            for (String v : values) {
                // "keyword|pin" → 분리
                String[] parts = v.split("\\|");
                String keyword = parts[0];
                int pin = Integer.parseInt(parts[1]);

                RecentSearch entity = new RecentSearch(pin, keyword, user);
                recentSearchRepository.save(entity);
            }
            redisTemplate.delete(key); // flush 후 삭제
        }

    }
    @Transactional
    public void flushAll() {
        Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*");
        if (keys != null) {
            for (String key : keys) {
                Long userId = Long.parseLong(key.replace(KEY_PREFIX, ""));
                flushToDb(userId);
            }
        }
    }
}
