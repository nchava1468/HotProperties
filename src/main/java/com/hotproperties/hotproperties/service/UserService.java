package com.hotproperties.hotproperties.service;

import com.hotproperties.hotproperties.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;

import java.util.List;

public interface UserService {

    @PreAuthorize("isAuthenticated()")
    void prepareDashboardModel(Model model);

    //@PreAuthorize("isAuthenticated()")
    //void prepareProfileModel(Model model);

    //@PreAuthorize("isAuthenticated()")
    //void prepareSettingsModel(Model model);

    //@PreAuthorize("isAuthenticated()")
    //void updateUserSettings(User updatedUser, String password, List<Long> addIds, List<Long> removeIds);

    @PreAuthorize("hasRole('ADMIN')")
    List<User> getAllUsers();

    //@PreAuthorize("hasRole('MANAGER')")
    //List<User> getTeamForCurrentManager();

    //String storeProfilePicture(Long userId, MultipartFile file);

    User registerNewUser(User user, List<String> roleNames);

    void updateUser(User user);

    @PreAuthorize("isAuthenticated()")
    User getCurrentUser();

    String getRoleForUser(String username);

    void deleteUserById(Long id);

    boolean existsByUsername(String username);

    void prepareProfileModel(Model model);

    //void createNewAgent(User agent);
}
