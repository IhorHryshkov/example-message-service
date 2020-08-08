/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-11T03:23
 */
package com.example.ems.network.controllers.exceptions.user;

public class ResponseUsernameUsedException extends RuntimeException {

    public ResponseUsernameUsedException() {
        super("Username is used, please try to use other username");
    }

}
