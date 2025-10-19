package iuh.fit.se.ace_store_www.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils; // Cần cái này để dùng ObjectUtils.emptyMap()
import iuh.fit.se.ace_store_www.dto.response.ApiResponse;
import iuh.fit.se.ace_store_www.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {
    private final Cloudinary cloudinary;

    @Override
    public ApiResponse uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResponse.error("File cannot be empty, man.");
        }
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.emptyMap()
            );
            Map<String, String> data = Map.of(
                    "publicId", uploadResult.get("public_id").toString(),
                    "url", uploadResult.get("secure_url").toString(),
                    "fileName", Objects.requireNonNull(file.getOriginalFilename())
            );
            return ApiResponse.success("File uploaded successfully.", data);
        } catch (IOException e) {
            // Log lỗi rồi throw Exception
            throw new RuntimeException("Error uploading file to Cloudinary: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse uploadMultipleFile(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return ApiResponse.error("File list cannot be empty, bro.");
        }
        List<Map<String, String>> uploadedFilesData = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    Map uploadResult = cloudinary.uploader().upload(
                            file.getBytes(),
                            ObjectUtils.emptyMap()
                    );
                    Map<String, String> data = Map.of(
                            "publicId", uploadResult.get("public_id").toString(),
                            "url", uploadResult.get("secure_url").toString(),
                            "fileName", Objects.requireNonNull(file.getOriginalFilename())
                    );
                    uploadedFilesData.add(data);
                } catch (IOException e) {
                    throw new RuntimeException("Error uploading one of the files: " + e.getMessage());
                }
            }
        }
        return ApiResponse.success("Multiple files uploaded successfully.", uploadedFilesData);
    }

    @Override
    public ApiResponse deleteFile(String publicId) {
        if (publicId == null || publicId.isEmpty()) {
            return ApiResponse.error("Public ID is required to delete the file, bae.");
        }
        try {
            Map deleteResult = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            String result = deleteResult.get("result").toString();
            if (result.equals("ok")) {
                return ApiResponse.success("File deleted successfully. Public ID: " + publicId, null);
            } else if (result.equals("not found")) {
                return ApiResponse.error("File not found on Cloudinary with Public ID: " + publicId);
            } else {
                return ApiResponse.error("Failed to delete file on Cloudinary. Result: " + result);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error deleting file from Cloudinary: " + e.getMessage());
        }
    }
}