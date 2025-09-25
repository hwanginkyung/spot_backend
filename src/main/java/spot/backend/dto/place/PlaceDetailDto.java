package spot.backend.dto.place;

import lombok.Getter;
import spot.backend.domain.PlaceList;

import java.util.List;


public record PlaceDetailDto (
    Long placeId,
    String gId,
    String name,
    String address,
    double latitude,
    double longitude,
    String list,
    String photo,
    double ratingAvg,
    int ratingCount,
    Integer myRating,      // nullable
    List<PlaceSaverDto> savers

){
    public static PlaceDetailDto empty(
            Long placeId,
            String gId,
            String name,
            String address,
            double latitude,
            double longitude,
            String photo,
            String list
    ) {
        return new PlaceDetailDto(
                placeId,      // DB에 없으면 null
                gId,          // 검색한 gId
                name,         // 가져온 이름
                address,      // 가져온 주소
                latitude,     // 가져온 위도
                longitude,    // 가져온 경도
                null,         // PlaceList는 null
                photo,        // 사진 URL
                0.0,          // ratingAvg 기본값
                0,            // ratingCount 기본값
                null,         // myRating null
                List.of()     // saver 목록 empty
        );
    }
}
