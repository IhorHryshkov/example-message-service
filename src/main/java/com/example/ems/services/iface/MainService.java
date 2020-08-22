/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:28
 */
package com.example.ems.services.iface;

public interface MainService<A, ID, ALL, ALLR, ADD, ADDR, UPD, UPDR> {
  ADDR add(ADD data);

  UPDR update(UPD data, ID id);

  A getById(ID id);

  ALLR all(ALL params);
}
