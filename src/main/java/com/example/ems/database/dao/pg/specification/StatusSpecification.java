package com.example.ems.database.dao.pg.specification;

import com.example.ems.dto.database.pg.Status;
import com.example.ems.dto.network.controller.status.AllIn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class StatusSpecification {
	public static Specification<Status> findByCriteria(final AllIn searchCriteria) {

		return (Specification<Status>) (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			if (searchCriteria.getId() != null) {
				predicates.add(cb.equal(root.get("id"), searchCriteria.getId()));
			}
			if (searchCriteria.getName() != null && !searchCriteria.getName().isEmpty()) {
				StringBuilder usrName = new StringBuilder();
				predicates.add(cb.like(
						cb.lower(root.get("name")),
						usrName
								.append("%")
								.append(searchCriteria.getName())
								.append("%")
								.toString()
								.toLowerCase()
				));
			}

			return cb.and(predicates.toArray(new Predicate[]{}));
		};
	}
}
