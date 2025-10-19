package iuh.fit.se.ace_store_www.service;

import iuh.fit.se.ace_store_www.dto.response.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface CloudinaryService {
    ApiResponse uploadFile(MultipartFile file);
    ApiResponse uploadMultipleFile(MultipartFile[] files);
    ApiResponse deleteFile(String publicId);
}
