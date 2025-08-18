package spot.backend.search.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Service
public class SearchService {


    private final WebClient webClient;

    public SearchService(WebClient webClient) {
        this.webClient = webClient.mutate()
                .baseUrl("http://3.39.241.53:5000")
                .build();
    }

    public Mono<String> search(String query) {
        return webClient.post()
                .uri("/search")
                .bodyValue(Map.of("query", query))
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Mono.just("[]"); // 에러 시 빈 리스트 반환
                });
    }

}

