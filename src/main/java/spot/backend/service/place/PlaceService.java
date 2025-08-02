package spot.backend.service.place;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spot.backend.domain.Place;
import spot.backend.domain.PlaceList;
import spot.backend.domain.SavedPlace;
import spot.backend.dto.place.PlaceDetailDto;
import spot.backend.dto.place.PlaceSaverDto;
import spot.backend.login.memberService.domain.KakaoMem;
import spot.backend.login.memberService.repository.KakaoMemRepository;
import spot.backend.repository.PlaceRepository;
import spot.backend.repository.SavedPlaceRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final KakaoMemRepository kakaoMemRepository;
    private final SavedPlaceRepository savedPlaceRepository;

    @Transactional
    public PlaceDetailDto getPlaceDetail(Long placeId, Long userId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("장소 없음"));

        // 기본 정보
        String name = place.getName();
        String address = place.getAddress();
        double lat =roundTo2Decimals(place.getLatitude());
        double lng = roundTo2Decimals(place.getLongitude());
        PlaceList list = place.getList();
        String photo = place.getPhoto();

        // 유저 관련 정보
        KakaoMem user = kakaoMemRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        Optional<SavedPlace> saved = savedPlaceRepository.findByUserAndPlace(user, place);

        int ratingCount = place.getRatingCount();
        double ratingAvg = place.getRatingAvg();
        Integer myRating = saved.map(SavedPlace::getRating).orElse(null);

        List<SavedPlace> recentSavers = savedPlaceRepository.findTop2ByPlaceOrderByCreatedAtDesc(place);

        List<PlaceSaverDto> saverDtos = recentSavers.stream()
                .map(sp -> new PlaceSaverDto(
                        sp.getUser().getNickname(),
                        sp.getUser().getPhoto()
                ))
                .toList();
        return new PlaceDetailDto(
                place.getId(), name, address, lat, lng, list, photo,
                ratingAvg, ratingCount, myRating, saverDtos
        );
    }
    public Place findOrCreatePlace(String name, double lat, double lng) {
        double roundedLat = roundTo2Decimals(lat);
        double roundedLng = roundTo2Decimals(lng);

        return placeRepository.findByLatitudeAndLongitude(roundedLat, roundedLng)
                .orElseGet(() -> {
                    Place place = new Place();
                    place.setName(name);
                    place.setLatitude(roundedLat);
                    place.setLongitude(roundedLng);
                    // 기타 필드 초기화
                    return placeRepository.save(place);
                });
    }

    private double roundTo2Decimals(double value) {
        return Math.round(value * 100) / 100.0;
    }

}
