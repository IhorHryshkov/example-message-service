/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:36
 */
package com.example.ems.services;

import static com.example.ems.database.dao.pg.specification.StatusSpecification.findByCriteria;

import com.example.ems.database.dao.pg.StatusDAO;
import com.example.ems.dto.database.pg.Status;
import com.example.ems.dto.network.controller.status.AllIn;
import com.example.ems.dto.network.controller.status.AllOut;
import com.example.ems.services.iface.MainService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/** Processing service of status {@link Status} data */
@Slf4j
@Service
public class StatusService
    implements MainService<
        Status, Integer, AllIn, AllOut<Status>, Status, Integer, Status, Status> {

  private final StatusDAO statusDAO;

  public StatusService(StatusDAO statusDAO) {
    this.statusDAO = statusDAO;
  }

  /**
   * Method is not implemented
   *
   * @param data New status data {@link Status}
   * @return id of status data
   */
  @Override
  public Integer add(Status data) {
    throw new RuntimeException("Method is not implemented");
  }

  /**
   * Method is not implemented
   *
   * @param data Update status data {@link Status}
   * @param id ID of old status data
   * @return object with update status data {@link Status}
   */
  @Override
  public Status update(Status data, Integer id) {
    throw new RuntimeException("Method is not implemented");
  }

  /**
   * Method is not implemented
   *
   * @param id ID of status data
   * @return object with status data {@link Status}
   */
  @Override
  public Status getById(Integer id) {
    throw new RuntimeException("Method is not implemented");
  }

  /**
   * Load all statuses {@link Status} by query params and add result to cache
   *
   * @param params Object of query params for search {@link AllIn}
   * @return result object with list of statuses and etag value {@link AllOut}
   */
  @Override
  @Cacheable(
      value = "statusCache::all::ifNoneMatch",
      key = "#params.toHashKey()",
      unless = "#result == null || #result.getData() == null || #result.getData().size() == 0")
  public AllOut<Status> all(AllIn params) {
    List<Status> statuses = this.statusDAO.findAll(findByCriteria(params));

    String etag =
        DigestUtils.sha256Hex(
            String.format(
                "%s:%s:%d",
                UUID.randomUUID().toString(), params.getPath(), Instant.now().toEpochMilli()));

    return new AllOut<>(etag, statuses);
  }
}
