/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-08-17T05:29
 */
package com.example.ems.services.components;

import com.example.ems.database.dao.pg.CountersDAO;
import com.example.ems.database.dao.pg.UsersDAO;
import com.example.ems.database.dao.redis.StateDAO;
import com.example.ems.dto.database.pg.Counters;
import com.example.ems.dto.database.pg.Types;
import com.example.ems.dto.database.pg.Users;
import com.example.ems.dto.database.pg.ids.CountersIds;
import com.example.ems.network.controllers.exceptions.status.UserIDNotFoundException;
import com.example.ems.utils.enums.States;
import java.math.BigInteger;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserCounterComponent {
  private final CountersDAO countersDAO;
  private final UsersDAO usersDAO;
  private final StateDAO stateDAO;

  public UserCounterComponent(CountersDAO countersDAO, UsersDAO usersDAO, StateDAO stateDAO) {
    this.countersDAO = countersDAO;
    this.usersDAO = usersDAO;
    this.stateDAO = stateDAO;
  }

  public void incCounter(Types type, Users user, Long count, String key, String hash) {
    if (!this.stateDAO.exist(String.format(key, States.INIT), hash)
        && this.stateDAO.add(String.format(key, States.INIT), hash, "") == null) {
      Counters counter;
      count = count == null || count <= 0 ? 1L : count;
      if (type == null) {
        log.warn("Type name {} not found", user.getStatus().getName());
      } else {
        CountersIds countersIds = new CountersIds(user.getId(), type.getId());
        counter = this.countersDAO.findById(countersIds).orElse(null);
        if (counter == null) {
          counter = new Counters(countersIds, BigInteger.valueOf(count));
        } else {
          counter.setCounts(counter.getCounts().add(BigInteger.valueOf(count)));
        }
        this.countersDAO.save(counter);
      }
    }
  }

  @Cacheable(value = "userCache", key = "#root.getMethodName() + \"::ifNoneMatch::\" + #hashUserId")
  public Users getUserOrNotFound(
      UUID userId, String delStateKey, String hashKeyToDel, String hashUserId) {
    Users user = this.usersDAO.findById(userId).orElse(null);
    if (user == null) {
      log.error("User ID {} not found", userId);
      this.stateDAO.del(String.format(delStateKey, States.IN_PROGRESS), hashKeyToDel);
      throw new UserIDNotFoundException();
    }
    return user;
  }
}
