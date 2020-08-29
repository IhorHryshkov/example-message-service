/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-08-28T20:45
 */
package unit.com.example.ems.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import com.example.ems.database.dao.pg.TypesDAO;
import com.example.ems.dto.database.pg.Types;
import com.example.ems.dto.network.controller.type.AllIn;
import com.example.ems.dto.network.controller.type.AllOut;
import com.example.ems.services.TypeService;
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
class TypeServiceTest {
  @Mock private TypesDAO typesDAO;

  @InjectMocks private TypeService typeService;

  @Test
  void add() {
    assertThat(catchThrowable(() -> typeService.add(new Types())))
        .as("add no init method exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Method is not implemented");
  }

  @Test
  void update() {
    assertThat(catchThrowable(() -> typeService.update(new Types(), 1)))
        .as("update no init method exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Method is not implemented");
  }

  @Test
  void getById() {
    assertThat(catchThrowable(() -> typeService.getById(1)))
        .as("getById init method exception")
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Method is not implemented");
  }

  @Test
  void all() {
    AllIn allIn = new AllIn("testType", 1, "testResId", "testPath");
    AllIn allInExpected = new AllIn("testType", 1, "testResId", "testPath");
    List<Types> typesEmpty = Collections.emptyList();
    List<Types> typesEmptyExpected = Collections.emptyList();
    List<Types> types = Collections.singletonList(new Types());
    List<Types> typesExpected = Collections.singletonList(new Types());
    when(typesDAO.findAll(Mockito.<Specification<Types>>any()))
        .thenReturn(typesEmpty)
        .thenReturn(types);
    AllOut<Types> allOut = typeService.all(allIn);
    assertThat(allOut.getEtag()).as("Etag not null").isNotNull().isNotEmpty();
    assertThat(allOut.getData()).as("Type list is empty").isNotNull().isEqualTo(typesEmptyExpected);
    allOut = typeService.all(allIn);
    assertThat(allOut.getEtag()).as("Etag not null").isNotNull().isNotEmpty();
    assertThat(allOut.getData()).as("Type list is not empty").isNotNull().isEqualTo(typesExpected);
  }
}
