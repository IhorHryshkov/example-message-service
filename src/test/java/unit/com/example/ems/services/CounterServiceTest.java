package unit.com.example.ems.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.example.ems.database.dao.pg.CountersDAO;
import com.example.ems.database.dao.pg.TypesDAO;
import com.example.ems.database.dao.redis.StateDAO;
import com.example.ems.dto.database.pg.Counters;
import com.example.ems.dto.network.controller.counter.GetByIdIn;
import com.example.ems.dto.network.controller.counter.GetByIdOut;
import com.example.ems.services.CounterService;
import com.example.ems.services.QueueService;
import com.example.ems.services.components.UserCounterComponent;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CounterServiceTest {
  @Mock private CountersDAO countersDAO;
  @Mock private StateDAO stateDAO;
  @Mock private TypesDAO typeDAO;
  @Mock private QueueService queueService;
  @Mock private UserCounterComponent userCounterComponent;

  @InjectMocks private CounterService counterService;

  @Test
  void getByUserId() {
    UUID userIdExpected = UUID.fromString("aaaaaaaa-86f0-4b01-a18e-def71dbda1ba");
    GetByIdIn in =
        new GetByIdIn(
            userIdExpected.toString(), "bbbbbbbb-86f0-4b01-a18e-def71dbda1ba", "/v1/test");
    List<Counters> countersExpected = Collections.singletonList(new Counters());
    when(countersDAO.findByKeysUserId(eq(userIdExpected)))
        .thenReturn(Collections.emptyList())
        .thenReturn(Collections.singletonList(new Counters()));
    // If return empty
    GetByIdOut<List<Counters>> out = counterService.getByUserId(in);
    assertThat(out.getEtag()).as("ETag").isNotNull();
    assertThat(out.getData()).as("List of counters").isEqualTo(Collections.emptyList());
    // If return not empty
    out = counterService.getByUserId(in);
    assertThat(out.getEtag()).as("ETag").isNotNull();
    assertThat(out.getData()).as("List of counters").isEqualTo(countersExpected);
  }

  @Test
  void add() {}

  @Test
  void listenCounterAdd() {}
}
