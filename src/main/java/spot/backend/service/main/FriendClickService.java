package spot.backend.service.main;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.backend.aws.s3.S3Service;
import spot.backend.domain.FriendClickCount;
import spot.backend.dto.main.FriendDto;
import spot.backend.login.memberService.domain.KakaoMem;
import spot.backend.login.memberService.repository.KakaoMemRepository;
import spot.backend.repository.FriendClickCountRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendClickService {

    private final FriendClickCountRepository friendClickCountRepository;
    private final KakaoMemRepository kakaoMemRepository; // 이미 네가 갖고 있다고 했음
    private final S3Service s3Service;

    /**
     * 친구 프로필 클릭 시 호출
     */
    public void handleClick(Long userId, Long friendId) {
        FriendClickCount fcc = friendClickCountRepository.findByUserIdAndFriendId(userId, friendId)
                .orElseGet(() -> FriendClickCount.builder()
                        .userId(userId)
                        .friendId(friendId)
                        .cnt(0L)
                        .build());
        fcc.increment();
        friendClickCountRepository.save(fcc);
    }

    /**
     * 내가 자주 클릭한 TOP 5 친구 불러오기
     */
    @Transactional(readOnly = true)
    public List<FriendDto> getTop5Friends(Long userId, List<KakaoMem> alFriendIds) {

        List<Long> allFriendIds = alFriendIds.stream()
                .map(KakaoMem::getId)   // KakaoMem 객체에서 id만 추출
                .collect(Collectors.toList());
        List<FriendClickCount> topClicks = friendClickCountRepository.findTopFriends(
                userId, allFriendIds, PageRequest.of(0, 5));

        List<Long> topIds = topClicks.stream()
                .map(FriendClickCount::getFriendId)
                .collect(Collectors.toList());

        // 부족할 경우 friendIds에서 채우기
        if (topIds.size() < 5) {
            for (Long id : allFriendIds) {
                if (topIds.size() >= 5) break;
                if (!topIds.contains(id)) topIds.add(id);
            }
        }

        // 멤버 조회
        List<KakaoMem> members = kakaoMemRepository.findByIdIn(topIds);
        Map<Long, KakaoMem> map = members.stream()
                .collect(Collectors.toMap(KakaoMem::getId, m -> m));

        return topIds.stream()
                .map(id -> {
                    KakaoMem m = map.get(id);
                    String profileUrl = s3Service.buildS3ProfileUrl(m.getPhoto());
                    return new FriendDto(id, m.getNickname(), profileUrl);
                })
                .toList();
    }


}
