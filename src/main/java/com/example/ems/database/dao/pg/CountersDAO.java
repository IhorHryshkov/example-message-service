/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:14
 */
package com.example.ems.database.dao.pg;

import com.example.ems.dto.database.pg.Counters;
import com.example.ems.dto.database.pg.ids.CountersIds;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CountersDAO extends JpaRepository<Counters, CountersIds> {

	List<Counters> findByUserId(UUID userId);
}
