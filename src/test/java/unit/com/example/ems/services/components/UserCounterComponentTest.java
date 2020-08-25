package unit.com.example.ems.services.components;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.example.ems.database.dao.pg.CountersDAO;
import com.example.ems.database.dao.pg.UsersDAO;
import com.example.ems.database.dao.redis.StateDAO;
import com.example.ems.dto.database.pg.Status;
import com.example.ems.dto.database.pg.Types;
import com.example.ems.dto.database.pg.Users;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserCounterComponentTest {
  private CountersDAO countersDAO;
  private UsersDAO usersDAO;
  private StateDAO stateDAO;

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

    when(stateDAO.exist(eq(keyExpect), eq(hash))).thenReturn(true, false);
    when(stateDAO.add(eq(keyExpect), eq(hash), eq(""))).thenReturn("object", null);
  }

  @Test
  void getUserOrNotFound() {}
}
