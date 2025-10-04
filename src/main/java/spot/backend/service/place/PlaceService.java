package spot.backend.service.place;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spot.backend.aws.s3.S3Service;
import spot.backend.domain.Place;
import spot.backend.domain.PlaceArea;
import spot.backend.domain.PlaceList;
import spot.backend.domain.SavedPlace;
import spot.backend.dto.main.PlaceDto;
import spot.backend.dto.place.PlaceDetailDto;
import spot.backend.dto.place.PlaceSaverDto;
import spot.backend.login.memberService.domain.KakaoMem;
import spot.backend.login.memberService.repository.KakaoMemRepository;
import spot.backend.repository.ActivityRepository;
import spot.backend.repository.PlaceAreaRepository;
import spot.backend.repository.PlaceRepository;
import spot.backend.repository.SavedPlaceRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final KakaoMemRepository kakaoMemRepository;
    private final SavedPlaceRepository savedPlaceRepository;
    private final PlaceAreaRepository placeAreaRepository;
    private final S3Service s3Service;

    @Transactional
    public PlaceDetailDto getPlaceDetail(Long placeId, Long userId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("장소 없음"));
        return getPlaceDetails(place,userId);
    }
    public PlaceDetailDto getPlaceDetailBygId(String gId, Long userId) {
        Place place = placeRepository.findByGid(gId)
                .orElseThrow(() -> new IllegalArgumentException("장소 없음"));
        return getPlaceDetails(place,userId);
    }
    @Transactional
    public PlaceDetailDto getPlaceDetails(Place place,Long userId) {
        // 기본 정보
        String gId= place.getGid();
        String name = place.getName();
        String address = place.getAddress();
        double lat =place.getLatitude();
        double lng = place.getLongitude();
        String list = place.getList();
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
                place.getId(),gId, name, address, lat, lng, list, photo,
                ratingAvg, ratingCount, myRating, saverDtos
        );
    }
    @Transactional
    public Place findOrCreatePlace(String name, double lat, double lng) {

        return placeRepository.findByLatitudeAndLongitude(lat, lng)
                .orElseGet(() -> {
                    Place place = Place.builder()
                            .name(name)
                            .latitude(lat)
                            .longitude(lng)
                            .build();
                    // 기타 필드 초기화
                    return placeRepository.save(place);
                });
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return 6371.0 * c*1000;
    }
    public PlaceArea GetPlaceArea(double lat, double lng) {
        // 모든 상권 불러오기
        List<PlaceArea> areas = placeAreaRepository.findAll();

        // 반경 내에 들어가는 첫 번째 상권 찾기
        return areas.stream()
                .filter(area -> distance(lat, lng, area.getLatitude(), area.getLongitude())
                        <= area.getRadiusM())
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public List<PlaceDto> findPlacesNearbyWithFriends(Long userId, double lat, double lng, double distanceM) {

        // 1. 나 + 친구 ID 리스트
        KakaoMem user = kakaoMemRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        List<Long> friendIds = user.getFriends().stream()
                .map(f -> f.getFriend().getId())
                .toList();

        friendIds.add(userId); // 나 포함

        // 2. 해당 유저들의 저장 장소 가져오기
        List<SavedPlace> savedPlaces = savedPlaceRepository.findByUserIdIn(friendIds);

        // 3. 반경 내 필터
        List<SavedPlace> nearbySavedPlaces = savedPlaces.stream()
                .filter(sp -> distance(lat, lng, sp.getPlace().getLatitude(), sp.getPlace().getLongitude()) <= distanceM)
                .toList();

        // 4. Place별로 그룹화 및 DTO 변환
        Map<Long, List<SavedPlace>> groupedByPlace = nearbySavedPlaces.stream()
                .collect(Collectors.groupingBy(sp -> sp.getPlace().getId()));

        List<PlaceDto> result = groupedByPlace.entrySet().stream()
                .map(entry -> {
                    Place p = entry.getValue().get(0).getPlace(); // Place
                    int imageCount = entry.getValue().size();
                    String imageUrl = s3Service.buildS3PlaceUrl(p.getPhoto());
                    double dist = distance(lat, lng, p.getLatitude(), p.getLongitude());

                    return new PlaceDto(
                            p.getId(),
                            p.getName(),
                            imageUrl,
                            imageCount,
                            dist
                    );
                })
                .sorted(Comparator.comparingDouble(PlaceDto::getDistance))
                .toList();

        return result;
    }
}
