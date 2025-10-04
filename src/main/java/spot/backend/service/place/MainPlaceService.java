package spot.backend.service.place;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spot.backend.aws.s3.S3Service;
import spot.backend.domain.Comment;
import spot.backend.domain.Friend;
import spot.backend.domain.Place;
import spot.backend.domain.SavedPlace;
import spot.backend.dto.main.HomePlaceDto;
import spot.backend.login.memberService.domain.KakaoMem;
import spot.backend.login.memberService.repository.KakaoMemRepository;
import spot.backend.repository.ActivityRepository;
import spot.backend.repository.FriendRepository;
import spot.backend.repository.PlaceRepository;
import spot.backend.repository.SavedPlaceRepository;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static spot.backend.service.place.PlaceService.distance;

@Service
@RequiredArgsConstructor
public class MainPlaceService {

    private final ActivityRepository activityRepository;
    private final PlaceRepository placeRepository;
    private final FriendRepository friendRepository;
    private final S3Service s3Service;
    private final KakaoMemRepository kakaoMemRepository;
    private final SavedPlaceRepository savedPlaceRepository;


    public List<HomePlaceDto> getMainPlaces(Long userId, List<Long> placeIds, double lat, double lng) {

        // 1. 친구 ID 리스트
        List<Long> friendIds = friendRepository.findFriendIds(userId);

        // 2. Place 객체 리스트
        List<Place> places = placeRepository.findAllById(placeIds);

        // 3. 친구 활동 최신순 place
        List<Place> sortedPlaces = activityRepository.findFriendLatestPlaces(friendIds, places);

        // 4. 중복 제거 + 친구 활동 없는 place 포함
        Set<Place> placeSet = new LinkedHashSet<>(sortedPlaces);
        placeSet.addAll(places);
        List<Place> finalPlaces = new ArrayList<>(placeSet);

        // 5. DTO 변환
        return finalPlaces.stream()
                .map(place -> buildDto(userId,place, friendIds, lat, lng))
                .collect(Collectors.toList());
    }

    private HomePlaceDto buildDto(Long userId, Place place, List<Long> friendIds, double userLat, double userLng) {
        KakaoMem me= kakaoMemRepository.findById(userId).orElse(null);
        // 1. 장소 사진
        List<String> photos = s3Service.getImageKeysInFolder(place.getPhoto()).stream()
                .map(s3Service::buildS3PlaceUrl)
                .collect(Collectors.toList());

        // 2. 저장한 사람 중 랜덤 3명 사진
        Set<KakaoMem> myFriendSet = me.getFriends().stream()
                .map(Friend::getFriend)               // Friend → KakaoMem
                .collect(Collectors.toSet());

        // 이 장소를 저장한 사람 중 내 친구만 필터링
        List<String> memPhotos = place.getSavedPlaces().stream()
                .map(SavedPlace::getUser)             // 장소를 저장한 KakaoMem
                .filter(myFriendSet::contains)        // 내 친구인지 확인
                .map(KakaoMem::getPhoto)              // 프로필 사진 추출
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        //3명이상이면 랜덤으로 3명까지만유
        Collections.shuffle(memPhotos);
        if (memPhotos.size() > 3) {
            memPhotos = memPhotos.subList(0, 3);
        }

        // 3. 친구 중 저장한 숫자
        long savedCount = place.getSavedPlaces().stream()
                .map(SavedPlace::getUser)
                .filter(u -> friendIds.contains(u.getId()))
                .count();

        // 4. 랜덤 친구 코멘트 + 코멘트 갯수
        List<Comment> friendComments = place.getComment().stream()
                .filter(c -> friendIds.contains(c.getKakaoMem().getId()))
                .collect(Collectors.toList());

        String comments = "";
        int commentCount = friendComments.size();
        long memId = 0;
        String commentPhoto = null;
        if(!friendComments.isEmpty()) {
            Comment randomComment = friendComments.get(ThreadLocalRandom.current().nextInt(friendComments.size()));
            comments = randomComment.getContent();
            memId = randomComment.getKakaoMem().getId();
            commentPhoto = s3Service.buildS3PlaceUrl(randomComment.getKakaoMem().getPhoto());
        }

        // 5. 거리 계산
        double distance = distance(userLat, userLng, place.getLatitude(), place.getLongitude());

        return HomePlaceDto.builder()
                .id(place.getId())
                .gid(place.getGid())
                .photos(photos)
                .name(place.getName())
                .address(place.getAddress())
                .rating(place.getRatingAvg())
                .ratingCount(place.getRatingCount())
                .category(place.getList())
                .savedCount((int)savedCount)
                .searchCount(place.getSearchCount())
                .score(place.getScore())
                .distance(distance)
                .isMarked(savedPlaceRepository.existsByUserIdAndPlace(userId, place))
                .memPhotos(memPhotos)
                .comment(comments)
                .commentCount(commentCount)
                .memId(memId)
                .commentPhoto(commentPhoto)
                .build();
    }
}
