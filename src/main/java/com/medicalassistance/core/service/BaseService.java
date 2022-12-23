package com.medicalassistance.core.service;

import com.medicalassistance.core.common.AuthorityName;
import com.medicalassistance.core.common.UserCommonService;
import com.medicalassistance.core.entity.User;
import com.medicalassistance.core.exception.AlreadyExistsException;
import com.medicalassistance.core.exception.InvalidUserRequestException;
import com.medicalassistance.core.mapper.UserMapper;
import com.medicalassistance.core.repository.UserRepository;
import com.medicalassistance.core.request.LoginRequest;
import com.medicalassistance.core.request.UpdatePasswordRequest;
import com.medicalassistance.core.request.UserRequest;
import com.medicalassistance.core.response.LoginResponse;
import com.medicalassistance.core.response.UpdatePasswordResponse;
import com.medicalassistance.core.security.JwtTokenUtil;
import com.medicalassistance.core.security.JwtUser;
import com.medicalassistance.core.util.EncryptionUtil;
import com.medicalassistance.core.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
public class BaseService {
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserCommonService userCommonService;

    @Autowired
    private PatientService patientService;

    public LoginResponse login(LoginRequest request, AuthorityName authorityName) {
        if (request == null) {
            return createErrorLoginResponse();
        }
        User savedUser = userRepository.findByEmailAddress(request.getEmailId().toLowerCase(Locale.ROOT));
        if (savedUser != null && savedUser.getAuthorities().contains(authorityName)) {
            if (savedUser.isDeleted()) {
                return this.createErrorLoginResponse("Your account was deleted! Please, contact administration!");
            }
            if (checkValidLogin(savedUser, request.getPassword())) {
                return this.createSuccessLoginResponse(savedUser);
            } else {
                return this.createErrorLoginResponse();
            }
        } else {
            return this.createErrorLoginResponse("User doesn't exist. Please sign up.");
        }
    }

    public LoginResponse signUp(UserRequest userRequest, AuthorityName authorityName) {
        return signUp(userRequest, authorityName, false);
    }

    public LoginResponse signUp(UserRequest userRequest, AuthorityName authorityName, boolean isPasswordAutoGenerated) {
        User user = userMapper.fromPatientRequest(userRequest);
        if (user == null) {
            return this.createErrorLoginResponse("Invalid user request");
        } else if (user.getEmailAddress() == null) {
            return this.createErrorLoginResponse("Invalid email address");
        } else if (user.getPassword() == null) {
            return this.createErrorLoginResponse("Invalid user password");
        } else if (user.getDateOfBirth() == null) {
            return this.createErrorLoginResponse("Invalid date of birth");
        } else if (user.getFullName() == null) {
            return this.createErrorLoginResponse("Invalid full name");
        } else if (user.getCity() == null) {
            return this.createErrorLoginResponse("Invalid city");
        } else if (user.getCountry() == null) {
            return this.createErrorLoginResponse("Invalid country");
        } else if (user.getPhoneNumber() == null) {
            return this.createErrorLoginResponse("Invalid phone number");
        } else if (user.getProvince() == null) {
            return this.createErrorLoginResponse("Invalid province");
        } else if (
            // If user has ROLE_COUNSELOR or ROLE_DOCTOR, then they are required to have UNIQUE registration number
                authorityName == AuthorityName.ROLE_COUNSELOR || authorityName == AuthorityName.ROLE_DOCTOR) {
            if (user.getRegistrationNumber() == null) {
                return this.createErrorLoginResponse("invalid registration number");
            }
            // check if the registration number is unique
            if (userRepository.existsByRegistrationNumberAndDeletedFalse(user.getRegistrationNumber())) {
                return this.createErrorLoginResponse("registration number is already in use");
            }
        }

        try {
            this.checkIfEmailIsTakenWithException(user.getEmailAddress());
        } catch (AlreadyExistsException e) {
            return this.createErrorLoginResponse(e.getMessage());
        }

        Set<AuthorityName> authorities = new HashSet<>();
        authorities.add(authorityName);
        user.setAuthorities(authorities);
        user.setCreatedAt(TimeUtil.nowUTC());
        user.setUpdatedAt(TimeUtil.nowUTC());
        user.setDeleted(false);
        user.setLastPasswordResetDate(new Date());
        user.setPasswordAutoGenerated(isPasswordAutoGenerated);

        // For encrypting the password
        String encPassword = EncryptionUtil.encryptPassword(user.getPassword());
        if (encPassword != null) {
            user.setPassword(encPassword);
        }

        User savedUser = userRepository.save(user);
        return this.createSuccessLoginResponse(savedUser);
    }

    private boolean checkIfEmailIsTaken(String email) {
        return userRepository.existsByEmailAddressAndDeletedFalse(email);
    }

    public void checkIfEmailIsTakenWithException(String email) {
        if (this.checkIfEmailIsTaken(email)) {
            throw new AlreadyExistsException("User already exists");
        }
    }

    public LoginResponse createSuccessLoginResponse(User savedUser) {
        LoginResponse response = new LoginResponse();
        response.setUser(userMapper.toUserResponse(savedUser));
        response.setStatus(patientService.getPatientRecordStatus(savedUser));
        response.setLoginSuccess(true);
        JwtUser userDetails = (JwtUser) userDetailsService.loadUserByUsername(savedUser.getEmailAddress());
        response.setAccessToken(jwtTokenUtil.generateToken(userDetails));
        return response;
    }

    public LoginResponse createErrorLoginResponse() {
        return this.createErrorLoginResponse("Wrong credentials!");
    }

    public LoginResponse createErrorLoginResponse(String errorMessage) {
        LoginResponse response = new LoginResponse();
        response.setLoginSuccess(false);
        response.setErrorMessage(errorMessage);
        return response;
    }

    private boolean checkValidLogin(User user, String password) {
        String userPassword = user.getPassword();
        return userPassword != null && EncryptionUtil.isValidPassword(password, userPassword);
    }

    public boolean validatePasswordResetToken(String token) {
        String userName = jwtTokenUtil.getUsernameFromToken(token);
        User savedUser = userRepository.findByEmailAddressAndDeletedFalse(userName);

        if (savedUser != null) {
            JwtUser userDetails = (JwtUser) userDetailsService.loadUserByUsername(savedUser.getEmailAddress());
            return jwtTokenUtil.validateResetToken(token, userDetails);
        }
        return false;
    }

    public UpdatePasswordResponse updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        UpdatePasswordResponse response = new UpdatePasswordResponse();
        if (updatePasswordRequest != null && updatePasswordRequest.getPassword() != null && !updatePasswordRequest.getPassword().isEmpty()) {
            User user = userCommonService.getUser();
            if (EncryptionUtil.isValidPassword(updatePasswordRequest.getPassword(), user.getPassword())) {
                throw new InvalidUserRequestException("new password cannot be the same as the current password");
            }
            String encryptedPassword = EncryptionUtil.encryptPassword(updatePasswordRequest.getPassword());
            user.setPassword(encryptedPassword);
            user.setPasswordAutoGenerated(false);
            userRepository.save(user);
            response.setSuccess(true);
            response.setEmailAddress(user.getEmailAddress());
        } else {
            throw new InvalidUserRequestException("Invalid request!");
        }
        return response;
    }
}