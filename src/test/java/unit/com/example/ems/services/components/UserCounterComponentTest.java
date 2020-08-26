package unit.com.example.ems.services.components;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.example.ems.database.dao.pg.CountersDAO;
import com.example.ems.database.dao.pg.UsersDAO;
import com.example.ems.database.dao.redis.StateDAO;
import com.example.ems.dto.database.pg.Counters;
import com.example.ems.dto.database.pg.Status;
import com.example.ems.dto.database.pg.Types;
import com.example.ems.dto.database.pg.Users;
import com.example.ems.dto.database.pg.ids.CountersIds;
import com.example.ems.network.controllers.exceptions.status.UserIDNotFoundException;
import com.example.ems.services.components.UserCounterComponent;
import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserCounterComponentTest {
  @Mock private CountersDAO countersDAO;
  @Mock private UsersDAO usersDAO;
  @Mock private StateDAO stateDAO;

  @InjectMocks private UserCounterComponent userCounterComponent;

  @Test
  void incCounter() {
    String hash = "testHash";
    String key = "testKey::%s";
    String keyExpect = "testKey::INIT";
    Types type = new Types();
    type.setId(1);
    Users user = new Users();
    user.setId(UUID.fromString("99d4160b-d8e4-4425-a856-b4f2285f9ad5"));
    user.setStatus(new Status("Online"));
    CountersIds countersIds = new CountersIds(user.getId(), type.getId());
    Counters counterFind = new Counters(countersIds, BigInteger.valueOf(10));
    Counters counterNewExpect = new Counters(countersIds, BigInteger.valueOf(1));
    Counters counterFindAndAddExpect = new Counters(countersIds, BigInteger.valueOf(15));

    when(stateDAO.exist(eq(keyExpect), eq(hash))).thenReturn(true, false);
    when(stateDAO.add(eq(keyExpect), eq(hash), eq(""))).thenReturn("object", (Object) null);
    when(countersDAO.findById(eq(countersIds)))
        .thenReturn(Optional.empty())
        .thenReturn(Optional.of(counterFind));
    when(countersDAO.save(eq(counterNewExpect))).thenReturn(counterNewExpect);
    when(countersDAO.save(eq(counterFindAndAddExpect))).thenReturn(counterNewExpect);

    // State by key exist in cache
    userCounterComponent.incCounter(null, user, null, key, hash);
    // State by key not exist in cache but add new cache data found this data
    userCounterComponent.incCounter(null, user, null, key, hash);
    // State by key not exist in cache but add new cache data not found this data and type is null
    userCounterComponent.incCounter(null, user, null, key, hash);
    // State by key not exist in cache but add new cache data not found this data and counter in DB
    // not found and "count" field is null
    userCounterComponent.incCounter(type, user, null, key, hash);
    // State by key not exist in cache but add new cache data not found this data and counter in DB
    // found
    userCounterComponent.incCounter(type, user, 5L, key, hash);
  }

  @Test
  void getUserOrNotFound() {
    String hash = "testHash";
    String hashUserId = "userIdHash";
    String key = "testKey::%s";
    String keyExpect = "testKey::IN_PROGRESS";
    UUID userId = UUID.fromString("99d4160b-d8e4-4425-a856-b4f2285f9ad5");
    Users userFind = new Users();
    userFind.setId(userId);
    userFind.setStatus(new Status("Online"));
    when(usersDAO.findById(eq(userId)))
        .thenReturn(Optional.empty())
        .thenReturn(Optional.empty())
        .thenReturn(Optional.of(userFind));
    when(stateDAO.del(eq(keyExpect), eq(hash)))
        .thenThrow(new RuntimeException("Test"))
        .thenReturn(true);
    assertThat(
            catchThrowable(
                () -> userCounterComponent.getUserOrNotFound(userId, key, hash, hashUserId)))
        .as("Delete have internal exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Test");
    assertThat(
            catchThrowable(
                () -> userCounterComponent.getUserOrNotFound(userId, key, hash, hashUserId)))
        .as("User ID not found exception")
        .isInstanceOf(UserIDNotFoundException.class);
    assertThat(userCounterComponent.getUserOrNotFound(userId, key, hash, hashUserId))
        .as("User ID found")
        .isEqualTo(userFind);
  }
}
