package com.example.recognitionapp.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Collection; // For UserDetails
import java.util.Collections; // For UserDetails
import org.springframework.security.core.GrantedAuthority; // For UserDetails
import org.springframework.security.core.authority.SimpleGrantedAuthority; // For UserDetails
import org.springframework.security.core.userdetails.UserDetails; // For UserDetails

@Entity
@Table(name = "users") // "user" is often a reserved keyword in SQL
public class User implements UserDetails { // Implement UserDetails

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String email;

    private String password;
    private String status; // TODO: Consider Enum

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id")
    private Org org;

    private BigDecimal availableCredit;

    @OneToMany(mappedBy = "recognizer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Recognition> recognitionsGiven;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Recognition> recognitionsReceived;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RedemptionHistory> redemptionHistories;

    // Default constructor
    public User() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Note: UserDetails.getPassword() is implemented below
    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public BigDecimal getAvailableCredit() {
        return availableCredit;
    }

    public void setAvailableCredit(BigDecimal availableCredit) {
        this.availableCredit = availableCredit;
    }

    public List<Recognition> getRecognitionsGiven() {
        return recognitionsGiven;
    }

    public void setRecognitionsGiven(List<Recognition> recognitionsGiven) {
        this.recognitionsGiven = recognitionsGiven;
    }

    public List<Recognition> getRecognitionsReceived() {
        return recognitionsReceived;
    }

    public void setRecognitionsReceived(List<Recognition> recognitionsReceived) {
        this.recognitionsReceived = recognitionsReceived;
    }

    public List<RedemptionHistory> getRedemptionHistories() {
        return redemptionHistories;
    }

    public void setRedemptionHistories(List<RedemptionHistory> redemptionHistories) {
        this.redemptionHistories = redemptionHistories;
    }

    // UserDetails methods implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // For now, return a simple default role. This can be expanded later.
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return this.email; // Assuming email is the username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "ACTIVE".equalsIgnoreCase(this.status); // Or just true if status isn't used for enabled/disabled
    }
}
