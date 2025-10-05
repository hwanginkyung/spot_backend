package spot.backend.service.main;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.backend.domain.Friend;
import spot.backend.domain.FriendStatus;
import spot.backend.login.memberService.domain.KakaoMem;
import spot.backend.login.memberService.repository.KakaoMemRepository;
import spot.backend.repository.FriendRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {

    private final FriendRepository friendRepository;
    private final KakaoMemRepository kakaoMemRepository;
    /**
     * 내가 가진 친구 ID 목록 가져오기
     */
    public List<KakaoMem> getFriendIds(Long userId) {
        KakaoMem me= kakaoMemRepository.findById(userId).orElse(null);
        List<KakaoMem> friends = friendRepository.findByMemberAndStatus(me, FriendStatus.friend)
                .stream()
                .map(Friend::getFriend)
                .collect(Collectors.toList());
        return friends;
    }
}
