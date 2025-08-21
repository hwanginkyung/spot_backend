package spot.backend.service.main;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import spot.backend.domain.Place;
import spot.backend.domain.SavedPlace;
import spot.backend.dto.place.PopularDto;
import spot.backend.dto.place.SavedPlaceDto;
import spot.backend.login.memberService.domain.KakaoMem;
import spot.backend.login.memberService.repository.KakaoMemRepository;
import spot.backend.repository.PlaceRepository;
import spot.backend.repository.SavedPlaceRepository;
import spot.backend.service.place.BookmarkService;
import spot.backend.service.score.ScoreRedisService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavedPlaceService {

    private final SavedPlaceRepository savedPlaceRepository;
    private final KakaoMemRepository kakaoMemRepository;
    private final PlaceRepository placeRepository;
    private final S3Client s3Client;
    private final String bucketName = "spottests";
    private final ScoreRedisService scoreRedisService;
    private final BookmarkService bookmarkService;
    @Transactional
    public void removeSavedPlace(Long userId, Long placeId) {
        savedPlaceRepository.deleteByUserIdAndPlaceId(userId, placeId);
    }

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
    public List<SavedPlaceDto> getSavedPlaces(Long userId, String sort, Double userLat, Double userLng) {
        List<SavedPlace> savedPlaces = savedPlaceRepository.findByUserIdOrderByCreatedAtDesc(userId);
        // 거리순 정렬
        if ("distance".equalsIgnoreCase(sort) && userLat != null && userLng != null) {
            savedPlaces = savedPlaces.stream()
                    .sorted(Comparator.comparingDouble(sp -> calculateDistance(userLat, userLng,
                            sp.getPlace().getLatitude(), sp.getPlace().getLongitude())))
                    .collect(Collectors.toList());
        }

        return savedPlaces.stream()
                .map(sp -> new SavedPlaceDto(
                        sp.getPlace().getId(),
                        getPhotoUrls(sp.getPlace().getId()), // S3 폴더 안 모든 사진 URL
                        sp.getPlace().getName(),
                        sp.getPlace().getAddress(),
                        sp.getPlace().getRatingAvg(),
                        sp.getPlace().getList().name(),
                        savedPlaceRepository.countByPlaceId(sp.getPlace().getId())
                ))
                .collect(Collectors.toList());
    }
    public List<PopularDto> getPopularPlacesByDistance(Long userId, Double lat, Double lng) {
        List<Place> places = placeRepository.findAll();
        List<PopularDto> dtos = new ArrayList<>();

        for (Place place : places) {
            double distance = calculateDistance(lat, lng, place.getLatitude(), place.getLongitude());
            if (distance > 100) continue;

            boolean marked = savedPlaceRepository.existsByUserIdAndPlace(userId, place);

            double score= calculateScore(place);
            place.setScore(score);
            // photos는 필요에 따라 조회/매핑
            List<String> photos = getPhotoUrls(place.getId());

            dtos.add(new PopularDto(place,  distance,marked,photos));
        }
        dtos.sort(Comparator.comparingDouble(PopularDto::getDistance));
        return dtos;

    }
    private double calculateScore(Place place) {
        int bookmarkDelta = scoreRedisService.getBookmarkDelta(place.getId());
        int searchDelta = scoreRedisService.getSearchDelta(place.getId());
        double ratingDelta = scoreRedisService.getRatingDelta(place.getId());

        double rating = place.getRatingAvg() + ratingDelta;
        int savedCount = place.getSavedCount() + bookmarkDelta;
        int searchCount = place.getSearchCount() + searchDelta;

        return (rating * 10) + (savedCount / 10.0) + (searchCount / 15.0);
    }

    // Haversine formula
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    // S3 폴더 안 모든 객체 URL 가져오기
    private List<String> getPhotoUrls(Long placeId) {
        String folderPrefix =  placeId + "/"; // S3 폴더 경로 예: places/{placeId}/
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(folderPrefix)
                .build();

        return s3Client.listObjectsV2(listRequest)
                .contents()
                .stream()
                .map(obj -> s3Client.utilities().getUrl(b -> b.bucket(bucketName).key(obj.key())).toString())
                .collect(Collectors.toList());
    }
}

