package com.example.recognitionapp.config;

import com.example.recognitionapp.service.UserService; // Or a dedicated UserDetailsService implementation
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
// import static org.springframework.security.config.Customizer.withDefaults; // Not used in this config

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        // UserService will implement UserDetailsService which provides the loadUserByUsername method
        return email -> userService.loadUserByUsername(email);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .antMatchers("/api/users/register", "/login", "/logout", "/css/**", "/js/**", "/images/**", "/h2-console/**").permitAll()
                .antMatchers("/api/orgs/**").permitAll() // For now, let Org creation be public for easier setup
                .anyRequest().authenticated()
            )
            .formLogin(formLogin -> formLogin
                .loginPage("/login") // Specify custom login page URL
                .defaultSuccessUrl("/dashboard", true) // Redirect to dashboard on success
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .userDetailsService(userDetailsService); // Set the custom UserDetailsService

        // For H2 console access
        http.csrf(csrf -> csrf.ignoringAntMatchers("/h2-console/**", "/api/**")); // Disable CSRF for H2 console and API
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())); // Allow frames for H2 console

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }
}
