package spot.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spot.backend.aws.s3.S3Service;
import spot.backend.domain.Place;
import spot.backend.domain.PlaceList;
import spot.backend.dto.MainDto;
import spot.backend.login.memberService.domain.KakaoMem;
import spot.backend.login.memberService.repository.KakaoMemRepository;
import spot.backend.repository.PlaceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FolderImageService {

    private final PlaceRepository placeRepository;
    private final S3Service s3Service;
    private final KakaoMemRepository kakaoMemRepository;

    public List<MainDto> getUserFoldersImages(Long userId) {
        // 1. 유저 정보 조회 (ex: userRepository.findById(userId))
        KakaoMem user = kakaoMemRepository.findById(userId).orElseThrow();
        List<Place> places = placeRepository.findFolderNamesByUserId(userId);
        List<MainDto> result = new ArrayList<>();
        // 3. 각 장소의 폴더명을 이용해 S3 이미지 키 조회
        for (Place place : places) {
            String folder = place.getAddress();
            PlaceList list = place.getList();
            List<String> images = s3Service.getImageKeysInFolder(folder);
            MainDto dto = new MainDto(
                    folder,
                    list,
                    place.getPlaceLike().size(),
                    images
            );
            result.add(dto);
        }
        return result;
    }
}

