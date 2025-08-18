package spot.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import spot.backend.dto.CustomUserDetails;
import spot.backend.dto.place.PlaceDetailDto;
import spot.backend.dto.place.PopularDto;
import spot.backend.dto.place.RatingDto;
import spot.backend.dto.place.SavedPlaceDto;
import spot.backend.service.main.SavedPlaceService;
import spot.backend.service.place.PlaceService;
import spot.backend.service.place.RatingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlaceController {

    private final RatingService ratingService;
    private final PlaceService placeService;
    private final SavedPlaceService savedPlaceService;
    @PostMapping("/places/ratings")
    public ResponseEntity<String> addRating(@AuthenticationPrincipal CustomUserDetails user,
                                            @RequestBody @Valid RatingDto request){
        ratingService.giveRating(user.getId(), request);
        return ResponseEntity.ok("Success");
    }
    @DeleteMapping("/places/{place_id}/ratings")
    public ResponseEntity<String> deleteRating(@AuthenticationPrincipal CustomUserDetails user,
                                               @PathVariable long place_id ){
        ratingService.removeRating(user.getId(), place_id);
        return ResponseEntity.ok("Success");
    }
    @GetMapping("/places/{place_id}")
    public ResponseEntity<PlaceDetailDto> placesDetail(@AuthenticationPrincipal CustomUserDetails user,
                                                       @PathVariable long place_id ){

        Long userId = user.getId();
        return ResponseEntity.ok(placeService.getPlaceDetail(place_id, userId));
    }
    /*@GetMapping("/my")
    public List<SavedPlaceDto> getPlaces(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng
    ) {
        return savedPlaceService.getSavedPlaces(user.getId(), sort, lat, lng);
    }*/
    @GetMapping("/my")
    public List<SavedPlaceDto> getPlaces(
            @RequestParam(name = "userId", required = true) Long userId,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng
    ) {
        return savedPlaceService.getSavedPlaces(userId, sort, lat, lng);
    }
    @DeleteMapping("/main/{placeId}")
    public ResponseEntity<Void> removeSavedPlace(
            @RequestParam Long memberId,
            @PathVariable Long placeId
    ) {
        savedPlaceService.removeSavedPlace(memberId, placeId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/popular")
    public List<PopularDto> getPopularPlaces(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam double lat,
            @RequestParam double lng) {
        return savedPlaceService.getPopularPlacesByDistance(user.getId(), lat, lng);
    }
}
