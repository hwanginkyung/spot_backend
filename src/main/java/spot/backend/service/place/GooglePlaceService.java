package spot.backend.service.place;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import spot.backend.search.dto.GoogleDto;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GooglePlaceService {

    private final WebClient webClient;

    public GooglePlaceService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://maps.googleapis.com").build();
    }
    @Value("${google.apikey}")
    private String apiKey;
    private static final Logger log = LoggerFactory.getLogger(GooglePlaceService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<String> searchGIds(String names, double lat, double lng, int limit, int radius) {
        Map<String, Object> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/maps/api/place/textsearch/json")
                        .queryParam("query", names)
                        .queryParam("location", lat + "," + lng)
                        .queryParam("radius", radius)
                        .queryParam("language", "ko")
                        .queryParam("key", apiKey)
                        .build()
                )
                .retrieve()
                .bodyToMono(Map.class)
                .block(); // 동기 호출

        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
        if (results == null) return Collections.emptyList();

        return results.stream()
                .limit(limit)
                .map(r -> (String) r.get("place_id")) //  place_id만 추출
                .collect(Collectors.toList());
    }
    public JsonNode testplace(String query, double lat, double lng, int radius) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/maps/api/place/textsearch/json")
                        .queryParam("query", query)
                        .queryParam("location", lat + "," + lng)
                        .queryParam("radius", radius)
                        .queryParam("language", "ko")
                        .queryParam("key", apiKey)
                        .build()
                )
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }
    public GoogleDto searchPlaceByGId(String placeId, double lats, double lngs) {
        String url = String.format(
                "/maps/api/place/details/json?place_id=%s&fields=name,formatted_address,geometry,photos&language=ko&key=%s",
                placeId, apiKey
        );

        Map<String, Object> response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || response.get("result") == null) {
            throw new IllegalStateException("Google Place API 결과 없음: " + placeId);
        }

        Map<String, Object> result = (Map<String, Object>) response.get("result");

        // 이름, 주소
        String name = (String) result.get("name");
        String address = (String) result.get("formatted_address");
        String category = (String) result.get("category");

        // 위도/경도
        Map<String, Object> geometry = (Map<String, Object>) result.get("geometry");
        Map<String, Object> location = (Map<String, Object>) geometry.get("location");
        double latitude = (double) location.get("lat");
        double longitude = (double) location.get("lng");

        // 사진 URL (한 장만)
        String photoUrl = null;
        List<Map<String, Object>> photos = (List<Map<String, Object>>) result.get("photos");
        if (photos != null && !photos.isEmpty()) {
            String photoReference = (String) photos.get(0).get("photo_reference");
            photoUrl = "https://maps.googleapis.com/maps/api/place/photo"
                    + "?maxwidth=400"
                    + "&photoreference=" + photoReference
                    + "&key=" + apiKey;
        }
        double distance = calculateDistance(lats, lngs, latitude, longitude);
        return new GoogleDto(name, address, placeId, photoUrl, category, distance, latitude, longitude);
    }
    public List<GoogleDto> searchPlaces(String names, double lat, double lng, int limit,int radius) {
        Map<String, Object> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/maps/api/place/textsearch/json")
                        .queryParam("query", names)
                        .queryParam("location", lat + "," + lng)
                        .queryParam("radius", radius) //
                        .queryParam("language", "ko")
                        .queryParam("key", apiKey)
                        .build()
                )
                .retrieve()
                .bodyToMono(Map.class)
                .block(); // 동기 호출
        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
        if (results == null) return Collections.emptyList();

        return results.stream()
                .limit(limit)
                .map(r -> {
                    String photoUrl = null;
                    List<Map<String, Object>> photos = (List<Map<String, Object>>) r.get("photos");
                    if (photos != null && !photos.isEmpty()) {
                        String photoReference = (String) photos.get(0).get("photo_reference");
                        photoUrl = "https://maps.googleapis.com/maps/api/place/photo"
                                + "?maxwidth=400"
                                + "&photoreference=" + photoReference
                                + "&key=" + apiKey;
                    }
                    // 카테고리 (types 배열 중 첫 번째만 사용)
                    List<String> types = (List<String>) r.get("types");
                    String category = (types != null && !types.isEmpty()) ? types.get(0) : null;

                    Map<String, Object> geometry = (Map<String, Object>) r.get("geometry");
                    Map<String, Object> location = (Map<String, Object>) geometry.get("location");
                    double placeLat = (double) location.get("lat");
                    double placeLng = (double) location.get("lng");

                    double distance = calculateDistance(lat, lng, placeLat, placeLng);
                    return new GoogleDto(
                            (String) r.get("name"),
                            (String) r.get("formatted_address"),
                            (String) r.get("place_id"),
                            photoUrl,
                            category,
                            distance,
                            placeLat,
                            placeLng
                    );
                })
                .collect(Collectors.toList());
    }
    // 1. 이름 + 위도/경도로 place_id 찾기
    public String searchPlaceIdByNameAndLocation(String name, double lat, double lng, int radius) throws IOException {
        String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);
        String url = String.format(
                "https://maps.googleapis.com/maps/api/place/textsearch/json?query=%s&location=%f,%f&radius=%d&language=ko&key=%s",
                encodedName, lat, lng, radius, apiKey
        );

        log.info("Google Places API 호출 URL: {}", url);
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");

        try (InputStream is = conn.getInputStream()) {
            JsonNode root = objectMapper.readTree(is);
            log.warn("검색 실패 - name={}, lat={}, lng={}, rawResponse={}", name, lat, lng, root.toPrettyString());

            JsonNode results = root.path("results");
            if (results.isArray() && results.size() > 0) {
                return results.get(0).path("place_id").asText();
            } else {
                log.warn("place_id 미발견 - name={}, lat={}, lng={}, 응답 결과 없음", name, lat, lng);
                throw new IllegalStateException("place_id not found for name=" + name + ", lat=" + lat + ", lng=" + lng);
            }
        }
    }

    // 2. place_id로 photo_reference 얻기
    public List<String> getPhotoReferences(String placeId) throws IOException {
        String url = String.format(
                "https://maps.googleapis.com/maps/api/place/details/json?place_id=%s&fields=photos&key=%s",
                placeId, apiKey
        );

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");

        try (InputStream is = conn.getInputStream()) {
            JsonNode root = objectMapper.readTree(is);
            JsonNode photos = root.path("result").path("photos");
            List<String> photoRefs = new ArrayList<>();
            if (photos.isArray() && photos.size() > 0) {
                int limit = Math.min(photos.size(),5);
                for (int i = 0; i < limit; i++) {
                    photoRefs.add(photos.get(i).path("photo_reference").asText());
                }
            }
            return photoRefs;
        }
    }

    // 3. 사진 다운로드
    public byte[] downloadPlacePhoto(String photoRef, int maxWidth) throws IOException {
        String url = String.format(
                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=%d&photoreference=%s&key=%s",
                maxWidth, photoRef, apiKey
        );

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");

        try (InputStream is = conn.getInputStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }

            return baos.toByteArray();
        }
    }
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // meter 단위
    }
}

