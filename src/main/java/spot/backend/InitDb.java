package spot.backend;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import spot.backend.domain.Place;
import spot.backend.domain.PlaceList;
import spot.backend.domain.SavedPlace;
import spot.backend.login.memberService.domain.KakaoMem;
import spot.backend.login.memberService.repository.KakaoMemRepository;

public class InitDb {
    @Component
    @RequiredArgsConstructor
    @Transactional
    public class initDb {
        private final InitService initService;
        private final KakaoMemRepository memberRepository;

        @PostConstruct
        public void init() {
            memberRepository.deleteAll();
            initService.dbInit2();
        }

        @Component
        @Transactional
        @RequiredArgsConstructor
        static class InitService {
            private final EntityManager em;

            public void dbInit2() {
                KakaoMem member = new KakaoMem();
                member.setNickname("test2");
                member.setMail("dlsrud0723@naver.com");
                em.persist(member);
                Place place = new Place();
                place.setList(PlaceList.cafe);
                place.setAddress("test");
                place.setLatitude(10.0);
                place.setLongitude(10.0);
                em.persist(place);
                SavedPlace tests = new SavedPlace();
                tests.setUserid(member);
                tests.setPlace(place);
                em.persist(tests);

            }
        }
    }
}
