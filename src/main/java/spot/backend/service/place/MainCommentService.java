package spot.backend.service.place;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spot.backend.aws.s3.S3Service;
import spot.backend.domain.Comment;
import spot.backend.domain.FriendStatus;
import spot.backend.dto.CustomUserDetails;
import spot.backend.dto.main.HomeCommentDto;
import spot.backend.login.memberService.domain.KakaoMem;
import spot.backend.login.memberService.repository.KakaoMemRepository;
import spot.backend.repository.CommentRepository;
import spot.backend.repository.FriendRepository;
import spot.backend.repository.SavedPlaceRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainCommentService {

    private final KakaoMemRepository kakaoMemRepository;
    private final FriendRepository friendRepository;
    private final CommentRepository commentRepository;
    private final S3Service s3Service;
    private final SavedPlaceRepository savedPlaceRepository;
    public List<HomeCommentDto> homeComment(CustomUserDetails user,
                                            List<Long> placeIds,
                                            int page,
                                            int size) {

        KakaoMem me = kakaoMemRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));


        // 내 친구 ID 목록 가져오기
        List<Long> friendIds = friendRepository.findByMemberAndStatus(me, FriendStatus.friend)
                .stream()
                .map(f -> f.getFriend().getId())
                .collect(Collectors.toList());

        // 친구가 작성한 댓글만 조회 (최신순)
        List<Comment> comments = commentRepository.findFriendCommentsByPlaces(friendIds, placeIds);

        // 3️⃣ 즐겨찾기 여부 한번에 가져오기
        List<Long> commentPlaceIds = comments.stream()
                .map(c -> c.getPlace().getId())
                .distinct()
                .toList();

        Map<Long, Boolean> isMarkedMap = savedPlaceRepository.findByUserIdAndPlaceIds(me.getId(), commentPlaceIds)
                .stream()
                .collect(Collectors.toMap(sp -> sp.getPlace().getId(), sp -> true));

        // DTO 매핑
        List<HomeCommentDto> dtoList = comments.stream()
                .map(c -> new HomeCommentDto(
                        c.getId(),                               // commentId
                        c.getPlace().getId(),                    // placeId
                        c.getPlace().getGid(),                   // gid
                                       // placePhotos
                        s3Service.getAllImageKeysInFolder(c.getPlace().getPhoto()),
                        c.getPlace().getName(),                  // placeName
                        c.getPlace().getAddress(),               // address
                        c.getPlace().getScore(),                 // score
                        isMarkedMap.getOrDefault(c.getPlace().getId(), false), // isMarked
                        c.getContent(),                          // commentText
                        c.getKakaoMem().getId(),                 // memId
                        c.getKakaoMem().getEmail(),              // memEmail// commentPhoto
                        s3Service.buildS3PlaceUrl(c.getKakaoMem().getPhoto()),
                        c.getCreatedAt()                         // createdAt
                ))
                .toList();

        // 5️⃣ 페이징 처리 (subList로 간단하게)
        int fromIndex = Math.min(page * size, dtoList.size());
        int toIndex = Math.min(fromIndex + size, dtoList.size());
        return dtoList.subList(fromIndex, toIndex);
    }

}
