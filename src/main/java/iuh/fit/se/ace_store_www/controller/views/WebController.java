package iuh.fit.se.ace_store_www.controller.views;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class WebController {

    // URL: http://localhost:8080/products-view
    @GetMapping("/products-view")
    public String showProductsPage() {
        // Trả về template: src/main/resources/templates/product-list.html
        return "product-list";
    }

    // URL: http://localhost:8080/promotions-view
    @GetMapping("/promotions-view")
    public String showPromotionsPage() {
        // Trả về template: src/main/resources/templates/promotion-list.html
        return "promotion-list";
    }
    @GetMapping("/products/create")
    public String showCreateProductForm() {
        // Trả về template: src/main/resources/templates/product-create.html
        return "product-create";
    }
}