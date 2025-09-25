package spot.backend;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import spot.backend.login.memberService.repository.KakaoMemRepository;
import spot.backend.repository.PlaceRepository;
import spot.backend.repository.RecentSearchRepository;
import spot.backend.repository.SavedPlaceRepository;
import spot.backend.repository.UrlPlaceRepository;

@Component
@RequiredArgsConstructor
@Transactional
public class InitDb {

    private final InitService initService;
    private final KakaoMemRepository memberRepository;
    private final SavedPlaceRepository savedPlaceRepository;
    private final PlaceRepository placeRepository;
    private final UrlPlaceRepository urlPlaceRepository;
    private final RecentSearchRepository recentSearchRepository;

    @PostConstruct
    public void init() {
        savedPlaceRepository.deleteAll();
        urlPlaceRepository.deleteAll(); // saved_place 삭제
        recentSearchRepository.deleteAll();
        memberRepository.deleteAll();

        // 2. place 삭제
        placeRepository.deleteAll();

        // 3. 초기화 서비스 실행
        initService.resetTables();
        initService.dbInit2();
    }
}
