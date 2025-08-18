package spot.backend.service.place;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.backend.domain.Place;
import spot.backend.domain.SavedPlace;
import spot.backend.login.memberService.domain.KakaoMem;
import spot.backend.login.memberService.repository.KakaoMemRepository;
import spot.backend.repository.PlaceRepository;
import spot.backend.repository.SavedPlaceRepository;
import spot.backend.service.score.ScoreRedisService;


@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final PlaceRepository placeRepository;
    private final SavedPlaceRepository savedPlaceRepository;
    private final KakaoMemRepository kakaoMemRepository;
    private final ScoreRedisService scoreRedisService;
    @Transactional
    public boolean toggleBookmark(Long userId, Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("장소를 찾을 수 없습니다."));

        KakaoMem kakaoMem = kakaoMemRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다"));

        boolean exists = savedPlaceRepository.existsByUserIdAndPlace(userId, place);

        if (exists) {
            savedPlaceRepository.deleteByUserIdAndPlaceId(userId, placeId);
            place.setSavedCount(place.getSavedCount() - 1);   // dirty checking으로 DB 반영
            scoreRedisService.incrementBookmarkDelta(placeId, -1); // Redis delta
            return false;
        } else {
            SavedPlace bookmark = SavedPlace.builder()
                    .user(kakaoMem)
                    .place(place)
                    .build();
            savedPlaceRepository.save(bookmark);
            place.setSavedCount(place.getSavedCount() + 1);
            scoreRedisService.incrementBookmarkDelta(placeId, 1);  // Redis delta
            return true;
        }
    }
}
