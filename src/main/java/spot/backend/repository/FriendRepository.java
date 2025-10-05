package spot.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spot.backend.domain.Friend;
import spot.backend.domain.FriendStatus;
import spot.backend.login.memberService.domain.KakaoMem;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    // 내가 추가한 친구들 전체
    List<Friend> findByMember(KakaoMem member);

    // 상태별 친구 (예: 수락된 친구만)
    List<Friend> findByMemberAndStatus(KakaoMem member, FriendStatus status);

    // 내가 가진 친구들의 id만 리스트로 가져오기
    @Query("SELECT f.friend.id FROM Friend f WHERE f.member.id = :userId AND f.status = 'FRIEND'")
    List<Long> findFriendIds(@Param("userId") Long userId);
}
