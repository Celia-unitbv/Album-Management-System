package com.example.rest_api.controller;

import com.example.rest_api.database.users.model.PermissionEntity;
import com.example.rest_api.database.users.model.RoleEntity;
import com.example.rest_api.database.users.model.UserEntity;
import com.example.rest_api.service.PermissionService;
import com.example.rest_api.service.RoleService;
import com.example.rest_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;
    private  final PermissionService permissionService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService, PermissionService permissionService) {
        this.userService = userService;
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("welcomeMessage", "Hello Admin");
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String userManagement(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @GetMapping("/roles")
    public String roleManagement(Model model) {
        model.addAttribute("roles", roleService.findAll());
        return "admin/roles";
    }

    @GetMapping("/users/{id}/roles")
    public String updateUserRoles(@PathVariable Long id, Model model) {
        UserEntity user = userService.findById(id);
        List<RoleEntity> allRoles = roleService.findAll();

        model.addAttribute("user", user);
        model.addAttribute("allRoles", allRoles);

        return "admin/update-roles";
    }


    @PostMapping("/users/{id}/roles")
    public String updateUserRoles(@PathVariable Long id, @RequestParam List<Long> roleIds) {
        userService.updateUserRoles(id, roleIds);

        return "redirect:/admin/users";
    }

    @PostMapping("/roles/{id}/delete")
    public String deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return "redirect:/admin/roles";
    }

    @GetMapping("/roles/{id}/edit")
    public String editRoleForm(@PathVariable Long id, Model model) {
        RoleEntity role = roleService.findById(id);
        List<PermissionEntity> permissions = roleService.getPermissionsByRoleId(id); // Obține permisiunile asociate

        model.addAttribute("role", role);
        model.addAttribute("permissions", permissions);
        return "admin/edit-role";
    }

    @PostMapping("/roles/{id}/permissions/{permissionId}/delete")
    public String deletePermissionFromRole(@PathVariable Long id, @PathVariable Long permissionId) {
        permissionService.deletePermission(permissionId);
        return "redirect:/admin/roles/" + id + "/edit";
    }


    @PostMapping("/roles/{id}/edit")
    public String editRole(@PathVariable Long id, @RequestParam String name) {
        roleService.updateRoleName(id, name);
        return "redirect:/admin/roles";
    }

    @PostMapping("/roles/{id}/permissions/add")
    public String addPermissionToRole(
            @PathVariable Long id,
            @RequestParam String httpMethod,
            @RequestParam String url,
            Model model) {

        // Expresia regulată pentru un URL valid
        String urlPattern = "^(https?://|/)[a-zA-Z0-9/_-]+(\\*\\*|\\*)?$";

        if (!url.matches(urlPattern)) {
            model.addAttribute("errorMessage", "URL-ul nu este valid. Verificați formatul și încercați din nou.");
            RoleEntity role = roleService.findById(id);
            List<PermissionEntity> permissions = roleService.getPermissionsByRoleId(id);
            model.addAttribute("role", role);
            model.addAttribute("permissions", permissions);
            return "admin/edit-role";
        }

        // Adaugă permisiunea dacă URL-ul este valid
        permissionService.addPermissionToRole(id, httpMethod, url);
        return "redirect:/admin/roles/" + id + "/edit";
    }



}