package com.medicalassistance.core.repository;

import com.medicalassistance.core.entity.BooleanQuestion;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BooleanQuestionRepository extends MongoRepository<BooleanQuestion, String> {
    BooleanQuestion findByQuestionId(String questionId);
}