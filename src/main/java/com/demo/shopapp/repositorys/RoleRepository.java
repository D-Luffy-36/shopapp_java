package com.demo.shopapp.repositorys;

import com.demo.shopapp.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    List<Role> findAll();
    Optional<Role> findRoleById(long id);
}
