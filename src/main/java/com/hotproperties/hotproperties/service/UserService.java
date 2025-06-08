package com.hotproperties.hotproperties.service;

import com.hotproperties.hotproperties.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;

import java.util.List;

public interface UserService {

    @PreAuthorize("hasAnyRole('BUYER', 'AGENT', 'ADMIN')")
    void prepareDashboardModel(Model model);

    @PreAuthorize("hasRole('ADMIN')")
    List<User> getAllUsers();

    //String storeProfilePicture(Long userId, MultipartFile file);

    User registerNewUser(User user, List<String> roleNames);

    @PreAuthorize("hasAnyRole('BUYER', 'AGENT', 'ADMIN')")
    void updateUser(User user);

    @PreAuthorize("hasAnyRole('BUYER', 'AGENT', 'ADMIN')")
    User getCurrentUser();

    String getRoleForUser(String username);

    @PreAuthorize("hasRole('ADMIN')")
    void deleteUserById(Long id);

    boolean existsByUsername(String username);

    @PreAuthorize("hasAnyRole('BUYER', 'AGENT', 'ADMIN')")
    void prepareProfileModel(Model model);
}
