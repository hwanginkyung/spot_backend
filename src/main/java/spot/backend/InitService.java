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
        Place place1 = new Place();
        place1.setList(PlaceList.cafe);
        place1.setAddress("test");
        place1.setLatitude(10.00);
        place1.setLongitude(10.00);
        place1.setPhoto("1202");
        em.persist(place1);

        Place place2 = new Place();
        place2.setList(PlaceList.cafe);
        place2.setAddress("test1");
        place2.setLatitude(10.30);
        place2.setLongitude(10.09);
        place2.setPhoto("photo2");
        em.persist(place2);

        Place place3 = new Place();
        place3.setList(PlaceList.bar);
        place3.setAddress("test4");
        place3.setLatitude(11.00);
        place3.setLongitude(11.33);
        place3.setPhoto("photo3");
        em.persist(place3);

        // SavedPlace 생성
        em.persist(new SavedPlace(member, place1, 5));
        em.persist(new SavedPlace(member, place2, 1));
        em.persist(new SavedPlace(member, place3, 3));
        em.persist(new SavedPlace(members, place1, 4));
    }
}
