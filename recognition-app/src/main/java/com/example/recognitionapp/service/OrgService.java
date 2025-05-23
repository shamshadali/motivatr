package com.example.recognitionapp.service;

import com.example.recognitionapp.model.Org;
import com.example.recognitionapp.repository.OrgRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class OrgService {

    private final OrgRepository orgRepository;

    @Autowired
    public OrgService(OrgRepository orgRepository) {
        this.orgRepository = orgRepository;
    }

    public Org saveOrg(Org org) {
        // Add any business logic before saving if necessary
        return orgRepository.save(org);
    }

    public Optional<Org> findOrgById(Long id) {
        return orgRepository.findById(id);
    }
    // Add other methods as needed
}
