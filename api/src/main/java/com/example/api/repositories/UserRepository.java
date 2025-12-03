package com.example.api.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import com.example.api.entities.Organization;
import com.example.api.entities.User;
import com.example.api.entities.enums.Role;
import com.example.api.entities.enums.UserStatus;

@Repository
public interface UserRepository extends CrudRepository<User, UUID>, PagingAndSortingRepository<User, UUID> {
    //Iterable<User> findByOwner(UUID owner);

    // default Iterable<User> findAll() {
    //     SecurityContext context = SecurityContextHolder.getContext();
    //     Authentication authentication = context.getAuthentication();
    //     String owner = authentication.getName();
    //     return findByOwner(owner)
    // }
    default boolean softDelete(User userToBeDeleted) {
        userToBeDeleted.setStatus(UserStatus.IS_UNACTIVE);
        return true;
    }
    Boolean existsByRole(Role role);
    List<User> findByRole(Role role);
    User findByUserId(UUID userId);
    User findByEmail(String email);
    User findByVerificationToken(String verificationToken);
    User findByResetPasswordToken(String resetPasswordToken);
    
    List<User> findByOrganizationAndRole(Organization organization, Role role);

    default boolean suspend(UUID uuidOfUserSusending, User userToBeSuspended) {
        userToBeSuspended.setStatus(UserStatus.SUSPENDED);
        userToBeSuspended.setSuspendedBy(uuidOfUserSusending.toString());
        userToBeSuspended.setSuspendedDate(LocalDateTime.now());

        return true;
    }
}
