package com.medicalassistance.core.controller;

import com.medicalassistance.core.common.AuthorityName;
import com.medicalassistance.core.request.*;
import com.medicalassistance.core.response.*;
import com.medicalassistance.core.security.JwtTokenUtil;
import com.medicalassistance.core.service.AssessmentService;
import com.medicalassistance.core.service.BaseService;
import com.medicalassistance.core.service.PatientService;
import com.medicalassistance.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/patient")
public class PatientController {
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    private BaseService baseService;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private PatientService patientService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return baseService.login(request, AuthorityName.ROLE_PATIENT);
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public LoginResponse signup(@Valid @RequestBody UserRequest request) {
        return baseService.signUp(request, AuthorityName.ROLE_PATIENT);
    }

    @RequestMapping(value = "/assessment/{assessmentId}", method = RequestMethod.GET)
    public AssessmentResponse getAssessment(@PathVariable String assessmentId) {
        return assessmentService.getAssessment(assessmentId);
    }

    @RequestMapping(value = "/assessment/{assessmentId}", method = RequestMethod.POST)
    public void storeAssessment(@PathVariable String assessmentId, @Valid @RequestBody AssessmentResultRequest assessmentResultRequest) {
        assessmentService.storeAssessmentResult(assessmentId, assessmentResultRequest);
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public PatientRecordStatusResponse getStatus() {
        return patientService.getPatientRecordStatus();
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public UserProfileResponse getProfileCard() {
        return userService.getProfileCard();
    }

    @RequestMapping(value = "/profile", method = RequestMethod.PATCH)
    public UserProfileResponse updateProfile(@RequestBody UserUpdateRequest userUpdateRequest) {
        return userService.updateProfile(userUpdateRequest);
    }

    @RequestMapping(value = "/update-password", method = RequestMethod.POST)
    UpdatePasswordResponse createPasswordResetRequest(
            @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest) {
        return baseService.updatePassword(updatePasswordRequest);
    }
}