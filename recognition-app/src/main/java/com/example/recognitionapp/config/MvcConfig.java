package com.example.recognitionapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        // If we don't have a DashboardController, we could add:
        // registry.addViewController("/dashboard").setViewName("dashboard");
        // But DashboardController is already handling /dashboard

        // Add a view controller for the /register path to serve a registration.html page
        // This assumes you will create a registration.html page.
        // If UserController is supposed to handle GET /register to show a form,
        // then this line might conflict or be redundant.
        // For now, matching the task description's MvcConfig.
        // The DashboardController's /register mapping should be aligned with this.
        // Let's assume for now MvcConfig is authoritative for /login.
        // The DashboardController's /register mapping currently points to "registration"
        // which would be resolved by Thymeleaf to registration.html.
        // So, we can add registry.addViewController("/register").setViewName("registration");
        // if we want MvcConfig to handle it instead of DashboardController.
        // For this iteration, let's stick to the provided MvcConfig for /login only.
    }
}
