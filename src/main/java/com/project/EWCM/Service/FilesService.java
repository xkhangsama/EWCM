package com.project.EWCM.Service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class FilesService {
    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public String uploadFile(MultipartFile file, String folderPath) {
        try {
            String fileName = folderPath + "/" + file.getOriginalFilename();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return fileName; // Trả về đường dẫn lưu file trong bucket
        } catch (Exception e) {
            throw new RuntimeException("Error while uploading file.", e);
        }
    }

    public InputStream getFile(String filePath) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filePath)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching file from MinIO", e);
        }
    }
}
