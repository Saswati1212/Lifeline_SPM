package com.medicalassistance.core.common;

import com.medicalassistance.core.entity.User;
import com.medicalassistance.core.exception.ResourceNotFoundException;
import com.medicalassistance.core.repository.UserRepository;
import com.medicalassistance.core.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCommonService {
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    UserRepository userRepository;

    public User getUser() {
        String userId = jwtTokenUtil.getLoggedInUserID();
        User user = userRepository.findByUserIdAndDeletedFalse(userId);
        if (user != null) {
            return user;
        } else {
            throw new ResourceNotFoundException("User not found!");
        }
    }
}
