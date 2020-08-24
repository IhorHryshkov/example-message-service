package unit.com.example.ems.network.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.example.ems.dto.database.pg.Types;
import com.example.ems.dto.network.controller.Res;
import com.example.ems.dto.network.controller.type.AllIn;
import com.example.ems.dto.network.controller.type.AllOut;
import com.example.ems.network.controllers.TypeController;
import com.example.ems.network.controllers.exceptions.global.ResponseEmptyException;
import com.example.ems.network.controllers.exceptions.global.ResponseIfNoneMatchException;
import com.example.ems.services.CacheService;
import com.example.ems.services.TypeService;
import com.example.ems.utils.network.Response;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class TypeControllerTest {
  @Mock private TypeService typeService;
  @Mock private Response<Object> response;
  @Mock private CacheService cacheService;

  @InjectMocks private TypeController typeController;

  private Long timestamp;
  private String uuid;

  @BeforeEach
  void setUp() {
    timestamp = Instant.now().toEpochMilli();
    uuid = UUID.randomUUID().toString();
    MDC.put("resId", uuid);
  }

  @Test
  void all() {
    MDC.put("fullPathQuery", "/v1/test");
    MDC.put("ifNoneMatch", "testHash");

    AllIn in = new AllIn();
    in.setId(1);
    in.setName("online");

    String noneMatchKey = "typeCache::all::forMatch::testHash";
    String etagKey = "typeCache::all::forMatch::testEtag";
    doThrow(new ResponseIfNoneMatchException())
        .doNothing()
        .when(cacheService)
        .existOrIfNoneMatch(eq(noneMatchKey));
    List<Types> typesList = Collections.singletonList(new Types());
    AllOut<Types> out = new AllOut<>();
    out.setData(typesList);
    out.setEtag("testEtag");
    when(typeService.all(eq(in))).thenReturn(new AllOut<>()).thenReturn(out);
    doNothing().when(cacheService).setKeyForCheckWithTtlDivider(eq(etagKey), eq(2));
    when(response.formattedSuccess(
            eq(typesList),
            eq(MediaType.APPLICATION_JSON),
            eq(HttpStatus.OK.value()),
            eq("testEtag")))
        .thenReturn(
            ResponseEntity.status(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON)
                .eTag("testEtag")
                .body(new Res<>(uuid, typesList, null, timestamp)));
    assertThat(catchThrowable(() -> typeController.all(in)))
        .as("Check if none match")
        .isInstanceOf(ResponseIfNoneMatchException.class);
    assertThat(catchThrowable(() -> typeController.all(in)))
        .as("Check if response empty")
        .isInstanceOf(ResponseEmptyException.class);
    ResponseEntity<Res<Object>> all = typeController.all(in);
    assertThat(all.getStatusCode()).as("Status code").isEqualTo(HttpStatus.OK);
    assertThat(all.getBody()).as("Body not null").isNotNull();
    Res<Object> res = all.getBody();
    assertThat(res.getError()).as("Error is null").isNull();
    assertThat(res.getTimestamp()).as("Timestamp").isEqualTo(timestamp);
    assertThat(res.getResId()).as("Res ID").isEqualTo(uuid);
    assertThat(res.getData()).as("Data is not null").isNotNull();
    assertThat(res.getData()).as("Data is list").isEqualTo(typesList);
  }
}
