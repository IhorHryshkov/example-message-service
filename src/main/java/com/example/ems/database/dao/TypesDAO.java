/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:14
 */
package com.example.ems.database.dao;

import com.example.ems.database.models.Types;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypesDAO extends JpaRepository<Types, Integer> {

}
