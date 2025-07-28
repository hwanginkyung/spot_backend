package spot.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spot.backend.aws.s3.S3Service;
import spot.backend.domain.Place;
import spot.backend.dto.CustomUserDetails;
import spot.backend.dto.MainDto;
import spot.backend.dto.MapDto;
import spot.backend.dto.MapFinalDto;
import spot.backend.repository.PlaceRepository;
import spot.backend.service.FolderImageService;


import java.util.List;

@RestController
@RequiredArgsConstructor
public class HomeController {
    private final S3Service s3Service;
    private final FolderImageService folderImageService;
    private final PlaceRepository placeRepository;

    @GetMapping("/main")
    public ResponseEntity<MapFinalDto> getMyFoldersFull(@AuthenticationPrincipal CustomUserDetails user,
                                                          @RequestParam double lat,
                                                          @RequestParam double lng,
                                                          @RequestParam(defaultValue = "1000") int radius) {
        Long userId = user.getId();
        List<MainDto> response = folderImageService.getUserFoldersImages(userId);

        List<MapDto> nearbyPlaces = placeRepository.findNearbyPlaces(userId, lat, lng, radius);

        MapFinalDto answer= new MapFinalDto(response, nearbyPlaces);

        return ResponseEntity.ok(answer);
    }



}
