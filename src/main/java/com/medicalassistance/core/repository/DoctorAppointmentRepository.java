package com.medicalassistance.core.repository;

import com.medicalassistance.core.entity.CounselorAppointment;
import com.medicalassistance.core.entity.DoctorAppointment;
import com.medicalassistance.core.response.AppointmentListForDateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface DoctorAppointmentRepository extends MongoRepository<DoctorAppointment, String> {
    Page<DoctorAppointment> findByDoctorIdAndStartDateTimeGreaterThanEqualOrderByCreatedAtDesc(String doctorId, ZonedDateTime date, Pageable pageable);

    List<AppointmentListForDateResponse> findByDoctorIdAndStartDateTimeBetweenOrderByCreatedAtDesc(String counselorId, ZonedDateTime startDate, ZonedDateTime endDate);

    boolean existsByDoctorIdAndStartDateTimeBetweenOrStartDateTimeEquals(
            String doctorId, ZonedDateTime startDateTime, ZonedDateTime endDateTime, ZonedDateTime startDateTimeE);

    boolean existsByDoctorIdAndEndDateTimeBetweenOrEndDateTimeEquals(
            String doctorId, ZonedDateTime startDateTime, ZonedDateTime endDateTime, ZonedDateTime startDateTimeE);

    DoctorAppointment findByAppointmentId(String appointmentId);

    boolean existsByPatientRecordId(String patientRecordId);

    void deleteByAppointmentId(String appointmentId);

    Integer countByStartDateTimeAfter(ZonedDateTime currentDateTime);

    Integer countByStartDateTimeBetween(ZonedDateTime startDateTime, ZonedDateTime endDateTime);

    Integer countBy();

    void deleteByPatientId(String patientId);

    void deleteByDoctorId(String doctorId);

    List<DoctorAppointment> findByDoctorId(String counselorId);
}