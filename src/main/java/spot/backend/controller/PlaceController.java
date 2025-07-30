package spot.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import spot.backend.dto.CustomUserDetails;
import spot.backend.dto.place.PlaceDetailDto;
import spot.backend.dto.place.RatingDto;
import spot.backend.service.place.PlaceService;
import spot.backend.service.place.RatingService;

@RestController
@RequiredArgsConstructor
public class PlaceController {


    private final RatingService ratingService;
    private final PlaceService placeService;
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
}
