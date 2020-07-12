package com.example.ems.database.dao.specification;

import com.example.ems.database.models.Users;
import com.example.ems.network.models.user.AllIn;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class UsersSpecification {
	public static Specification<Users> findByCriteria(final AllIn searchCriteria) {

		return new Specification<Users>() {

			@Override
			public Predicate toPredicate(
					Root<Users> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {

				List<Predicate> predicates = new ArrayList<Predicate>();

				if (searchCriteria.getUserId() != null) {
					predicates.add(cb.equal(root.get("id"), searchCriteria.getUserId()));
				}
				if (searchCriteria.getUsername() != null && !searchCriteria.getUsername().isEmpty()) {
					StringBuilder usrName = new StringBuilder();
					predicates.add(cb.like(cb.lower(root.get("username")), usrName.append("%").append(searchCriteria.getUsername()).append("%").toString().toLowerCase()));
				}

				return cb.and(predicates.toArray(new Predicate[]{}));
			}
		};
	}
}
