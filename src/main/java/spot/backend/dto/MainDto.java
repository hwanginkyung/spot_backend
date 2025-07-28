package spot.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spot.backend.domain.PlaceList;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MainDto {
    private String folder;
    private PlaceList list;
    private int like;
    private List<String> images;
}
