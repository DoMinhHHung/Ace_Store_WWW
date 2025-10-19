package iuh.fit.se.ace_store_www.controller;

import iuh.fit.se.ace_store_www.dto.response.ApiResponse;
import iuh.fit.se.ace_store_www.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class CloudinaryController {

    private final CloudinaryService cloudinaryService;

    // POST /api/uploads/file
    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> uploadSingleFile(@RequestParam("file") MultipartFile file) {
        ApiResponse response = cloudinaryService.uploadFile(file);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @PostMapping(value = "/multiple-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        ApiResponse response = cloudinaryService.uploadMultipleFile(files);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("/delete/{publicId}")
    public ResponseEntity<ApiResponse> deleteFile(@PathVariable String publicId) {
        ApiResponse response = cloudinaryService.deleteFile(publicId);
        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }
}