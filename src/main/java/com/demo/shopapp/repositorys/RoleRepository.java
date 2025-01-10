package com.demo.shopapp.repositorys;

import com.demo.shopapp.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findRoleById(long id);
}
