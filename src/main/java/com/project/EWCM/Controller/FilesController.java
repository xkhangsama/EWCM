package com.project.EWCM.Controller;

import com.project.EWCM.DTO.FileDownloadResponseDto;
import com.project.EWCM.DTO.UploadFileResponseDto;
import com.project.EWCM.Service.FilesService;
import com.project.EWCM.config.jwt.JwtUtils;
import com.project.EWCM.exception.HttpException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/files")
public class FilesController {
    @Autowired
    private FilesService filesService;

    @Autowired
    private JwtUtils jwtUtils;

    Logger logger = LoggerFactory.getLogger(FilesController.class);

    @PostMapping("/upload")
    public ResponseEntity<Object> uploadFile(HttpServletRequest request, @RequestParam("file") MultipartFile file, @RequestParam("unitId") String unitId) {

        String requestPath = request.getMethod() + " " + request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        logger.info("EWCM-Request: " + requestPath);

        // Kiểm tra kích thước file
        if (file.getSize() > 50 * 1024 * 1024) { // 50MB
            throw new HttpException(10005,"File size exceeds the limit of 50MB.", HttpServletResponse.SC_BAD_REQUEST);
        }

        // Kiểm tra định dạng file (hình ảnh)
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new HttpException(10005,"File must be an image.", HttpServletResponse.SC_BAD_REQUEST);
        }


        // Tạo cấu trúc folder theo ngày hiện tại
        LocalDate date = LocalDate.now();
        String folderPath = "uploads/" + unitId + "/" + date.getYear() + "/" + date.getMonthValue() + "/" + date.getDayOfMonth();

        // Lấy token từ header Authorization
        String token = request.getHeader("Authorization").substring(7); // Lấy token sau "Bearer "

        // Lấy thông tin từ token
        String username = jwtUtils.getUserNameFromJwtToken(token);

        UploadFileResponseDto filePath = filesService.uploadFile(file, folderPath, username, unitId);

        return ResponseEntity.ok(filePath);
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(HttpServletRequest request, @PathVariable String fileId) {
        try {
            String requestPath = request.getMethod() + " " + request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
            logger.info("EWCM-Request: " + requestPath);

            // Lấy token từ header Authorization
            String token = request.getHeader("Authorization").substring(7); // Lấy token sau "Bearer "

            // Lấy thông tin từ token
            String username = jwtUtils.getUserNameFromJwtToken(token);

            FileDownloadResponseDto fileDownloadResponseDto = filesService.downloadFile(fileId, username);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDownloadResponseDto.getFilename() + "\"")
                    .body(fileDownloadResponseDto.getResource());
        } catch (Exception e) {
            throw new HttpException(10005,"File must be an image.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
