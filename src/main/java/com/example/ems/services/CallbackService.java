/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.1
 * @since 2020-08-09T17:08
 */
package com.example.ems.services;

import com.example.ems.database.dao.redis.StateDAO;
import com.example.ems.dto.mq.CallbackMQ;
import com.example.ems.network.controllers.exceptions.websocket.NoAckException;
import com.example.ems.utils.enums.States;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CallbackService {

  private final SimpMessagingTemplate simpMessagingTemplate;
  private final StateDAO stateDAO;
  private final QueueService queueService;

  CallbackService(
      SimpMessagingTemplate simpMessagingTemplate, QueueService queueService, StateDAO stateDAO) {
    this.simpMessagingTemplate = simpMessagingTemplate;
    this.queueService = queueService;
    this.stateDAO = stateDAO;
  }

  // id - still the same as a exchange name
  /**
   * Listener of AMQP messages and sender to client web socket and if has an error then it will
   * retry send message to client side again until client side is approve that it received this
   * message
   *
   * @param message AMQP message {@link Message} with data
   * @param in Body {@link CallbackMQ} of AMQP message after serialization
   * @throws NoAckException If client do not get message
   * @throws NullPointerException If some values is null
   */
  @RabbitListener(id = "websocket")
  public void listen(Message message, CallbackMQ<Object> in) {
    MDC.put("resId", in.getResId());
    log.debug("Message: {}", message);
    log.debug("Data: {}", in);
    String resolveKey = String.format("state::callback::%s", States.RESOLVE);
    String inProgressKey = String.format("state::callback::%s", States.IN_PROGRESS);
    log.debug("Get data resId: {}", in.getResId());
    if (this.queueService.isGoRetry(message)) {
      if (this.stateDAO.exist(resolveKey, in.getResId())) {
        this.stateDAO.del(resolveKey, in.getResId());
        log.debug("Websocket send successful: {}", in.getResId());
        return;
      }
      if (this.stateDAO.add(inProgressKey, in.getResId(), in) == null) {
        this.simpMessagingTemplate.convertAndSend(
            String.format("/queue/%s", in.getQueueName()), in.getData());
      }
      throw new NoAckException(String.format("Waiting RESOLVE by res ID: %s", in.getResId()));
    } else {
      this.queueService.sendMessage(
          String.format("websocket.%s", in.getQueueName()),
          in,
          this.queueService.getRabbitMQSettings().getWebsocket());
      this.stateDAO.del(inProgressKey, in.getResId());
    }
  }

  /**
   * Resolve web socket message if client successful get on its side and change state of this web
   * socket message from in progress to resolve
   *
   * @param resId Response ID from web socket message
   * @throws NullPointerException If some values is null
   */
  public void removeState(String resId) {
    if (this.stateDAO.exist(String.format("state::callback::%s", States.IN_PROGRESS), resId)) {
      this.stateDAO.add(
          String.format("state::callback::%s", States.RESOLVE),
          resId,
          Instant.now().toEpochMilli());
      this.stateDAO.del(String.format("state::callback::%s", States.IN_PROGRESS), resId);
    }
  }
}
