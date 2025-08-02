package spot.backend.service.place;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import spot.backend.domain.Place;
import spot.backend.domain.SavedPlace;
import spot.backend.dto.place.RatingDto;
import spot.backend.login.memberService.domain.KakaoMem;
import spot.backend.login.memberService.repository.KakaoMemRepository;
import spot.backend.repository.PlaceRepository;
import spot.backend.repository.SavedPlaceRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RatingService {
    private final KakaoMemRepository userRepository;
    private final PlaceRepository placeRepository;
    private final SavedPlaceRepository savedPlaceRepository;

    @Transactional
    public void giveRating(Long userId, RatingDto request) {
        KakaoMem user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
        Place place = placeRepository.findById(request.placeId())
                .orElseThrow(() -> new IllegalArgumentException("장소 없음"));

        Optional<SavedPlace> existing = savedPlaceRepository.findByUserAndPlace(user, place);
        int newScore = request.rating();
        if (existing.isPresent()) {
            if (existing.get().getRating() != 0) {
                SavedPlace rating = existing.get();
                int oldScore = rating.getRating();
                rating.updateRating(newScore);

                // 별점 평균 갱신 (수정)
                double newAverage = (place.getRatingAvg() * place.getRatingCount()
                        - oldScore + newScore) / place.getRatingCount();
                place.setRatingAvg(newAverage);
            }
            else{
                SavedPlace rating = existing.get();
                rating.updateRating(newScore);
                double newAverage = (place.getRatingAvg() * place.getRatingCount() + newScore)
                        / (place.getRatingCount() + 1);
                place.setRatingAvg(newAverage);
                place.setRatingCount(place.getRatingCount() + 1);
            }

        }
            else {
            // 새 별점 등록
            SavedPlace rating = new SavedPlace(user, place, newScore);
            savedPlaceRepository.save(rating);
            int newCount = place.getRatingCount() + 1;
            double newAverage = (place.getRatingAvg() * place.getRatingCount()
                    + newScore) / newCount;
            place.setRatingCount(newCount);
            place.setRatingAvg(newAverage);
        }
    }
    @Transactional
    public void removeRating(Long userId, Long placeId) {
        KakaoMem user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("장소 없음"));

        SavedPlace savedPlace = savedPlaceRepository.findByUserAndPlace(user, place)
                .orElseThrow(() -> new IllegalArgumentException("별점 정보 없음"));

        int oldRating = savedPlace.getRating();
        if (oldRating == 0) return; // 이미 제거된 경우 처리

        // 별점 초기화
        savedPlace.updateRating(0);

        // 장소 별점 평균, 카운트 갱신
        int newCount = place.getRatingCount() - 1;
        double newAvg = newCount == 0 ? 0.0 :
                (place.getRatingAvg() * place.getRatingCount() - oldRating) / newCount;
        place.setRatingCount(newCount);
        place.setRatingAvg(newAvg);
    }

}
