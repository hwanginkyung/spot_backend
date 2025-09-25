package spot.backend.service.main;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.backend.domain.Friend;
import spot.backend.repository.FriendRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {

    private final FriendRepository friendRepository;

    /**
     * 내가 가진 친구 ID 목록 가져오기
     */
    public List<Long> getFriendIds(Long userId) {
        return friendRepository.findByUserid(userId).stream()
                .map(Friend::getFriendid)
                .collect(Collectors.toList());
    }
}
