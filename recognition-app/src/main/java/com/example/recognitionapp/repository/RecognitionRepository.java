package com.example.recognitionapp.repository;

import com.example.recognitionapp.model.Recognition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecognitionRepository extends JpaRepository<Recognition, Long> {
    List<Recognition> findByRecipientId(Long recipientId);
    List<Recognition> findByRecognizerId(Long recognizerId);
}
