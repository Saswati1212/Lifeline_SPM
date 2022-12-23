package com.medicalassistance.core.repository;

import com.medicalassistance.core.common.AuthorityName;
import com.medicalassistance.core.entity.User;
import com.medicalassistance.core.response.CounselorDoctorCardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

public interface UserRepository extends MongoRepository<User, String> {
    User findByUserIdAndDeletedFalse(String userId);

    User findByEmailAddress(String emailAddress);

    User findByEmailAddressAndDeletedFalse(String emailAddress);

    User findByEmailAddressAndAuthoritiesContainsAndDeletedFalse(String emailAddress, Set<AuthorityName> authorityNames);

    boolean existsByEmailAddressAndDeletedFalse(String emailAddress);

    boolean existsByUserIdAndDeletedFalse(String userId);

    Page<User> findByAuthoritiesContainsAndDeletedFalseOrderByCreatedAtDesc(AuthorityName authorities, Pageable pageable);

    List<User> findByAuthoritiesContainsAndCreatedAtBetweenAndDeletedFalseOrderByCreatedAt(Set<AuthorityName> authorities, ZonedDateTime createdAt, ZonedDateTime createdAt2);

    List<User> findByAuthoritiesContainsAndCreatedAtAndDeletedFalseOrderByCreatedAt(Set<AuthorityName> authorities, ZonedDateTime createdAt);

    Integer countByAuthoritiesContains(Set<AuthorityName> authorities);

    boolean existsByRegistrationNumberAndDeletedFalse(String registrationNumber);

    CounselorDoctorCardResponse findFirstByRegistrationNumberAndDeletedFalse(String registrationNumber);
}