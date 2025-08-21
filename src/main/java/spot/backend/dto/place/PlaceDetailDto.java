package spot.backend.dto.place;

import lombok.Getter;
import spot.backend.domain.PlaceList;

import java.util.List;

@Getter
public record PlaceDetailDto (
    Long placeId,
    String name,
    String address,
    double latitude,
    double longitude,
    PlaceList list,
    String photo,
    double ratingAvg,
    int ratingCount,
    Integer myRating,      // nullable
    List<PlaceSaverDto> savers
){}
