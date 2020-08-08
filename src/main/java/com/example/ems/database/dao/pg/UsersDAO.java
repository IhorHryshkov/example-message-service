/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:14
 */
package com.example.ems.database.dao.pg;

import com.example.ems.dto.database.pg.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UsersDAO extends JpaRepository<Users, Integer>, JpaSpecificationExecutor<Users> {
}
