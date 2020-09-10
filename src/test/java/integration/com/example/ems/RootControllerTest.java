/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-09-01T13:34
 */
package integration.com.example.ems;

import com.example.ems.EmsApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@SpringBootTest(classes = EmsApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class RootControllerTest {
  final ObjectMapper mapper = new ObjectMapper();
  final HttpHeaders headers = new HttpHeaders();
  final CompletableFuture<Object> completableFuture = new CompletableFuture<>();
  @Autowired TestRestTemplate restTemplate;
  @LocalServerPort private int port;

  String createURLWithPort(String uri) {
    return String.format("http://localhost:%d%s", port, uri);
  }

  String createURLWithPort(String uri, Object pathParam) {
    return String.format("http://localhost:%d%s/%s", port, uri, pathParam);
  }

  List<Transport> createTransportClient() {
    return Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()));
  }

  class CustomStompFrameHandler implements StompFrameHandler {
    @Override
    public Type getPayloadType(StompHeaders stompHeaders) {
      return Object.class;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object o) {
      completableFuture.complete(o);
    }
  }
}
