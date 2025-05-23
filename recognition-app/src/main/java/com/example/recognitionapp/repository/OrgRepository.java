package com.example.recognitionapp.repository;

import com.example.recognitionapp.model.Org;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgRepository extends JpaRepository<Org, Long> {
    // You can add custom query methods here if needed later
}
