/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-08-09T17:08
 */
package com.example.ems.services;

import com.example.ems.database.dao.redis.StateDAO;
import com.example.ems.dto.mq.CallbackMQ;
import com.example.ems.network.controllers.exceptions.websocket.NoAckException;
import com.example.ems.utils.enums.States;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class CallbackService {

	private final SimpMessagingTemplate simpMessagingTemplate;
	private final StateDAO stateDAO;
	private final QueueService queueService;

	CallbackService(SimpMessagingTemplate simpMessagingTemplate, QueueService queueService, StateDAO stateDAO) {
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.queueService = queueService;
		this.stateDAO = stateDAO;
	}

	//id - still the same as a exchange name
	@RabbitListener(id = "websocket")
	public void listen(
			Message message,
			CallbackMQ<Object> in
	) {
		MDC.put("resId", in.getResId());
		log.debug("Message: {}", message);
		log.debug("Data: {}", in);
		this.sendDataToClient(message, in);
	}

	private void sendDataToClient(
			Message message,
			CallbackMQ<Object> data
	) {
		String resolveKey = String.format("state::callback::%s", States.RESOLVE);
		String inProgressKey = String.format("state::callback::%s", States.IN_PROGRESS);
		MDC.put("resId", data.getResId());
		log.debug("Get data resId: {}", data.getResId());
		if (this.queueService.isGoRetry(message)) {
			if (this.stateDAO.exist(resolveKey, data.getResId())) {
				this.stateDAO.del(resolveKey, data.getResId());
				return;
			}
			if (this.stateDAO.add(inProgressKey, data.getResId(), data) == null) {
				this.simpMessagingTemplate.convertAndSend(String.format("/queue/%s", data.getQueueName()), data.getData());
			}
			throw new NoAckException(String.format("Waiting RESOLVE by res ID: %s", data.getResId()));
		} else {
			this.queueService.sendMessage(String.format("websocket.%s", data.getQueueName()), data, this.queueService.getRabbitMQSettings().getWebsocket());
			this.stateDAO.del(inProgressKey, data.getResId());
		}

	}

	public void removeState(String resId) {
		if (this.stateDAO.exist(String.format("state::callback::%s", States.IN_PROGRESS), resId)) {
			this.stateDAO.add(String.format("state::callback::%s", States.RESOLVE), resId, Instant.now().toEpochMilli());
			this.stateDAO.del(String.format("state::callback::%s", States.IN_PROGRESS), resId);
		}
	}
}
