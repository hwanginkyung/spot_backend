package spot.backend.search.dto;

public record SearchResultItem(String docId, double score, String title) {

}