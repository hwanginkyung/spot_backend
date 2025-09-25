package spot.backend.dto.place;

import lombok.Getter;
import spot.backend.domain.Place;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PopularDto {
    private long id;
    private List<String> photos;     // 장소 사진
    private String name;        // 상호명
    private String address;     // 주소
    private double rating;      // 평점
    private String category;    // 업종
    private int savedCount;
    private int searchCount;
    private double score;
    private double distance;
    private boolean isMarked;

    public PopularDto(Place place, double distance, boolean isMarked, List<String> photos) {
        this.id = place.getId();
        this.photos = photos;
        this.name = place.getName();
        this.address = place.getAddress();
        this.rating = place.getRatingAvg();
        this.category = place.getList();
        this.distance = distance;
        this.isMarked = isMarked;
        this.savedCount = place.getSavedCount();
        this.searchCount = place.getSearchCount();
        this.score = place.getScore();
    }
}
