package com.example.recognitionapp.dto;

import com.example.recognitionapp.model.Org;

public class OrgResponse {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String contactPerson;
    private String contactPhone;
    private String status;

    // Default constructor
    public OrgResponse() {
    }

    public static OrgResponse fromEntity(Org org) {
        OrgResponse dto = new OrgResponse();
        dto.setId(org.getId());
        dto.setName(org.getName());
        dto.setAddress(org.getAddress());
        dto.setCity(org.getCity());
        dto.setState(org.getState());
        dto.setZip(org.getZip());
        dto.setContactPerson(org.getContactPerson());
        dto.setContactPhone(org.getContactPhone());
        dto.setStatus(org.getStatus());
        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
