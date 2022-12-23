package com.medicalassistance.core.response.projection;

import com.medicalassistance.core.entity.User;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "userProjection", types = User.class)
public interface UserProjection {
    String getFullName();

    String getEmailAddress();
}
