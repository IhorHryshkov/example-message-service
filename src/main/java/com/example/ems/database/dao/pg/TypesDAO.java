/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:14
 */
package com.example.ems.database.dao.pg;

import com.example.ems.dto.database.pg.Types;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TypesDAO extends JpaRepository<Types, Integer> {
	List<Types> findByNameIgnoreCase(String name);
}
