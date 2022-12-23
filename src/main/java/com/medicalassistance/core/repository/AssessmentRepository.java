package com.medicalassistance.core.repository;

import com.medicalassistance.core.entity.Assessment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AssessmentRepository extends MongoRepository<Assessment, String> {
    Assessment findByAssessmentId(String assessmentId);
}