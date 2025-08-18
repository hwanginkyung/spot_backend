package spot.backend.search.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import spot.backend.search.service.SearchService;

import java.util.Map;

@RestController
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping
    public Mono<String> search(@RequestBody Map<String, String> body) {
        String query = body.get("query");
        return searchService.search(query);
    }
}