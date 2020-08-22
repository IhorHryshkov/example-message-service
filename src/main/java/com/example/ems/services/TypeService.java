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

@Slf4j
@Service
public class TypeService
    implements MainService<Types, Integer, AllIn, AllOut<Types>, Types, Integer, Types, Types> {

  private final TypesDAO typesDAO;

  public TypeService(TypesDAO typesDAO) {
    this.typesDAO = typesDAO;
  }

  @Override
  public Integer add(Types data) {
    throw new RuntimeException("Method is not implemented");
  }

  @Override
  public Types update(Types data, Integer integer) {
    throw new RuntimeException("Method is not implemented");
  }

  @Override
  public Types getById(Integer integer) {
    throw new RuntimeException("Method is not implemented");
  }

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
