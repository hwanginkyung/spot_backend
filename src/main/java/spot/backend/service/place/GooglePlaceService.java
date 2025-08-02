package spot.backend.service.place;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GooglePlaceService {

    @Value("${google.apikey}")
    private String apiKey;
    private static final Logger log = LoggerFactory.getLogger(GooglePlaceService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 1. 이름 + 위도/경도로 place_id 찾기
    public String searchPlaceIdByNameAndLocation(String name, double lat, double lng) throws IOException {
        String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);
        String url = String.format(
                "https://maps.googleapis.com/maps/api/place/textsearch/json?query=%s&location=%f,%f&radius=500&key=%s",
                encodedName, lat, lng, apiKey
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
                int limit = Math.min(photos.size(), 5);
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
}

