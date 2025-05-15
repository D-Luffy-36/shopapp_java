package com.demo.shopapp.domain.role.controller;

import com.demo.shopapp.domain.role.entity.Role;
import com.demo.shopapp.shared.response.ResponseObject;
import com.demo.shopapp.domain.user.service.role.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseObject<List<Role>> listAllRoles() {
        return ResponseObject.<List<Role>>builder()
                .message("successfully")
                .status(HttpStatus.OK)
                .data(
                        this.roleService.getAll()
                )
                .build();
    };
}
