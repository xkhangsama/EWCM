package com.project.EWCM.Service;

import com.project.EWCM.Controller.UnitController;
import com.project.EWCM.DTO.FileDownloadResponseDto;
import com.project.EWCM.DTO.UploadFileResponseDto;
import com.project.EWCM.Document.Account;
import com.project.EWCM.Document.File;
import com.project.EWCM.Document.Unit;
import com.project.EWCM.exception.HttpException;
import com.project.EWCM.repository.FileRepository;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service
public class FilesService {
    @Autowired
    private MinioClient minioClient;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UnitService unitService;

    @Value("${minio.bucket-name}")
    private String bucketName;

    Logger logger = LoggerFactory.getLogger(FilesService.class);

    public UploadFileResponseDto uploadFile(MultipartFile file, String folderPath, String username, String unitId) {
        try {
            UploadFileResponseDto result = new UploadFileResponseDto();
            Date currentDate = new Date();
            String filePath = folderPath + "/" + file.getOriginalFilename();
            String fileName = file.getOriginalFilename();
            String extension = "";
            if (fileName != null && fileName.contains(".")) {
                extension = fileName.substring(fileName.lastIndexOf(".") + 1);
            }
            String mimeType = file.getContentType();
            long size = file.getSize();

            Account account = accountService.findAccountByUsername(username);
            Unit unit = unitService.findUnitById(new ObjectId(unitId));

            // Chỉ trưởng đơn vị mới thêm file
            if(!unit.getUnitHead().getId().equals(account.getId())){
                throw new HttpException(10003, "Users are not allowed.", HttpServletResponse.SC_FORBIDDEN);
            }

            //Push file lên server minio
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            //Lưu thông tin file xuống collection file
            File newFile = new File();
            newFile.setFilename(fileName);
            newFile.setExtension(extension);
            newFile.setMimeType(mimeType);
            newFile.setSize(size);
            newFile.setPath(filePath);
            com.project.EWCM.pojo.Account createdBy =
                    new com.project.EWCM.pojo.Account(account.getId(),account.getUsername(),account.getFullName(), account.getEmail(), account.getType());
            newFile.setCreatedBy(createdBy);
            newFile.setCreatedDate(currentDate);
            newFile.setUpdatedDate(currentDate);

            fileRepository.save(newFile);
            logger.info("EWCM-Update File Data: " + newFile.toString());

            result.setId(newFile.getId());
            result.setFileName(fileName);
            result.setSize(size);
            result.setMimeType(mimeType);
            result.setFilePath(filePath);
            return result;
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

    public FileDownloadResponseDto downloadFile(String fileId, String username) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        FileDownloadResponseDto result = new FileDownloadResponseDto();

        Account account = accountService.findAccountByUsername(username);
        File fileEntity = fileRepository.findById(new ObjectId(fileId)).orElseThrow(() ->
                new HttpException(10004, "File not found.", HttpServletResponse.SC_NOT_FOUND)
        );
        result.setFilename(fileEntity.getFilename());
        result.setFileMimeType(fileEntity.getMimeType());
        result.setSize(fileEntity.getSize());
        result.setCreatedBy(fileEntity.getCreatedBy());

        InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileEntity.getPath())
                        .build());
        Resource resource = new InputStreamResource(stream);
        result.setResource(resource);
        return result;
    }
}
