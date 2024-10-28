package com.project.EWCM.Controller;

import com.project.EWCM.Service.FilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {

        // Kiểm tra kích thước file
        if (file.getSize() > 50 * 1024 * 1024) { // 50MB
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("File size exceeds the limit of 50MB.");
        }

        // Kiểm tra định dạng file (hình ảnh)
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("File must be an image.");
        }


        // Tạo cấu trúc folder theo ngày hiện tại
        LocalDate date = LocalDate.now();
        String folderPath = "user-uploads/" + date.getYear() + "/" + date.getMonthValue() + "/" + date.getDayOfMonth();

        String filePath = filesService.uploadFile(file, folderPath);

        return ResponseEntity.ok("File uploaded to: " + filePath);
    }

    @GetMapping("/download/{filePath:.+}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String filePath) {
        try {
            InputStream fileStream = filesService.getFile(filePath);

            // Chuyển InputStream sang byte array để trả về
            byte[] fileBytes = fileStream.readAllBytes();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath + "\"")
                    .body(fileBytes);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
