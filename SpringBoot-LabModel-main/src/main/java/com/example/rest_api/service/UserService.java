package com.example.rest_api.service;

import com.example.rest_api.database.users.model.PermissionEntity;
import com.example.rest_api.database.users.model.RoleEntity;
import com.example.rest_api.database.users.model.UserEntity;
import com.example.rest_api.database.users.repository.RoleRepository;
import com.example.rest_api.database.users.repository.UserRepository;
import com.example.rest_api.security.AuthenticatedUser;
import com.example.rest_api.security.PasswordGeneratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService extends OidcUserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;

    }

    /* Authentication methods */
    /**
     * Classic Auth.
     * @param email
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity entity = userRepository.findByEmail(email)
                /* TODO: Find a way to communicated this to the user */
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return entity.toAuthenticatedUser();
    }

    /**
     * Used for oAuth Auth.
     * @param userRequest
     * @return
     * @throws OAuth2AuthenticationException
     */
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // Delegates to the default OidcUserService for the basic OidcUser
        OidcUser oidcUser = super.loadUser(userRequest);

        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();

        // Check if user exists in DB, if not, create it
        Optional<UserEntity> optUser = userRepository.findByEmail(email);
        if (optUser.isEmpty()) {
            UserEntity newUser = new UserEntity();
            newUser.setEmail(email);
            newUser.setUsername(name);
            newUser.setPassword(PasswordGeneratorUtil.generate());
            newUser.setIsOAuthAccount(true);
            newUser.setRoles(roleRepository.findAllByName("USER"));
            this.save(newUser);
            return newUser.toAuthenticatedUser();
        }

        AuthenticatedUser authenticatedUser = optUser.get().toAuthenticatedUser();
        authenticatedUser.setIdToken(oidcUser.getIdToken());
        return authenticatedUser;
    }

    /* Queries */
    public UserEntity save(UserEntity user) {
        /* Encrypt password */
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void updateUserRoles(Long userId, List<Long> roleIds) {
        // Șterge relațiile existente
        userRepository.deleteRolesByUserId(userId);

        // Adaugă noile relații
        for (Long roleId : roleIds) {
            userRepository.addRoleToUser(userId, roleId);
        }
    }


    public Boolean existsByEmail(String email){
        return this.userRepository.existsByEmail(email);
    }

    public List<UserEntity> findAll() {
        return this.userRepository.findAll();
    }

    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
    public UserEntity findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public List<PermissionEntity> getUserPermissions(String email) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found with email: " + email);
        }

        UserEntity user = userOptional.get();

        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public void refreshAuthentication(UserEntity user) {
        // Încarcă autoritățile actualizate folosind map
        Collection<GrantedAuthority> updatedAuthorities = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getHttpMethod() + " " + permission.getUrl()))
                .collect(Collectors.toSet());

        // Reîmprospătează Authentication în SecurityContext
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                user,
                currentAuth.getCredentials(),
                updatedAuthorities
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }


}
