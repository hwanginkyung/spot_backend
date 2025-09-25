package spot.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spot.backend.aws.s3.S3Upload;
import spot.backend.domain.Place;
import spot.backend.service.place.GooglePlaceService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spot.backend.service.place.PlaceService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/google")
public class GooglePlaceController {
    private static final Logger log = LoggerFactory.getLogger(GooglePlaceController.class);
    private final GooglePlaceService googlePlaceService;
    private final S3Upload s3Upload;
    private final PlaceService placeService;

    @PostMapping("/upload-photo")
    public ResponseEntity<List<String>> uploadPhoto(
            @RequestParam String name,
            @RequestParam double lat,
            @RequestParam double lng) throws IOException {
        String placeId = null;
        // 1. 장소 이름 + 위치로 place_id
        try {
            placeId = googlePlaceService.searchPlaceIdByNameAndLocation(name, lat, lng,500);
            // 이후 처리
        } catch (IllegalStateException e) {
            log.error("Google Place ID 검색 실패: {}", e.getMessage());
            throw e;  // 또는 적절한 커스텀 예외로 변환해도 좋습니다.
        }
        Place place = placeService.findOrCreatePlace(name, lat, lng);
        // 2. 사진 참조 얻기
        List<String> photoRefs = googlePlaceService.getPhotoReferences(placeId);
        if (photoRefs.isEmpty()) {
            log.warn("photo_reference 미발견 - placeId={}", placeId);
            throw new IllegalStateException("photo_reference not found");
        }

        List<String> imageUrls = new ArrayList<>();
        for (String photoRef : photoRefs) {
            // 3. 사진 다운로드
            byte[] photoBytes = googlePlaceService.downloadPlacePhoto(photoRef, 800);

            // 4. S3 업로드
            String filename =  UUID.randomUUID() + ".jpg";
            String imageUrl = s3Upload.upload(photoBytes, place.getGid()+"/"+filename, "image/jpeg");
            imageUrls.add(imageUrl);
        }

        return ResponseEntity.ok(imageUrls);
    }
}

