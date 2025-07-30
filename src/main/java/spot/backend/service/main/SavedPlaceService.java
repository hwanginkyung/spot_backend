package spot.backend.service.main;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spot.backend.domain.Place;
import spot.backend.domain.SavedPlace;
import spot.backend.login.memberService.domain.KakaoMem;
import spot.backend.login.memberService.repository.KakaoMemRepository;
import spot.backend.repository.PlaceRepository;
import spot.backend.repository.SavedPlaceRepository;

@Service
@RequiredArgsConstructor
public class SavedPlaceService {

    private final SavedPlaceRepository savedPlaceRepository;
    private final KakaoMemRepository kakaoMemRepository;
    private final PlaceRepository placeRepository;

    @Transactional
    public void addSavedPlace(Long userId, Long placeId) {
        KakaoMem user = kakaoMemRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new RuntimeException("Place not found"));

        // 중복 방지
        savedPlaceRepository.findByUserAndPlace(user, place)
                .ifPresent(b -> { throw new RuntimeException("Already bookmarked"); });

        SavedPlace savedPlace = new SavedPlace(user,place);
        savedPlaceRepository.save(savedPlace);
    }
}

