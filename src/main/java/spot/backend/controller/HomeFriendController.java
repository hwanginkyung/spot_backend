package spot.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spot.backend.dto.CustomUserDetails;
import spot.backend.dto.main.HomeCommentDto;
import spot.backend.dto.main.HomeFriendDto;
import spot.backend.dto.main.HomePlaceDto;
import spot.backend.service.place.MainFriendService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HomeFriendController {


    private final MainFriendService mainFriendService;

    @GetMapping("/main/{user_id}")
    public HomeFriendDto mainFriend(@AuthenticationPrincipal CustomUserDetails user,
                                          @PathVariable long user_id,
                                          @RequestParam double lat,
                                          @RequestParam double lng,
                                          @RequestParam double distance

    ) {
        Long userId = user.getId();
        HomeFriendDto gogo = mainFriendService.getFriendPlaces(userId, user_id,lat, lng,distance);
        return gogo;
    }
}
