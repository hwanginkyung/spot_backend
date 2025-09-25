package spot.backend.search.dto;

public record GoogleDto (
        String name,
        String address,
        String gid,
        String photoUrl,
        String category,
        double distance,
        double latitude,
        double longitude

    ){
}
