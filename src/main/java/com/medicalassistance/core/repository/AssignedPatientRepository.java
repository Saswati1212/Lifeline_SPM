package com.medicalassistance.core.repository;

import com.medicalassistance.core.entity.AssignedPatient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.ZonedDateTime;

public interface AssignedPatientRepository extends MongoRepository<AssignedPatient, String> {
    Page<AssignedPatient> findByDoctorRegistrationNumberOrderByCreatedAtDesc(String doctorRegistrationNumber, Pageable pageable);

    boolean existsByPatientRecordId(String patientRecordId);

    Integer countByDoctorRegistrationNumber(String doctorRegistrationNumber);

    boolean existsByPatientId(String patientId);

    Integer countByCreatedAtBetween(ZonedDateTime startDateTime, ZonedDateTime endDateTime);

    Integer countBy();

    void deleteByPatientId(String patientId);

    void deleteByDoctorRegistrationNumber(String doctorRegistrationNumber);
}