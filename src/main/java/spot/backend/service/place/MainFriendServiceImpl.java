package spot.backend.service.place;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spot.backend.aws.s3.S3Service;
import spot.backend.domain.Place;
import spot.backend.dto.main.FriendPlaceDto;
import spot.backend.dto.main.HomeFriendDto;
import spot.backend.login.memberService.domain.KakaoMem;
import spot.backend.login.memberService.repository.KakaoMemRepository;
import spot.backend.repository.CommentRepository;
import spot.backend.repository.PlaceRepository;
import spot.backend.repository.SavedPlaceRepository;
import spot.backend.domain.SavedPlace;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainFriendServiceImpl implements MainFriendService {
    private final S3Service s3Service;
    private final SavedPlaceRepository savedPlaceRepository;
    private final KakaoMemRepository kakaoMemRepository;
    private final PlaceService placeService;
    private final CommentRepository commentRepository;

    @Override
    public HomeFriendDto getFriendPlaces(Long myId, Long friendId, double lat, double lng, double distance) {
        KakaoMem friend = kakaoMemRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("Friend not found"));

        // 2. 친구의 장소 리스트 조회
        List<Place> friendPlaces = savedPlaceRepository.findByUserId(friendId)
                .stream()
                .map(SavedPlace::getPlace)
                .collect(Collectors.toList());

        // 3. 거리 필터링 (lat/lng 기준으로 distance km 이내)
        List<Place> nearbyPlaces = friendPlaces.stream()
                .filter(place -> placeService.distance(lat, lng, place.getLatitude(), place.getLongitude()) <= distance)
                .collect(Collectors.toList());

        KakaoMem fr= kakaoMemRepository.findById(friendId).orElseThrow(() -> new IllegalArgumentException("Friend not found"));
        List<FriendPlaceDto> friendPlaceDtos = nearbyPlaces.stream()
                .map(place -> {
                    int commentCount = commentRepository.countByPlaceAndKakaoMem(place, fr);
                    String photoUrl = s3Service.buildS3PlaceUrl(place.getPhoto());
                    return new FriendPlaceDto(photoUrl, commentCount);
                })
                .collect(Collectors.toList());
        // 4. DTO 변환
        return new HomeFriendDto(
                friend.getSpotNickname(),
                friend.getEmail(),
                friend.getInfo(),
                friendPlaceDtos // List<Place> 형태 그대로 넘겨도 되고, DTO로 매핑해도 됨
        );
    }
}
