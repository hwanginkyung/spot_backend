package spot.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spot.backend.dto.CustomUserDetails;
import spot.backend.service.place.BookmarkService;

@RestController
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping("/{placeId}/toggle")
    public String toggleBookmark(@AuthenticationPrincipal CustomUserDetails user,
                                 @PathVariable Long placeId) {
        boolean added = bookmarkService.toggleBookmark(user.getId(), placeId);
        return added ? "북마크 추가됨" : "북마크 해제됨";
    }
}
