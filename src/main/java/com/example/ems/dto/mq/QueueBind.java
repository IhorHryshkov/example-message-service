/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-08-10T06:26
 */
package com.example.ems.dto.mq;

import lombok.*;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class QueueBind {
	private Queue queue;
	private Binding binding;
}
