/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-08-06T21:42
 */
package com.example.ems.dto.mq;

import lombok.*;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class QueueConf {
	private String exchange;
	private String routingKey;
	private Boolean durable;
	private Boolean exclusive;
	private Boolean autoDelete;
}
