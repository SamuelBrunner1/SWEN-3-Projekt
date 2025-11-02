package at.technikum.swen_brunner_wydra.service;

import io.minio.*;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.util.UUID;

@Service
public class StorageService {

    private final MinioClient minio;
    private final String bucket;

    public StorageService(MinioClient minio, @Value("${MINIO_BUCKET:documents}") String bucket) {
        this.minio = minio;
        this.bucket = bucket;
    }

    public void ensureBucket() throws Exception {
        boolean exists = minio.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minio.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    public String putPdf(String originalFilename, InputStream in, long size) throws Exception {
        ensureBucket();
        String key = "%s/%s.pdf".formatted(java.time.LocalDate.now(), UUID.randomUUID());
        minio.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(key)
                        .contentType("application/pdf")
                        .stream(in, size, -1)
                        .build()
        );
        return key;
    }

    public InputStream getObject(String key) throws Exception {
        return minio.getObject(GetObjectArgs.builder().bucket(bucket).object(key).build());
    }

    public String bucket() { return bucket; }
}
