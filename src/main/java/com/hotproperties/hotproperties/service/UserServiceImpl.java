package com.hotproperties.hotproperties.service;

import com.hotproperties.hotproperties.entity.Role;
import com.hotproperties.hotproperties.entity.User;
import com.hotproperties.hotproperties.repository.RoleRepository;
import com.hotproperties.hotproperties.repository.UserRepository;
import com.hotproperties.hotproperties.util.CurrentUserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private CurrentUserContext getCurrentUserContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new CurrentUserContext(user, auth);
    }

    @Override
    public void prepareDashboardModel(Model model) {
        CurrentUserContext context = getCurrentUserContext();
        model.addAttribute("user", context.user());
        model.addAttribute("authorization", context.auth());
    }
/*
    @Override
    public void prepareProfileModel(Model model) {
        model.addAttribute("user", getCurrentUserContext().user());
    }

    @Override
    public void prepareSettingsModel(Model model) {
        CurrentUserContext context = getCurrentUserContext();
        User user = context.user();
        Authentication auth = context.auth();

        model.addAttribute("user", user);

        boolean isManager = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));

        if (isManager) {
            List<User> currentEmployees = userRepository.findByManager(user);
            List<User> availableUsers = userRepository.findByManagerIsNull().stream()
                    .filter(u -> !u.getUsername().equals(user.getUsername()))
                    .collect(Collectors.toList());

            model.addAttribute("currentEmployees", currentEmployees);
            model.addAttribute("availableUsers", availableUsers);
        }
    }

    @Override
    public void updateUserSettings(User updatedUser, String password, List<Long> addIds, List<Long> removeIds) {
        User user = getCurrentUserContext().user();

        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());

        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        if (addIds != null) {
            for (Long empId : addIds) {
                User emp = userRepository.findById(empId).orElseThrow();
                emp.setManager(user);
                userRepository.save(emp);
            }
        }

        if (removeIds != null) {
            for (Long empId : removeIds) {
                User emp = userRepository.findById(empId).orElseThrow();
                emp.setManager(null);
                userRepository.save(emp);
            }
        }

        userRepository.save(user);
    }

    @Override
    public User registerNewUser(User user, List<String> roleNames) {
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        Set<Role> roles = roleNames.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                .collect(Collectors.toSet());

        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }*/

    public User registerNewUser(User user, List<String> roles) {

        validateUsername(user.getUsername());
        validatePassword(user.getPassword());
        validateFirstName(user.getFirstName());
        validateLastName(user.getLastName());
        validateEmail(user.getEmail());
        validateRoles(roles);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(roles.stream()
                .map(role -> roleRepository.findByName(role)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + role)))
                .collect(Collectors.toSet()));

        return userRepository.save(user);
    }



    @Override
    public List<User> getAllUsers() {
        return userRepository.findAllByOrderByLastNameAsc();
    }
/*
    @Override
    public List<User> getTeamForCurrentManager() {
        return userRepository.findByManager(getCurrentUserContext().user());
    }

    @Override
    public String storeProfilePicture(Long userId, MultipartFile file) {
        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

            // Resolve absolute path relative to the project directory
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads", "profile-pictures");
            Files.createDirectories(uploadPath);  // Ensure path exists

            // Locate user and remove previous image (if any)
            User user = userRepository.findById(userId).orElseThrow();

            if (user.getProfilePicture() != null && !user.getProfilePicture().equals("default.jpg")) {
                Path oldPath = uploadPath.resolve(user.getProfilePicture());
                Files.deleteIfExists(oldPath);
            }

            Path filePath = uploadPath.resolve(filename);
            file.transferTo(filePath.toFile());

            // Save to user
            user.setProfilePicture(filename);
            userRepository.save(user);

            return filename;

        } catch (IOException ex) {
            System.out.println("Failed to save file: " + ex.getMessage());
            throw new RuntimeException("Failed to store profile picture", ex);
        }
    }
*/

    @Override
    public void updateUser(User savedUser) {
        userRepository.save(savedUser);
    }

    @Override
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Override
    public String getRoleForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return user.getRoles().stream()
                .map(Role::getName)
                .findFirst()
                .orElse("ROLE_USER");
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
/*
    @Override
    public void createNewAgent(User agent) {
        if (agent.getUsername() == null || agent.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }

        if (agent.getPassword() == null || agent.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (agent.getEmail() == null || agent.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (agent.getFirstName() == null || agent.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }

        if (agent.getLastName() == null || agent.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }

        if (existsByUsername(agent.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        registerNewUser(agent, List.of("ROLE_AGENT"));
    }*/

    // === VALIDATION METHODS ===

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty() || userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username is required and must be unique");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
    }

    private void validateFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
    }

    private void validateLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
    }

    private void validateRoles(List<String> roles) {
        if (roles == null || roles.size() != 1) {
            throw new IllegalArgumentException("Exactly one role must be assigned");
        }
    }

}
