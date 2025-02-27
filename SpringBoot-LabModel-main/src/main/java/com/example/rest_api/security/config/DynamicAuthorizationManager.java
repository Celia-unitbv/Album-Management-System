package com.example.rest_api.security.config;

import com.example.rest_api.database.users.model.RoleEntity;
import com.example.rest_api.service.RoleService;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.function.Supplier;

@Component
public class DynamicAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final RoleService roleService;
    private final AntPathMatcher pathMatcher;

    public DynamicAuthorizationManager(RoleService roleService) {
        this.roleService = roleService;
        this.pathMatcher = new AntPathMatcher();
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        String requestUrl = context.getRequest().getRequestURI();
        String requestMethod = context.getRequest().getMethod();

        List<RoleEntity> roles = roleService.findAll();

        for (RoleEntity role : roles) {
            if (authentication.get().getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals(role.getName()))) {

                boolean hasPermission = role.getPermissions().stream()
                        .anyMatch(permission ->
                                        pathMatcher.match(permission.getUrl(), requestUrl) &&
                                        permission.getHttpMethod().equalsIgnoreCase(requestMethod));


                if (hasPermission) {
                    return new AuthorizationDecision(true);
                }
            }
        }
        return new AuthorizationDecision(false);
    }
}
