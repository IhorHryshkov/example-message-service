/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:14
 */
package com.example.ems.database.dao.pg;

import com.example.ems.dto.database.pg.Types;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/** JPA interface for work with Types entity ${@link Types} */
public interface TypesDAO extends JpaRepository<Types, Integer>, JpaSpecificationExecutor<Types> {
  /**
   * Find types by name and ignore case
   *
   * @param name Name of type
   * @return result is list of types
   */
  List<Types> findByNameIgnoreCase(String name);
}
