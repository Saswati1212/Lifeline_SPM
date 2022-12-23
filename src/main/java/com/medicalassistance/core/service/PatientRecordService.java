package com.medicalassistance.core.service;

import com.medicalassistance.core.common.PatientRecordStatus;
import com.medicalassistance.core.common.UserCommonService;
import com.medicalassistance.core.entity.*;
import com.medicalassistance.core.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientRecordService {
    @Autowired
    AssessmentRepository assessmentRepository;

    @Autowired
    AssessmentResultRepository assessmentResultRepository;

    @Autowired
    ActivePatientRepository activePatientRepository;

    @Autowired
    UserCommonService userCommonService;

    @Autowired
    BooleanQuestionRepository booleanQuestionRepository;

    @Autowired
    PatientRecordRepository patientRecordRepository;

    public PatientRecord afterAssessment(AssessmentResult assessmentResult) {
        // store the patient record
        PatientRecord patientRecord = new PatientRecord();
        patientRecord.setAssessmentResultId(assessmentResult.getAssessmentResultId());
        patientRecord.setPatientId(assessmentResult.getPatientId());
        patientRecord.setStatus(PatientRecordStatus.COUNSELOR_IN_PROGRESS);
        patientRecord = patientRecordRepository.save(patientRecord);

        // create an active patient record
        ActivePatient activePatient = new ActivePatient();
        activePatient.setPatientId(assessmentResult.getPatientId());
        activePatient.setPatientRecordId(patientRecord.getPatientRecordId());
        activePatient = activePatientRepository.save(activePatient);

        patientRecord.setActivePatientId(activePatient.getActivePatientId());
        return patientRecordRepository.save(patientRecord);
    }

    public PatientRecord afterAppointment(Appointment appointment, PatientRecord patientRecord, PatientRecordStatus status) {
        if (status == PatientRecordStatus.COUNSELOR_APPOINTMENT || status == PatientRecordStatus.DOCTOR_APPOINTMENT) {
            // update patient record (ActivePatient)
            patientRecord.update();
            patientRecord.setAppointmentId(appointment.getAppointmentId());
            patientRecord.setStatus(status);
            return patientRecordRepository.save(patientRecord);
        }
        return null;
    }

    public PatientRecord afterAssigningDoctor(AssignedPatient assignedPatient, PatientRecord patientRecord) {
        ActivePatient activePatient = activePatientRepository.findByActivePatientId(patientRecord.getActivePatientId());

        // delete active patient
        activePatientRepository.delete(activePatient);

        // update patient record
        patientRecord.update();
        patientRecord.setAssignedPatientId(assignedPatient.getAssignedPatientId());
        patientRecord.setStatus(PatientRecordStatus.DOCTOR_IN_PROGRESS);
        return patientRecordRepository.save(patientRecord);
    }

    public PatientRecord afterRejectingPatient(PatientRecord patientRecord, PatientRecordStatus status) {
        patientRecord.update();
        patientRecord.setStatus(status);
        return patientRecordRepository.save(patientRecord);
    }
}
