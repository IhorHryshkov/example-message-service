/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-08-09T17:08
 */
package com.example.ems.services;

import com.example.ems.database.dao.redis.StateDAO;
import com.example.ems.dto.network.controller.user.AddOut;
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
			AddOut addOut
	) {
		MDC.put("resId", "");
		log.debug("Message: {}", message);
		String typeId = message.getMessageProperties().getHeader("__TypeId__");
		log.debug("TypeId: {}", typeId);
		switch (typeId) {
			case "com.example.ems.dto.network.controller.user.AddOut": {
				MDC.put("resId", addOut.getResId());
				log.debug("Get data AddIn: {}", addOut);
				if (!this.queueService.endedRetryCount(message)) {
					if (this.stateDAO.exist(String.format("userState::callback::%s", States.RESOLVE), addOut.getResId())) {
						this.stateDAO.del(String.format("userState::callback::%s", States.RESOLVE), addOut.getResId());
						return;
					}
					if (this.stateDAO.add(String.format("userState::callback::%s", States.IN_PROGRESS), addOut.getResId(), addOut) == null) {
						this.simpMessagingTemplate.convertAndSend(String.format("/queue/%s", addOut.getUsername()), addOut);
					}
					throw new NoAckException(String.format("Waiting RESOLVE by res ID: %s", addOut.getResId()));
				} else {
					this.queueService.sendMessage(String.format("websocket.%s", addOut.getUsername()), addOut, this.queueService.getRabbitMQSettings().getWebsocket());
					this.stateDAO.del(String.format("userState::callback::%s", States.IN_PROGRESS), addOut.getResId());
				}
			}
			default:
				log.warn("__TypeId__ not found for message: {}", message);
		}
	}

	public void removeState(String resId) {
		if (this.stateDAO.exist(String.format("userState::callback::%s", States.IN_PROGRESS), resId)) {
			this.stateDAO.add(String.format("userState::callback::%s", States.RESOLVE), resId, Instant.now().toEpochMilli());
			this.stateDAO.del(String.format("userState::callback::%s", States.IN_PROGRESS), resId);
		}
	}
}
