package com.demo.shopapp.services.role;

import com.demo.shopapp.entities.Role;
import com.demo.shopapp.repositorys.RoleRepository;
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
