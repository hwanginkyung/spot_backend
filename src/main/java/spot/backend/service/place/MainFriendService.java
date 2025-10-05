package spot.backend.service.place;

import spot.backend.dto.main.HomeFriendDto;

public interface MainFriendService {
    HomeFriendDto getFriendPlaces(Long myId, Long friendId, double lat, double lng, double distance);
}
