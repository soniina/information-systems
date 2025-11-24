package itmo.sonina.creaturecatalog.services

import io.minio.*
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class MinioService(
    private val minioClient: MinioClient,
    @Value("\${minio.bucket-name}") private val bucketName: String
) {

    @PostConstruct
    fun init() {
        val found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build())
        }
    }

    fun uploadFile(filename: String, inputStream: InputStream, contentType: String) {
        try {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(filename)
                    .stream(inputStream, -1, 10485760)
                    .contentType(contentType)
                    .build()
            )
        } catch (e: Exception) {
            throw RuntimeException("Ошибка загрузки файла в MinIO: ${e.message}")
        }
    }

    fun downloadFile(filename: String): InputStream {
        return minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(bucketName)
                .`object`(filename)
                .build()
        )
    }

    fun deleteFile(filename: String) {
        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucketName)
                .`object`(filename)
                .build()
        )
    }
}