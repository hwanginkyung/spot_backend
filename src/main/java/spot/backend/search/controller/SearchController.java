package spot.backend.search.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import spot.backend.dto.CustomUserDetails;
import spot.backend.dto.place.RecentSearchDto;
import spot.backend.search.service.RecentSearchService;
import spot.backend.search.service.SearchService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SearchController {
    private final RecentSearchService recentSearchService;
    private final SearchService searchService;
    @PostMapping("/search")
    public Mono<String> search(@RequestBody Map<String, String> body,@AuthenticationPrincipal CustomUserDetails user) {
        String query = body.get("query");
        recentSearchService.saveKeyword(user.getId(), query,0);
        return searchService.search(query);
    }
    @GetMapping("/recent")
    public ResponseEntity<List<RecentSearchDto>> recentSearches(@RequestParam Long userId) {
        List<RecentSearchDto> recent = recentSearchService.getRecentSearches(userId);
        return ResponseEntity.ok(recent);
    }
}