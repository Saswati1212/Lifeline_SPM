package com.medicalassistance.core.service;

import com.medicalassistance.core.common.AuthorityName;
import com.medicalassistance.core.common.PatientRecordStatus;
import com.medicalassistance.core.entity.*;
import com.medicalassistance.core.exception.AlreadyExistsException;
import com.medicalassistance.core.exception.InvalidUserRequestException;
import com.medicalassistance.core.exception.ResourceNotFoundException;
import com.medicalassistance.core.mapper.UserMapper;
import com.medicalassistance.core.repository.*;
import com.medicalassistance.core.request.AssessmentResultRequest;
import com.medicalassistance.core.request.AttemptedQuestionRequest;
import com.medicalassistance.core.request.DummyUsers;
import com.medicalassistance.core.request.UserRequest;
import com.medicalassistance.core.response.*;
import com.medicalassistance.core.util.EncryptionUtil;
import com.medicalassistance.core.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {
    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private BaseService baseService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ActivePatientRepository activePatientRepository;

    @Autowired
    private CounselorAppointmentRepository counselorAppointmentRepository;

    @Autowired
    private DoctorAppointmentRepository doctorAppointmentRepository;

    @Autowired
    private AssignedPatientRepository assignedPatientRepository;

    @Autowired
    private AssessmentResultRepository assessmentResultRepository;

    @Autowired
    private PatientRecordService patientRecordService;

    @Autowired
    private PatientRecordRepository patientRecordRepository;

    public void createAssessment(Assessment assessment) {
        assessmentRepository.save(assessment);
    }

    public Page<AdminPatientCard> getPatients(Pageable pageable) {
        Page<User> patientCardPage = userRepository.findByAuthoritiesContainsAndDeletedFalseOrderByCreatedAtDesc(AuthorityName.ROLE_PATIENT, pageable);
        return patientCardPage.map(userMapper::toAdminPatientCard);
    }

    public Page<AdminCounselorCard> getCounselors(Pageable pageable) {
        Page<User> patientCardPage = userRepository.findByAuthoritiesContainsAndDeletedFalseOrderByCreatedAtDesc(AuthorityName.ROLE_COUNSELOR, pageable);
        return patientCardPage.map(userMapper::toAdminCounselorCard);
    }

    public Page<AdminDoctorCard> getDoctors(Pageable pageable) {
        Page<User> patientCardPage = userRepository.findByAuthoritiesContainsAndDeletedFalseOrderByCreatedAtDesc(AuthorityName.ROLE_DOCTOR, pageable);
        return patientCardPage.map(userMapper::toAdminDoctorCard);
    }

    public AdminUserCreateResponse createPatient(UserRequest userRequest) {
        return createUser(userRequest, AuthorityName.ROLE_PATIENT);
    }

    public AdminUserCreateResponse createCounselor(UserRequest userRequest) {
        return createUser(userRequest, AuthorityName.ROLE_COUNSELOR);
    }

    public AdminUserCreateResponse createDoctor(UserRequest userRequest) {
        return createUser(userRequest, AuthorityName.ROLE_DOCTOR);
    }

    private AdminUserCreateResponse createUser(UserRequest userRequest, AuthorityName authorityName) {
        userRequest.setPassword(UserUtil.generateRandomPassword());
        System.out.println("Password: " + userRequest.getPassword());
        LoginResponse loginResponse = baseService.signUp(userRequest, authorityName, true);
        AdminUserCreateResponse response = new AdminUserCreateResponse();
        response.setSuccess(loginResponse.isLoginSuccess());
        response.setErrorMessage(loginResponse.getErrorMessage());
        response.setUser(loginResponse.getUser());
        return response;
    }

    public void removePatient(String emailAddress) {
        removeUser(emailAddress, AuthorityName.ROLE_PATIENT);
    }

    public void removeCounselor(String emailAddress) {
        removeUser(emailAddress, AuthorityName.ROLE_COUNSELOR);
    }

    public void removeDoctor(String emailAddress) {
        removeUser(emailAddress, AuthorityName.ROLE_DOCTOR);
    }

    private void removeUser(String emailAddress, AuthorityName authorityName) {
        User user = userRepository.findByEmailAddressAndAuthoritiesContainsAndDeletedFalse(emailAddress, Collections.singleton(authorityName));
        if (authorityName == AuthorityName.ROLE_PATIENT) {
            activePatientRepository.deleteByPatientId(user.getUserId());
            assessmentResultRepository.deleteByPatientId(user.getUserId());
            assignedPatientRepository.deleteByPatientId(user.getUserId());
            counselorAppointmentRepository.deleteByPatientId(user.getUserId());
            doctorAppointmentRepository.deleteByPatientId(user.getUserId());
            patientRecordRepository.deleteByPatientId(user.getUserId());
        }
        if (authorityName == AuthorityName.ROLE_COUNSELOR) {
            List<CounselorAppointment> appointments = counselorAppointmentRepository.findByCounselorId(user.getUserId());
            for (CounselorAppointment appointment : appointments) {
                List<PatientRecord> patientRecords = patientRecordRepository.findByAppointmentIdAndStatus(appointment.getAppointmentId(), PatientRecordStatus.COUNSELOR_APPOINTMENT);
                patientRecords.forEach(patientRecord -> {
                    if (patientRecord.getActivePatientId() != null)
                        activePatientRepository.deleteByActivePatientId(patientRecord.getActivePatientId());
                    patientRecordRepository.deleteByPatientRecordId(patientRecord.getPatientRecordId());
                });
                patientRecords = patientRecordRepository.findByAppointmentIdAndStatus(appointment.getAppointmentId(), PatientRecordStatus.COUNSELOR_IN_PROGRESS);
                patientRecords.forEach(patientRecord -> {
                    if (patientRecord.getActivePatientId() != null)
                        activePatientRepository.deleteByActivePatientId(patientRecord.getActivePatientId());
                    patientRecordRepository.deleteByPatientRecordId(patientRecord.getPatientRecordId());
                });
                counselorAppointmentRepository.deleteByAppointmentId(appointment.getAppointmentId());
            }
        }
        if (authorityName == AuthorityName.ROLE_DOCTOR) {
            List<DoctorAppointment> appointments = doctorAppointmentRepository.findByDoctorId(user.getUserId());
            for (DoctorAppointment appointment : appointments) {
                List<PatientRecord> patientRecords = patientRecordRepository.findByAppointmentIdAndStatus(appointment.getAppointmentId(), PatientRecordStatus.DOCTOR_APPOINTMENT);
                patientRecords.forEach(patientRecord -> {
                    if (patientRecord.getActivePatientId() != null)
                        activePatientRepository.deleteByActivePatientId(patientRecord.getActivePatientId());
                    patientRecordRepository.deleteByPatientRecordId(patientRecord.getPatientRecordId());
                });
                patientRecords = patientRecordRepository.findByAppointmentIdAndStatus(appointment.getAppointmentId(), PatientRecordStatus.DOCTOR_IN_PROGRESS);
                patientRecords.forEach(patientRecord -> {
                    if (patientRecord.getActivePatientId() != null)
                        activePatientRepository.deleteByActivePatientId(patientRecord.getActivePatientId());
                    patientRecordRepository.deleteByPatientRecordId(patientRecord.getPatientRecordId());
                });
                doctorAppointmentRepository.deleteByAppointmentId(appointment.getAppointmentId());
            }
            assignedPatientRepository.deleteByDoctorRegistrationNumber(user.getRegistrationNumber());
        }
        if (user != null) {
            user.setDeleted(true);
            userRepository.save(user);
        } else {
            throw new ResourceNotFoundException("user not found!");
        }
    }

    public AdminPatientReport getAdminPatientReportByRange(ZonedDateTime startDateTime, ZonedDateTime endDateTime) {
        AdminPatientReport report = new AdminPatientReport();
        List<User> patientCardPage = userRepository.findByAuthoritiesContainsAndCreatedAtBetweenAndDeletedFalseOrderByCreatedAt
                (Collections.singleton(AuthorityName.ROLE_PATIENT),
                        startDateTime,
                        endDateTime);
        report.setPatients(patientCardPage.stream().map(userMapper::toAdminPatientCard).collect(Collectors.toList()));
        report.setNumAttemptedAssessment(activePatientRepository.countByCreatedAtBetween(startDateTime, endDateTime));
        report.setNumTotal(patientCardPage.size());
        report.setNumHasCounselorAppointment(counselorAppointmentRepository.countByStartDateTimeBetween(startDateTime, endDateTime));
        Integer numHasDoctorAppointment = doctorAppointmentRepository.countByStartDateTimeBetween(startDateTime, endDateTime);
        report.setNumHasDoctorAppointment(numHasDoctorAppointment);
        report.setNumInProcessingDoctorAppointment(assignedPatientRepository.countByCreatedAtBetween(startDateTime, endDateTime) - numHasDoctorAppointment);
        return report;
    }

    public AdminPatientReportParameters getAdminPatientReportParameters() {
        AdminPatientReportParameters report = new AdminPatientReportParameters();
        Integer totalUsers = userRepository.countByAuthoritiesContains(Collections.singleton(AuthorityName.ROLE_PATIENT));
        report.setNumAttemptedAssessment(activePatientRepository.countBy());
        report.setNumTotal(totalUsers);
        report.setNumHasCounselorAppointment(counselorAppointmentRepository.countBy());
        Integer numHasDoctorAppointment = doctorAppointmentRepository.countBy();
        report.setNumHasDoctorAppointment(numHasDoctorAppointment);
        report.setNumInProcessingDoctorAppointment(assignedPatientRepository.countBy() - numHasDoctorAppointment);
        return report;
    }

    public void resetUsers() {
        userRepository.findAll().forEach(user -> {
            user.setDeleted(false);
            user.setPasswordAutoGenerated(false);
            userRepository.save(user);
        });
    }


    public void insertDummyUsers(DummyUsers dummyUsers) {
        ZonedDateTime createdAt = dummyUsers.getStartDateTime();
        Integer interval = dummyUsers.getInterval();
        if (interval <= 0) {
            throw new InvalidUserRequestException("invalid admin request");
        }
        List<User> patients = new ArrayList<>();
        int index = dummyUsers.getStartIndex();
        for (int i = 0; i < dummyUsers.getNumPatient(); i++) {
            User user = createDummyUser(index++, createdAt, AuthorityName.ROLE_PATIENT);
            createdAt = createdAt.plusMinutes(interval);
            patients.add(userRepository.save(user));
        }

        List<User> counselors = new ArrayList<>();
        for (int i = 0; i < dummyUsers.getNumHasCounselorAppointment(); i++) {
            User user = createDummyUser(index++, createdAt, AuthorityName.ROLE_COUNSELOR);
            createdAt = createdAt.plusMinutes(interval);
            counselors.add(userRepository.save(user));
        }

        List<User> doctors = new ArrayList<>();
        for (int i = 0; i < dummyUsers.getNumHasDoctorAppointment(); i++) {
            User user = createDummyUser(index++, createdAt, AuthorityName.ROLE_DOCTOR);
            createdAt = createdAt.plusMinutes(interval);
            doctors.add(userRepository.save(user));
        }

        List<PatientRecord> patientRecords = new ArrayList<>();
        for (int i = 0; i < Math.min(dummyUsers.getNumAttemptedAssessment(), patients.size()); i++) {
            User patient = patients.get(i);
            if (activePatientRepository.existsByPatientId(patient.getUserId()) ||
                    assignedPatientRepository.existsByPatientId(patient.getUserId())) {
                throw new AlreadyExistsException("You already have an active patient file with us!");
            }

            AssessmentResult assessmentResult = new AssessmentResult();

            // store the assessment result
            assessmentResult.setAssessmentId("635b203cf4d8b811f7a0ac0b");
            assessmentResult.setCreatedAt(patient.getCreatedAt());
            assessmentResult.setUpdatedAt(patient.getUpdatedAt());
            AssessmentResultRequest resultRequest = createAssessmentResultRequest();
            List<AttemptedQuestion> attemptedQuestions = new ArrayList<>();
            for (int j = 0; j < resultRequest.getQuestions().size(); j++) {
                AttemptedQuestionRequest questionRequest = resultRequest.getQuestions().get(j);
                attemptedQuestions.add(new AttemptedQuestion(questionRequest.getQuestionId(), questionRequest.getAnswer()));
            }
            assessmentResult.setPatientId(patient.getUserId());
            assessmentResult.setAttemptedQuestions(attemptedQuestions);
            assessmentResult = assessmentResultRepository.save(assessmentResult);

            // create patient record and active patient record.
            // store the patient record
            PatientRecord patientRecord = new PatientRecord();
            patientRecord.setCreatedAt(patient.getCreatedAt());
            patientRecord.setUpdatedAt(patient.getUpdatedAt());
            patientRecord.setAssessmentResultId(assessmentResult.getAssessmentResultId());
            patientRecord.setPatientId(assessmentResult.getPatientId());
            patientRecord.setStatus(PatientRecordStatus.COUNSELOR_IN_PROGRESS);
            patientRecord = patientRecordRepository.save(patientRecord);

            // create an active patient record
            ActivePatient activePatient = new ActivePatient();
            activePatient.setCreatedAt(patient.getCreatedAt());
            activePatient.setUpdatedAt(patient.getUpdatedAt());
            activePatient.setPatientId(assessmentResult.getPatientId());
            activePatient.setPatientRecordId(patientRecord.getPatientRecordId());
            activePatient = activePatientRepository.save(activePatient);

            patientRecord.setActivePatientId(activePatient.getActivePatientId());
            patientRecords.add(patientRecordRepository.save(patientRecord));
        }

        int counselorIndex = 0;
        List<PatientRecord> counselorAppointmentPatientRecords = new ArrayList<>();
        for (int i = 0; i < Math.min(dummyUsers.getNumHasCounselorAppointment(), patientRecords.size()); i++) {
            User counselor = counselors.get(counselorIndex);
            counselorIndex = (counselorIndex + 1) % counselors.size();
            PatientRecord patientRecord = patientRecords.get(i);
            // save counselor appointment
            CounselorAppointment counselorAppointment = new CounselorAppointment();
            counselorAppointment.setCreatedAt(counselor.getCreatedAt());
            counselorAppointment.setUpdatedAt(counselor.getUpdatedAt());
            counselorAppointment.setCounselorId(counselor.getUserId());
            counselorAppointment.setPatientRecordId(patientRecord.getPatientRecordId());
            counselorAppointment.setStartDateTime(createdAt);
            createdAt = createdAt.plusMinutes(interval * 10);
            counselorAppointment.setEndDateTime(createdAt);
            createdAt = createdAt.plusMinutes(interval * 10);

            counselorAppointment = counselorAppointmentRepository.save(counselorAppointment);

            // update patient record
            counselorAppointmentPatientRecords.add(patientRecordService.afterAppointment(counselorAppointment, patientRecord, PatientRecordStatus.COUNSELOR_APPOINTMENT));
        }

        List<PatientRecord> assignedPatientsToDoctor = new ArrayList<>();
        int doctorIndex = 0;
        for (int i = 0; i < Math.min(counselorAppointmentPatientRecords.size(), dummyUsers.getNumInProcessingDoctor() + dummyUsers.getNumHasDoctorAppointment()); i++) {
            User doctor = doctors.get(doctorIndex);
            doctorIndex = (doctorIndex + 1) % doctors.size();
            User counselor = counselors.get(counselorIndex);
            counselorIndex = (counselorIndex + 1) % counselors.size();
            PatientRecord patientRecord = counselorAppointmentPatientRecords.get(i);
            // before forwarding to a doctor, delete any existing appointment with the counselor.
            if (patientRecord.getStatus() == PatientRecordStatus.COUNSELOR_APPOINTMENT &&
                    patientRecord.getAppointmentId() != null) {
                counselorAppointmentRepository.deleteByAppointmentId(patientRecord.getAppointmentId());
            }

            // save assigned patient record
            AssignedPatient assignedPatient = new AssignedPatient();
            assignedPatient.setCreatedAt(counselor.getCreatedAt());
            assignedPatient.setUpdatedAt(counselor.getUpdatedAt());
            assignedPatient.setPatientRecordId(patientRecord.getPatientRecordId());
            assignedPatient.setDoctorRegistrationNumber(doctor.getRegistrationNumber());
            assignedPatient.setCounselorRegistrationNumber(counselor.getRegistrationNumber());
            assignedPatient.setPatientId(patientRecord.getPatientId());
            assignedPatient = assignedPatientRepository.save(assignedPatient);

            // update patient record after assigning a doctor to active patient record
            assignedPatientsToDoctor.add(patientRecordService.afterAssigningDoctor(assignedPatient, patientRecord));
        }

        int assignedPatients = assignedPatientsToDoctor.size() - dummyUsers.getNumInProcessingDoctor();
        List<PatientRecord> doctorAppointmentPatientRecords = new ArrayList<>();
        for (int i = 0; i < Math.min(dummyUsers.getNumHasDoctorAppointment(), assignedPatients); i++) {
            User doctor = doctors.get(doctorIndex);
            doctorIndex = (doctorIndex + 1) % doctors.size();
            PatientRecord patientRecord = patientRecords.get(i);
            // save counselor appointment
            DoctorAppointment doctorAppointment = new DoctorAppointment();
            doctorAppointment.setCreatedAt(doctor.getCreatedAt());
            doctorAppointment.setUpdatedAt(doctor.getUpdatedAt());
            doctorAppointment.setDoctorId(doctor.getUserId());
            doctorAppointment.setPatientRecordId(patientRecord.getPatientRecordId());
            doctorAppointment.setStartDateTime(createdAt);
            createdAt = createdAt.plusMinutes(interval * 10);
            doctorAppointment.setEndDateTime(createdAt);
            createdAt = createdAt.plusMinutes(interval * 10);

            doctorAppointment = doctorAppointmentRepository.save(doctorAppointment);

            // update patient record
            doctorAppointmentPatientRecords.add(patientRecordService.afterAppointment(doctorAppointment, patientRecord, PatientRecordStatus.DOCTOR_APPOINTMENT));
        }
    }

    private User createDummyUser(Integer index, ZonedDateTime createdAt, AuthorityName authorityName) {
        User user = new User();
        user.setFullName("Dummy FullName " + index);
        user.setEmailAddress("dummyemail" + index + "@test.com");
        user.setAddressLine("Dummy Address");
        user.setProvince("Dummy Province");
        user.setCountry("Dummy Country");
        user.setCity("Dummy City");
        user.setPhoneNumber("1234567890");
        user.setPassword(EncryptionUtil.encryptPassword("12345678"));
        user.setRegistrationNumber("RG12345678" + index);
        user.setDateOfBirth(new Date("01 January 2000").toInstant().atZone(ZoneOffset.UTC));
        user.setAuthorities(Collections.singleton(authorityName));
        user.setPasswordAutoGenerated(false);
        user.setDeleted(false);
        user.setCreatedAt(createdAt);
        user.setUpdatedAt(createdAt);
        return user;
    }

    private AssessmentResultRequest createAssessmentResultRequest() {
        AssessmentResultRequest assessmentResultRequest = new AssessmentResultRequest();
        List<AttemptedQuestionRequest> questionRequests = new ArrayList<>();
        questionRequests.add(new AttemptedQuestionRequest("635b1d55f4d8b811f7a0ac02", 1));
        questionRequests.add(new AttemptedQuestionRequest("635b1d5ff4d8b811f7a0ac03", 1));
        questionRequests.add(new AttemptedQuestionRequest("635b1d6df4d8b811f7a0ac04", 0));
        questionRequests.add(new AttemptedQuestionRequest("635b1d77f4d8b811f7a0ac05", 1));
        questionRequests.add(new AttemptedQuestionRequest("635b1d85f4d8b811f7a0ac06", 0));
        questionRequests.add(new AttemptedQuestionRequest("635b1d90f4d8b811f7a0ac07", 1));
        questionRequests.add(new AttemptedQuestionRequest("635b1dd1f4d8b811f7a0ac08", 0));
        questionRequests.add(new AttemptedQuestionRequest("635b1dd1f4d8b811f7a0ac09", 0));
        questionRequests.add(new AttemptedQuestionRequest("635b1dd1f4d8b811f7a0ac0a", 1));
        assessmentResultRequest.setQuestions(questionRequests);
        return assessmentResultRequest;
    }
}