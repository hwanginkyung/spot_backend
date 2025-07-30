package spot.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import spot.backend.aws.s3.S3Service;
import spot.backend.dto.CustomUserDetails;
import spot.backend.dto.main.MainDto;
import spot.backend.dto.main.MapDto;
import spot.backend.dto.main.MapFinalDto;
import spot.backend.repository.PlaceRepository;
import spot.backend.service.main.FolderImageService;
import spot.backend.service.main.SavedPlaceService;


import java.util.List;

@RestController
@RequiredArgsConstructor
public class HomeController {
    private final S3Service s3Service;
    private final FolderImageService folderImageService;
    private final PlaceRepository placeRepository;
    private final SavedPlaceService savedPlaceService;

    @GetMapping("/main")
    public ResponseEntity<MapFinalDto> getMyFoldersFull(@AuthenticationPrincipal CustomUserDetails user,
                                                          @RequestParam double latitude,
                                                          @RequestParam double longitude,
                                                          @RequestParam(defaultValue = "1000") int radius) {
        Long userId = user.getId();
        List<MainDto> response = folderImageService.getUserFoldersImages(userId);

        List<MapDto> nearbyPlaces = placeRepository.findNearbyPlaces(userId, latitude, longitude, radius);

        MapFinalDto answer= new MapFinalDto(response, nearbyPlaces);

        return ResponseEntity.ok(answer);
    }
    @PostMapping("/main/bookmark/{placeId}")
    public ResponseEntity<String> addSavedPlace(@AuthenticationPrincipal CustomUserDetails user,
                                              @PathVariable Long placeId) {
        savedPlaceService.addSavedPlace(user.getId(), placeId);
        return ResponseEntity.ok("place saved");
    }

}
