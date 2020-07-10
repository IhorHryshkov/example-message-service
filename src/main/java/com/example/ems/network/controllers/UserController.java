/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-08T01:14
 */
package com.example.ems.network.controllers;

import com.example.ems.database.models.Users;
import com.example.ems.network.models.Res;
import com.example.ems.network.models.user.Add;
import com.example.ems.network.models.user.All;
import com.example.ems.services.UserService;
import com.example.ems.utils.network.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/v1")
public class UserController {

    private final UserService userService;
    private final Response<Users> response;

    UserController(UserService userService, Response<Users> response) {
        this.userService = userService;
        this.response = response;
    }

    @ExceptionHandler({Exception.class})
    public void handleException() {
        //
    }

    @GetMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    List<Users> all(@Valid All query) {
        return this.userService.all(query);
    }

    @PostMapping("/user")
    ResponseEntity<Res<Users>> add(@Valid @RequestBody Add addUser) {
        try {

        } catch (Exception ex) {

        }
        return response.formattedSuccess();
    }

}
