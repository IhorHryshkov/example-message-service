/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:14
 */
package com.example.ems.database.dao.pg;

import com.example.ems.dto.database.pg.Users;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UsersDAO extends JpaRepository<Users, UUID>, JpaSpecificationExecutor<Users> {
  List<Users> findByStatusNameIgnoreCaseAndUsername(String name, String username);

  List<Users> findByUsername(String username);
}
