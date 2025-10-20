package iuh.fit.se.ace_store_www.controller.views;

import iuh.fit.se.ace_store_www.repository.ProductRepository;
import iuh.fit.se.ace_store_www.repository.PromotionRepository;
import iuh.fit.se.ace_store_www.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/fe")
@RequiredArgsConstructor
public class FEViewController {
    
    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;
    private final UserRepository userRepository;

    @GetMapping("/products")
    public String showProductsPage(Model model) {
        // Load initial data for server-side rendering
        model.addAttribute("products", productRepository.findAll());
        return "products";
    }

    @GetMapping("/users")
    public String showUsersPage(Model model) {
        // Load initial data for server-side rendering
        model.addAttribute("users", userRepository.findAll());
        return "users";
    }

    @GetMapping("/promotions")
    public String showPromotionsPage(Model model) {
        // Load initial data for server-side rendering
        model.addAttribute("promotions", promotionRepository.findAll());
        return "promotions";
    }

    @GetMapping("/admins")
    public String showAdminsPage(Model model) {
        // Load admin users (users with ADMIN role)
        model.addAttribute("admins", userRepository.findAll()
            .stream()
            .filter(user -> user.getRoles().stream()
                .anyMatch(role -> role.name().equals("ADMIN")))
            .toList());
        return "admins";
    }
}
