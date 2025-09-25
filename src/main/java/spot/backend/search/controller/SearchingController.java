package spot.backend.search.controller;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import spot.backend.dto.CustomUserDetails;
import spot.backend.dto.place.PlaceDetailDto;
import spot.backend.repository.PlaceRepository;
import spot.backend.scheduler.RecentSearchScheduler;
import spot.backend.search.dto.GoogleDto;
import spot.backend.search.service.RecentSearchService;
import spot.backend.service.place.GooglePlaceService;
import spot.backend.service.place.PlaceService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class SearchingController {

    private final GooglePlaceService googlePlaceService;
    private final RecentSearchService recentSearchService;
    private final PlaceService placeService;

    @GetMapping("/search")
    public ResponseEntity<List<GoogleDto>> getHomePlaces(
            @RequestParam String keyword,
            @RequestParam double lat,
            @RequestParam double lng) {

        List<GoogleDto> places = googlePlaceService.searchPlaces(keyword,lat, lng, 10,10000);
        return ResponseEntity.ok(places);
    }
    @GetMapping("/test")
    public ResponseEntity<JsonNode> testGoogle(
            @RequestParam String query,
            @RequestParam double lat,
            @RequestParam double lng
    ) {
        JsonNode response = googlePlaceService.testplace(query, lat, lng, 5000);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/serach/detail")
    public ResponseEntity<PlaceDetailDto> getPlaceDetail(@AuthenticationPrincipal CustomUserDetails user,
                                                         @RequestParam String gid,
                                                         @RequestParam double lat,
                                                         @RequestParam double lng){
        Long userId=user.getId();
        PlaceDetailDto result;
        try {
            // DB에 있으면 서비스 메서드로 변환
            result = placeService.getPlaceDetailBygId(gid, userId);
        } catch (IllegalArgumentException e) {
            // DB에 없으면 Google API에서 place 정보 가져와서 empty DTO 생성
            GoogleDto googleDto = googlePlaceService.searchPlaceByGId(gid, lat, lng);
            result = PlaceDetailDto.empty(
                    null,                       // placeId 없음
                    gid,                        // 오타 수정: gId → gid
                    googleDto.name(),
                    googleDto.address(),
                    googleDto.latitude(),
                    googleDto.longitude(),
                    googleDto.photoUrl(),
                    googleDto.category()
            );
        }
        recentSearchService.saveKeyword(userId, result.name(), 0);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/search/details")
    public ResponseEntity<List<PlaceDetailDto>> getPlaceDetails(@AuthenticationPrincipal CustomUserDetails user,
                                                                @RequestParam String keyword,
                                                                @RequestParam double lat,
                                                                @RequestParam double lng){
        Long userId= user.getId();
        List<String> gIds = googlePlaceService.searchGIds(keyword,lat, lng, 4,10000);
        List<PlaceDetailDto> results = gIds.stream()
                .map(gId -> {
                    try {
                        // DB에 있으면 서비스 메서드로 변환
                        return placeService.getPlaceDetailBygId(gId, userId);
                    } catch (IllegalArgumentException e) {
                        // DB에 없으면 Google API에서 place 정보 가져와서 empty DTO 생성
                        GoogleDto googleDto = googlePlaceService.searchPlaceByGId(gId,lat,lng); // searchPlaceById는 이름/주소/사진/위치 반환
                        return PlaceDetailDto.empty(
                                null,                       // placeId 없음
                                gId,
                                googleDto.name(),
                                googleDto.address(),
                                googleDto.latitude(),
                                googleDto.longitude(),
                                googleDto.photoUrl(),
                                googleDto.category()
                        );
                    }
                })
                .toList();
        recentSearchService.saveKeyword(userId, keyword, 0);
        return ResponseEntity.ok(results);
    }
}
