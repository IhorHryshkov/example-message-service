/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-08-28T20:21
 */
package unit.com.example.ems.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import com.example.ems.database.dao.pg.StatusDAO;
import com.example.ems.dto.database.pg.Status;
import com.example.ems.dto.network.controller.status.AllIn;
import com.example.ems.dto.network.controller.status.AllOut;
import com.example.ems.services.StatusService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class StatusServiceTest {
  @Mock private StatusDAO statusDAO;

  @InjectMocks private StatusService statusService;

  @Test
  void add() {
    assertThat(catchThrowable(() -> statusService.add(new Status())))
        .as("add no init method exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Method is not implemented");
  }

  @Test
  void update() {
    assertThat(catchThrowable(() -> statusService.update(new Status(), 1)))
        .as("update no init method exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Method is not implemented");
  }

  @Test
  void getById() {
    assertThat(catchThrowable(() -> statusService.getById(1)))
        .as("getById init method exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Method is not implemented");
  }

  @Test
  void all() {
    AllIn allIn = new AllIn("testStatus", 1, "testResId", "testPath");
    AllIn allInExpected = new AllIn("testStatus", 1, "testResId", "testPath");
    List<Status> statusesEmpty = Collections.emptyList();
    List<Status> statusesEmptyExpected = Collections.emptyList();
    List<Status> statuses = Collections.singletonList(new Status());
    List<Status> statusesExpected = Collections.singletonList(new Status());
    when(statusDAO.findAll(Mockito.<Specification<Status>>any()))
        .thenReturn(statusesEmpty)
        .thenReturn(statuses);
    AllOut<Status> allOut = statusService.all(allIn);
    assertThat(allOut.getEtag()).as("Etag not null").isNotNull().isNotEmpty();
    assertThat(allOut.getData())
        .as("Status list is empty")
        .isNotNull()
        .isEqualTo(statusesEmptyExpected);
    allOut = statusService.all(allIn);
    assertThat(allOut.getEtag()).as("Etag not null").isNotNull().isNotEmpty();
    assertThat(allOut.getData())
        .as("Status list is not empty")
        .isNotNull()
        .isEqualTo(statusesExpected);
  }
}
