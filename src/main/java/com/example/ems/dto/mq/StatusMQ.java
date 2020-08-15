/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-08-15T01:41
 */
package com.example.ems.dto.mq;

import com.example.ems.dto.database.pg.Users;
import com.example.ems.dto.network.controller.user.UpdateIn;
import lombok.*;

import java.io.Serializable;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StatusMQ implements Serializable {
	private Users user;
	private UpdateIn update;

}
