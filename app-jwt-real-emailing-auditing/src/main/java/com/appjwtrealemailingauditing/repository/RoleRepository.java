package com.appjwtrealemailingauditing.repository;

import com.appjwtrealemailingauditing.entity.Role;
import com.appjwtrealemailingauditing.entity.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByRoleName(RoleName roleName);
}
