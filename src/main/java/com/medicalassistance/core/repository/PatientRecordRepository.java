package com.medicalassistance.core.repository;

import com.medicalassistance.core.common.PatientRecordStatus;
import com.medicalassistance.core.entity.PatientRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PatientRecordRepository extends MongoRepository<PatientRecord, String> {
    PatientRecord findByPatientRecordId(String patientRecordId);

    boolean existsByPatientRecordId(String patientRecordId);

    PatientRecord findTop1ByPatientIdOrderByCreatedAtDesc(String patientId);

    void deleteByPatientId(String patientId);

    List<PatientRecord> findByAppointmentIdAndStatus(String appointmentId, PatientRecordStatus patientRecordStatus);

    void deleteByAppointmentIdAndStatusIs(String appointmentId, PatientRecordStatus patientRecordStatus);

    void deleteByPatientRecordId(String patientRecordId);
}