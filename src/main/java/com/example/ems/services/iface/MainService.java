/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:28
 */
package com.example.ems.services.iface;

/**
 * Root interface for a lot of processing services
 *
 * @param <A> result data for method get by ID
 * @param <ID> ID object for update or get by ID
 * @param <ALL> params for get all results
 * @param <ALLR> result data for method all
 * @param <ADD> object for add new data
 * @param <ADDR> result data for method add
 * @param <UPD> object for update old data
 * @param <UPDR> result data for method update
 */
public interface MainService<A, ID, ALL, ALLR, ADD, ADDR, UPD, UPDR> {
  ADDR add(ADD data);

  UPDR update(UPD data, ID id);

  A getById(ID id);

  ALLR all(ALL params);
}
