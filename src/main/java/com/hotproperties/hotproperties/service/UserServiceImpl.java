package com.hotproperties.hotproperties.service;

import com.hotproperties.hotproperties.entity.*;
import com.hotproperties.hotproperties.exceptions.InvalidUserParameterException;
import com.hotproperties.hotproperties.repository.RoleRepository;
import com.hotproperties.hotproperties.repository.UserRepository;
import com.hotproperties.hotproperties.util.CurrentUserContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
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

    @Override
    public void prepareProfileModel(Model model) {
        CurrentUserContext context = getCurrentUserContext();
        model.addAttribute("user", context.user());
        model.addAttribute("authorization", context.auth());
    }

    @Override
    public void updateUser(User updatedUser) {
        User user = getCurrentUserContext().user();

        validateFirstName(updatedUser.getFirstName());
        validateLastName(updatedUser.getLastName());
        validateEmail(updatedUser.getEmail());

        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());

        userRepository.save(user);
    }

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
                .orElse("ROLE_BUYER");
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {

        User user = userRepository.findById(id).orElseThrow();

        for (Favorite favorite : user.getFavorites()) {
            Property property = favorite.getProperty();
            property.getFavorites().remove(favorite);
        }

        for (Message message : user.getMessages()) {
            Property property = message.getProperty();
            property.getMessages().remove(message);
        }

        user.getFavorites().clear();
        user.getMessages().clear();

        userRepository.delete(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    // === VALIDATION METHODS ===

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty() || existsByUsername(username)) {
            throw new InvalidUserParameterException("Username is required and must be unique");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidUserParameterException("Password is required");
        }
    }

    private void validateFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new InvalidUserParameterException("First name is required");
        }
    }

    private void validateLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new InvalidUserParameterException("Last name is required");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidUserParameterException("Email is required");
        }
    }

    private void validateRoles(List<String> roles) {
        if (roles == null || roles.size() != 1) {
            throw new InvalidUserParameterException("Exactly one role must be assigned");
        }
    }
}
