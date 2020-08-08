/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:14
 */
package com.example.ems.database.dao.pg;

import com.example.ems.dto.database.pg.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusDAO extends JpaRepository<Status, Integer> {

}