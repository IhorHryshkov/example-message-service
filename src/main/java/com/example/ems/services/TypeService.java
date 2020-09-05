/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T00:36
 */
package com.example.ems.services;

import static com.example.ems.database.dao.pg.specification.TypeSpecification.findByCriteria;

import com.example.ems.database.dao.pg.TypesDAO;
import com.example.ems.dto.database.pg.Types;
import com.example.ems.dto.network.controller.type.AllIn;
import com.example.ems.dto.network.controller.type.AllOut;
import com.example.ems.services.iface.MainService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/** Processing service of type {@link Types} data */
@Slf4j
@Service
public class TypeService
    implements MainService<Types, Integer, AllIn, AllOut<Types>, Types, Integer, Types, Types> {

  private final TypesDAO typesDAO;

  public TypeService(TypesDAO typesDAO) {
    this.typesDAO = typesDAO;
  }

  /**
   * Method is not implemented
   *
   * @param data New type data {@link Types}
   * @return id of type data
   */
  @Override
  public Integer add(Types data) {
    throw new RuntimeException("Method is not implemented");
  }

  /**
   * Method is not implemented
   *
   * @param data Update type data {@link Types}
   * @param id ID of old type data
   * @return object with update type data {@link Types}
   */
  @Override
  public Types update(Types data, Integer id) {
    throw new RuntimeException("Method is not implemented");
  }

  /**
   * Method is not implemented
   *
   * @param id ID of type data
   * @return object with type data {@link Types}
   */
  @Override
  public Types getById(Integer id) {
    throw new RuntimeException("Method is not implemented");
  }

  /**
   * Load all types {@link Types} by query params and add result to cache
   *
   * @param params Object of query params for search {@link AllIn}
   * @return result object with list of types and etag value {@link AllOut}
   */
  @Override
  @Cacheable(
      value = "typeCache::all::ifNoneMatch",
      key = "#params.toHashKey()",
      unless = "#result == null || #result.getData() == null || #result.getData().size() == 0")
  public AllOut<Types> all(AllIn params) {
    List<Types> types = this.typesDAO.findAll(findByCriteria(params));
    String etag =
        DigestUtils.sha256Hex(
            String.format(
                "%s:%s:%d",
                UUID.randomUUID().toString(), params.getPath(), Instant.now().toEpochMilli()));

    return new AllOut<>(etag, types);
  }
}
