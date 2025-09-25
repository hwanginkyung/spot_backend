package spot.backend;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import spot.backend.domain.Place;
import spot.backend.domain.PlaceList;
import spot.backend.domain.SavedPlace;
import spot.backend.login.memberService.domain.KakaoMem;

@Component
@Transactional
@RequiredArgsConstructor
public class InitService {
    private final EntityManager em;

    public void resetTables() {
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE place").executeUpdate();
        em.createNativeQuery("ALTER TABLE place AUTO_INCREMENT = 1202").executeUpdate();
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }

    public void dbInit2() {
        // 사용자 생성
        KakaoMem member = new KakaoMem();
        member.setNickname("test2");
        member.setEmail("dlsrud0723@naver.com");
        member.setKakaoId("1579");
        em.persist(member);

        KakaoMem members = new KakaoMem();
        members.setNickname("test");
        members.setEmail("dlsrud03@naver.com");
        members.setKakaoId("157");
        em.persist(members);

        // 장소 생성
        Place place1 = Place.builder()
                .list("cafe")
                .address("test")
                .latitude(10.00)
                .longitude(10.00)
                .photo("1202")
                .ratingAvg(0.0)
                .build();
        em.persist(place1);

        Place place2 = Place.builder()
                .list("cafe")
                .address("test1")
                .latitude(10.30)
                .longitude(10.09)
                .photo("photo2")
                .ratingAvg(0.0)
                .build();
        em.persist(place2);

        Place place3 = Place.builder()
                .list("bar")
                .address("test4")
                .latitude(11.00)
                .longitude(11.33)
                .photo("photo3")
                .ratingAvg(0.0)
                .build();
        em.persist(place3);

        // SavedPlace 생성
        em.persist(new SavedPlace(member, place1, 5));
        em.persist(new SavedPlace(member, place2, 1));
        em.persist(new SavedPlace(member, place3, 3));
        em.persist(new SavedPlace(members, place1, 4));
    }
}
