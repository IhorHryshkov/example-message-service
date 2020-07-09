/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:14
 */
package com.example.ems.database.dao;

import com.example.ems.database.models.Counters;
import com.example.ems.database.models.ids.CountersIds;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CountersDAO extends JpaRepository<Counters, CountersIds> {

	List<Counters> findByUserId(UUID userId);
}
