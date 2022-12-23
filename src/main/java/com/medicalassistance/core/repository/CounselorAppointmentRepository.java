package com.medicalassistance.core.repository;

import com.medicalassistance.core.entity.CounselorAppointment;
import com.medicalassistance.core.response.AppointmentListForDateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface CounselorAppointmentRepository extends MongoRepository<CounselorAppointment, String> {
    Page<CounselorAppointment> findByCounselorIdAndStartDateTimeGreaterThanEqualOrderByCreatedAtDesc(String counselorId, ZonedDateTime date, Pageable pageable);

    List<AppointmentListForDateResponse> findByCounselorIdAndStartDateTimeBetweenOrderByCreatedAtDesc(String counselorId, ZonedDateTime startDate, ZonedDateTime endDate);

    boolean existsByCounselorIdAndStartDateTimeBetweenOrStartDateTimeEquals(String counselorId, ZonedDateTime startDateTime, ZonedDateTime endDateTime, ZonedDateTime startDateTimeE);

    boolean existsByCounselorIdAndEndDateTimeBetweenOrEndDateTimeEquals(String counselorId, ZonedDateTime startDateTime, ZonedDateTime endDateTime, ZonedDateTime startDateTimeE);

    CounselorAppointment findByAppointmentId(String appointmentId);

    boolean existsByPatientRecordId(String patientRecordId);

    void deleteByAppointmentId(String appointmentId);

    Integer countByCounselorId(String counselorId);

    Integer countByStartDateTimeAfter(ZonedDateTime currentDateTime);

    Integer countByStartDateTimeBetween(ZonedDateTime startDateTime, ZonedDateTime endDateTime);

    Integer countBy();

    void deleteByPatientId(String patientId);

    void deleteByCounselorId(String counselorId);

    List<CounselorAppointment> findByCounselorId(String counselorId);
}