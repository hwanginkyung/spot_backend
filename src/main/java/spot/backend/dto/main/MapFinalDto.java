package spot.backend.dto.main;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapFinalDto {
        private List<MainDto> saveplace;
        private List<MapDto> maps;
}
