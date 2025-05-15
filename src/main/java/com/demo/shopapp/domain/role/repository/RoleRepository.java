package com.demo.shopapp.domain.role.repository;

import com.demo.shopapp.domain.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    List<Role> findAll();
    Optional<Role> findRoleById(long id);

    List<Role> findByNameIn(Set<String> names);
}
