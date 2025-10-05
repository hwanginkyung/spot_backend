package spot.backend.aws.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;



import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
    public class S3Service {
        private final S3Client s3Client;
        private final String bucketName = "spottests";
    public List<String> getImageKeysInFolder(String folder) {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(folder + "/") // 폴더 이름 끝에 / 꼭 붙이기
                .maxKeys(3)
                .build();
        ListObjectsV2Response response = s3Client.listObjectsV2(request);
        return response.contents().stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
    }
    public String buildS3ProfileUrl(String profileImageId) {
        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, profileImageId);
    }
    public String buildS3PlaceUrl(String photoId) {
        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, photoId);
    }
    public List<String> getAllImageKeysInFolder(String folder) {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(folder + "/")
                .build();

        ListObjectsV2Response response = s3Client.listObjectsV2(request);

        return response.contents().stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
    }

}
