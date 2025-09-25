package spot.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spot.backend.dto.CustomUserDetails;
import spot.backend.dto.main.FriendDto;
import spot.backend.dto.main.HomeDto;
import spot.backend.dto.main.PlaceDto;
import spot.backend.service.main.FriendClickService;
import spot.backend.service.main.FriendService;
import spot.backend.service.place.PlaceService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HomeController {
    //친구 최대 10명,
    private final FriendService friendService;
    private final PlaceService placeService;
    private final FriendClickService friendClickService;
    @GetMapping("/main")
    public HomeDto main(@AuthenticationPrincipal CustomUserDetails user,
                        @RequestParam double distance,
                        @RequestParam double lat,
                        @RequestParam double lng
                        ) {
        Long userId = user.getId();

        // 1. 최근 클릭한 친구 5명 가져오기
        List<Long> allFriendIds = friendService.getFriendIds(userId);
        List<FriendDto> friends = friendClickService.getTop5Friends(userId, allFriendIds);

        // 2. 주변 장소 가져오기
        List<PlaceDto> places = placeService.findPlacesNearbyWithFriends(userId,lat, lng, distance);
        return new HomeDto(friends,places);
    }
}
