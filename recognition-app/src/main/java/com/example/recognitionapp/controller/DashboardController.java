package com.example.recognitionapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.ui.Model;
// import java.security.Principal;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard() {
        // Spring Security's principal object is automatically available in Thymeleaf
        // if thymeleaf-extras-springsecurity is configured.
        // No need to explicitly add to model unless more details are needed.
        return "dashboard";
    }

    // Optional: If we don't have a dedicated registration page yet,
    // but want the link from login.html to go somewhere.
    // This could also be handled by UserController if it serves a registration HTML page.
    @GetMapping("/register")
    public String showRegistrationForm() {
        // return "registration"; // Assuming a registration.html page
        // For now, let's assume UserController /api/users/register is API only.
        // So, this might just redirect or show a simple page.
        // Or, we can create a simple registration.html page later.
        // For this step, let's just make sure /dashboard works.
        // The login page has a register link, but we haven't built the HTML form for it yet.
        // Let's create a placeholder for it.
        return "redirect:/api/users/register"; // Or a dedicated HTML page if we build one
    }
}
