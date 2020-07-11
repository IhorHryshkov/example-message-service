/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T01:14
 */
package com.example.ems.network.controllers;

import com.example.ems.database.models.Status;
import com.example.ems.network.models.status.Add;
import com.example.ems.network.models.status.All;
import com.example.ems.services.StatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/v1")
public class StatusController {

	private final StatusService statusService;

	StatusController(StatusService statusService) {
		this.statusService = statusService;
	}

	@GetMapping("/status")
	@ResponseStatus(HttpStatus.OK)
	List<Status> all(@Valid All query) {
		return this.statusService.all(query);
	}

	@PostMapping("/status")
	ResponseEntity<String> add(@Valid @RequestBody Add addStatus) {
		return ResponseEntity.ok("Test post: " + addStatus);
	}

}
