package com.example.recognitionapp.controller;

import com.example.recognitionapp.dto.OrgResponse;
import com.example.recognitionapp.model.Org;
import com.example.recognitionapp.service.OrgService;
import com.example.recognitionapp.exception.OrgNotFoundException; // For explicit handling if needed
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/orgs")
public class OrgController {

    private final OrgService orgService;

    @Autowired
    public OrgController(OrgService orgService) {
        this.orgService = orgService;
    }

    @PostMapping("/")
    public ResponseEntity<OrgResponse> createOrg(@RequestBody Org org) {
        // For simplicity, using Org entity directly as request.
        // Consider an OrgRequest DTO for more complex scenarios or validation.
        Org savedOrg = orgService.saveOrg(org);
        return new ResponseEntity<>(OrgResponse.fromEntity(savedOrg), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrgResponse> getOrgById(@PathVariable Long id) {
        Optional<Org> org = orgService.findOrgById(id);
        return org.map(o -> ResponseEntity.ok(OrgResponse.fromEntity(o)))
                  .orElse(ResponseEntity.notFound().build());
    }
}
