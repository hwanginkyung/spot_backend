package spot.backend.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spot.backend.domain.Comment;
import spot.backend.domain.Place;
import spot.backend.login.memberService.domain.KakaoMem;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {


    @Query("SELECT c FROM Comment c " +
            "JOIN FETCH c.place p " +
            "JOIN FETCH c.kakaoMem km " +
            "WHERE c.kakaoMem.id IN :friendIds " +
            "AND p.id IN :placeIds " +
            "ORDER BY c.createdAt DESC")
    List<Comment> findFriendCommentsByPlaces(
            @Param("friendIds") List<Long> friendIds,
            @Param("placeIds") List<Long> placeIds
    );
    int countByPlaceAndKakaoMem(Place place, KakaoMem kakaoMem);
}
