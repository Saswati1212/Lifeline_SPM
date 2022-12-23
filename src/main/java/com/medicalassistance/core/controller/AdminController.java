package com.medicalassistance.core.controller;

import com.medicalassistance.core.common.AuthorityName;
import com.medicalassistance.core.converter.ZonedDateTimeReadConverter;
import com.medicalassistance.core.entity.Assessment;
import com.medicalassistance.core.request.DummyUsers;
import com.medicalassistance.core.request.LoginRequest;
import com.medicalassistance.core.request.UserRequest;
import com.medicalassistance.core.response.*;
import com.medicalassistance.core.security.JwtTokenUtil;
import com.medicalassistance.core.service.AdminService;
import com.medicalassistance.core.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/v1/admin")
public class AdminController {
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AdminService adminService;

    @Autowired
    private BaseService baseService;

    @Autowired
    private ZonedDateTimeReadConverter zonedDateTimeReadConverter;

    @RequestMapping(value = "/assessment", method = RequestMethod.POST)
    public void createAssessment(@Valid @RequestBody Assessment assessment) {
        adminService.createAssessment(assessment);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return baseService.login(request, AuthorityName.ROLE_ADMIN);
    }

    @RequestMapping(value = "/patient", method = RequestMethod.GET)
    public Page<AdminPatientCard> getPatients(@RequestParam(defaultValue = "0") Integer page,
                                              @RequestParam(defaultValue = "10") Integer size) {
        Pageable paging = PageRequest.of(page, size);
        return adminService.getPatients(paging);
    }

    @RequestMapping(value = "/counselor", method = RequestMethod.GET)
    public Page<AdminCounselorCard> getCounselors(@RequestParam(defaultValue = "0") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        Pageable paging = PageRequest.of(page, size);
        return adminService.getCounselors(paging);
    }

    @RequestMapping(value = "/doctor", method = RequestMethod.GET)
    public Page<AdminDoctorCard> getDoctors(@RequestParam(defaultValue = "0") Integer page,
                                            @RequestParam(defaultValue = "10") Integer size) {
        Pageable paging = PageRequest.of(page, size);
        return adminService.getDoctors(paging);
    }

    @RequestMapping(value = "/patient", method = RequestMethod.POST)
    public AdminUserCreateResponse createPatient(@Valid @RequestBody UserRequest userRequest) {
        return adminService.createPatient(userRequest);
    }

    @RequestMapping(value = "/counselor", method = RequestMethod.POST)
    public AdminUserCreateResponse createCounselor(@Valid @RequestBody UserRequest userRequest) {
        return adminService.createCounselor(userRequest);
    }

    @RequestMapping(value = "/doctor", method = RequestMethod.POST)
    public AdminUserCreateResponse createDoctor(@Valid @RequestBody UserRequest userRequest) {
        return adminService.createDoctor(userRequest);
    }

    @RequestMapping(value = "/report", method = RequestMethod.GET)
    public AdminPatientReport getAdminPatientReport(@RequestParam Long startDateTime,
                                                    @RequestParam Long endDateTime) {
        return adminService.getAdminPatientReportByRange(
                zonedDateTimeReadConverter.convert(startDateTime),
                zonedDateTimeReadConverter.convert(endDateTime));
    }

    @RequestMapping(value = "/report-parameters", method = RequestMethod.GET)
    public AdminPatientReportParameters getAdminPatientReportParameters() {
        return adminService.getAdminPatientReportParameters();
    }

    @RequestMapping(value = "/patient/{email}", method = RequestMethod.DELETE)
    public void removePatient(@PathVariable String email) {
        adminService.removePatient(email);
    }

    @RequestMapping(value = "/counselor/{email}", method = RequestMethod.DELETE)
    public void removeCounselor(@PathVariable String email) {
        adminService.removeCounselor(email);
    }

    @RequestMapping(value = "/doctor/{email}", method = RequestMethod.DELETE)
    public void removeDoctor(@PathVariable String email) {
        adminService.removeDoctor(email);
    }

    @RequestMapping(value = "/reset", method = RequestMethod.DELETE)
    public void resetUsers() {
        adminService.resetUsers();
    }

    @RequestMapping(value = "/dummy", method = RequestMethod.POST)
    public void dummyData(@Valid @RequestBody DummyUsers dummyUsers) {
        adminService.insertDummyUsers(dummyUsers);
    }
}