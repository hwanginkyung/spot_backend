package spot.backend.search.dto;


import java.util.List;

public record SearchResponse(List<SearchResultItem> results) { }
