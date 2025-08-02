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
import spot.backend.repository.PlaceRepository;
import spot.backend.repository.SavedPlaceRepository;

@Component
    @RequiredArgsConstructor
    @Transactional
    public class initDb {
        private final InitService initService;
        private final KakaoMemRepository memberRepository;
    private final SavedPlaceRepository savedPlaceRepository;
    private final PlaceRepository placeRepository;
    @PostConstruct
        public void init() {
        savedPlaceRepository.deleteAll();
        memberRepository.deleteAll();
        placeRepository.deleteAll();
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
                member.setEmail("dlsrud0723@naver.com");
                member.setKakaoId("1579");
                em.persist(member);
                Place place = new Place();
                place.setList(PlaceList.cafe);
                place.setAddress("test");
                place.setLatitude(10.00);
                place.setLongitude(10.00);
                em.persist(place);
                SavedPlace tests = new SavedPlace(member, place, 5);
                em.persist(tests);
            }
        }
    }
