package com.medicalassistance.core.mapper;


import com.medicalassistance.core.common.AuthorityName;
import com.medicalassistance.core.common.UserCommonService;
import com.medicalassistance.core.converter.ZonedDateTimeReadConverter;
import com.medicalassistance.core.entity.User;
import com.medicalassistance.core.repository.ActivePatientRepository;
import com.medicalassistance.core.repository.AssignedPatientRepository;
import com.medicalassistance.core.repository.CounselorAppointmentRepository;
import com.medicalassistance.core.repository.UserRepository;
import com.medicalassistance.core.request.UserRequest;
import com.medicalassistance.core.request.UserUpdateRequest;
import com.medicalassistance.core.response.*;
import com.medicalassistance.core.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class UserMapper {
    @Autowired
    UserCommonService userCommonService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AssignedPatientRepository assignedPatientRepository;

    @Autowired
    ActivePatientRepository activePatientRepository;

    @Autowired
    CounselorAppointmentRepository counselorAppointmentRepository;

    @Autowired
    ZonedDateTimeReadConverter readConverter;

    public User fromPatientRequest(UserRequest userRequest) {
        User user = new User();
        user.setFullName(userRequest.getFullName());
        user.setEmailAddress(userRequest.getEmailAddress());
        user.setPassword(userRequest.getPassword());
        user.setAddressLine(userRequest.getAddressLine());
        user.setCity(userRequest.getCity());
        user.setProvince(userRequest.getProvince());
        user.setCountry(userRequest.getCountry());
        user.setDateOfBirth(userRequest.getDateOfBirth());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setRegistrationNumber(userRequest.getRegistrationNumber());
        return user;
    }

    public UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setFullName(user.getFullName());
        response.setEmailAddress(user.getEmailAddress());
        response.setAddressLine(user.getAddressLine());
        response.setCity(user.getCity());
        response.setProvince(user.getProvince());
        response.setCountry(user.getCountry());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAge(TimeUtil.nowUTC().getYear() - user.getDateOfBirth().getYear());
        return response;
    }

    public UserProfileResponse toUserProfileResponse(User user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setFullName(user.getFullName());
        response.setEmailAddress(user.getEmailAddress());
        response.setAddressLine(user.getAddressLine());
        response.setCity(user.getCity());
        response.setProvince(user.getProvince());
        response.setCountry(user.getCountry());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setRegistrationNumber(user.getRegistrationNumber());
        return response;
    }

    public UserCardResponse toUserCardResponse(User user) {
        UserCardResponse response = new UserCardResponse();
        response.setFullName(user.getFullName());
        response.setEmailAddress(user.getEmailAddress());
        response.setPhoneNumber(user.getPhoneNumber());
        return response;
    }

    public CounselorDoctorCardResponse toCounselorDoctorCardResponse(User user) {
        CounselorDoctorCardResponse response = new CounselorDoctorCardResponse();
        response.setFullName(user.getFullName());
        response.setEmailAddress(user.getEmailAddress());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setRegistrationNumber(user.getRegistrationNumber());
        if (user.getRegistrationNumber() != null)
            response.setCurrentPatients(assignedPatientRepository.countByDoctorRegistrationNumber(response.getRegistrationNumber()));
        return response;
    }

    public User fromUserUpdateRequest(UserUpdateRequest userUpdateRequest) {
        User user = userCommonService.getUser();

        if (user.getAuthorities().contains(AuthorityName.ROLE_COUNSELOR) || user.getAuthorities().contains(AuthorityName.ROLE_DOCTOR)) {
            user.setRegistrationNumber(userUpdateRequest.getRegistrationNumber());
        }

        if (userUpdateRequest.getFullName() != null && !userUpdateRequest.getFullName().isEmpty())
            user.setFullName(userUpdateRequest.getFullName());
        if (userUpdateRequest.getAddressLine() != null && !userUpdateRequest.getAddressLine().isEmpty())
            user.setAddressLine(userUpdateRequest.getAddressLine());
        if (userUpdateRequest.getCity() != null && !userUpdateRequest.getCity().isEmpty())
            user.setCity(userUpdateRequest.getCity());
        if (userUpdateRequest.getCountry() != null && !userUpdateRequest.getCountry().isEmpty())
            user.setCountry(userUpdateRequest.getCountry());
        if (userUpdateRequest.getProvince() != null && !userUpdateRequest.getProvince().isEmpty())
            user.setProvince(userUpdateRequest.getProvince());
        if (userUpdateRequest.getPhoneNumber() != null && !userUpdateRequest.getPhoneNumber().isEmpty())
            user.setPhoneNumber(userUpdateRequest.getPhoneNumber());
        if (userUpdateRequest.getDateOfBirth() != null)
            user.setDateOfBirth(userUpdateRequest.getDateOfBirth());
        return user;
    }

    public AdminPatientCard toAdminPatientCard(User user) {
        AdminPatientCard adminPatientCard = new AdminPatientCard(toUserCardResponse(user));
        adminPatientCard.setCreatedAt(user.getCreatedAt());
        return adminPatientCard;
    }

    public AdminCounselorCard toAdminCounselorCard(User user) {
        AdminCounselorCard adminCounselorCard = new AdminCounselorCard(toUserCardResponse(user));
        adminCounselorCard.setCreatedAt(user.getCreatedAt());
        return adminCounselorCard;
    }

    public AdminDoctorCard toAdminDoctorCard(User user) {
        AdminDoctorCard adminDoctorCard = new AdminDoctorCard(toUserCardResponse(user));
        adminDoctorCard.setCreatedAt(user.getCreatedAt());
        return adminDoctorCard;
    }
}
