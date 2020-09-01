/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-01T13:34
 */
package integration.com.example.ems;

import com.example.ems.EmsApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;

@SpringBootTest(classes = EmsApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class RootControllerTest {
  @LocalServerPort private int port;
  @Autowired TestRestTemplate restTemplate;
  final ObjectMapper mapper = new ObjectMapper();
  final HttpHeaders headers = new HttpHeaders();

  String createURLWithPort(String uri) {
    return String.format("http://localhost:%d%s", port, uri);
  }

  String createURLWithPort(String uri, Object pathParam) {
    return String.format("http://localhost:%d%s/%s", port, uri, pathParam);
  }
}
