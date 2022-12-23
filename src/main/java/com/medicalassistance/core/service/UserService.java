package com.medicalassistance.core.service;

import com.medicalassistance.core.common.UserCommonService;
import com.medicalassistance.core.entity.User;
import com.medicalassistance.core.mapper.UserMapper;
import com.medicalassistance.core.repository.UserRepository;
import com.medicalassistance.core.request.UserUpdateRequest;
import com.medicalassistance.core.response.UserProfileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    UserCommonService userCommonService;

    @Autowired
    UserRepository userRepository;

    public UserProfileResponse getProfileCard() {
        User user = userCommonService.getUser();
        return userMapper.toUserProfileResponse(user);
    }

    public UserProfileResponse updateProfile(UserUpdateRequest userUpdateRequest) {
        User updatedUser = userMapper.fromUserUpdateRequest(userUpdateRequest);
        return userMapper.toUserProfileResponse(userRepository.save(updatedUser));
    }
}
