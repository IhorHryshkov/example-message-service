/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:14
 */
package com.example.ems.database.dao.pg;

import com.example.ems.dto.database.pg.Status;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/** JPA interface for work with Status entity ${@link Status} */
public interface StatusDAO
    extends JpaRepository<Status, Integer>, JpaSpecificationExecutor<Status> {
  /**
   * Find statuses by name and ignore case
   *
   * @param name Name of status
   * @return result is list of statuses
   */
  List<Status> findByNameIgnoreCase(String name);
}
