package com.demo.shopapp.domain.user.service.role;

import com.demo.shopapp.domain.role.entity.Role;
import com.demo.shopapp.domain.role.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService implements IRoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> getAll() {
        return this.roleRepository.findAll();
    }
}
