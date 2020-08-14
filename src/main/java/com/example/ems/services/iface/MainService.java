/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:28
 */
package com.example.ems.services.iface;

public interface MainService<A, P, ID, ALL> {
	ID add(A data);

	A update(A data, ID id);

	A getById(ID id);

	ALL all(P params);
}
