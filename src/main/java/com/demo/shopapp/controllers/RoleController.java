package com.demo.shopapp.controllers;

import com.demo.shopapp.entities.Product;
import com.demo.shopapp.entities.Role;
import com.demo.shopapp.exceptions.DataNotFoundException;
import com.demo.shopapp.responses.ResponseObject;
import com.demo.shopapp.responses.product.ProductResponse;
import com.demo.shopapp.services.role.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
