package spot.backend;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import spot.backend.login.memberService.repository.KakaoMemRepository;
import spot.backend.repository.PlaceRepository;
import spot.backend.repository.SavedPlaceRepository;

@Component
@RequiredArgsConstructor
@Transactional
public class InitDb {

    private final InitService initService;
    private final KakaoMemRepository memberRepository;
    private final SavedPlaceRepository savedPlaceRepository;
    private final PlaceRepository placeRepository;

    @PostConstruct
    public void init() {
        savedPlaceRepository.deleteAll();
        memberRepository.deleteAll();
        placeRepository.deleteAll();

        initService.resetTables();
        initService.dbInit2();
    }
}
