package iuh.fit.se.ace_store_www.controller;

import iuh.fit.se.ace_store_www.dto.response.ApiResponse;
import iuh.fit.se.ace_store_www.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    public AdminController(UserService userService) { this.userService = userService; }

    @PostMapping("/grant-admin/{userId}")
    public ResponseEntity<ApiResponse<String>> grantAdmin(@PathVariable Long userId) {
        var resp = userService.grantAdminRole(userId);
        if (!resp.isSuccess()) return ResponseEntity.badRequest().body(ApiResponse.fail(resp.getMessage()));
        return ResponseEntity.ok(resp);
    }
}
