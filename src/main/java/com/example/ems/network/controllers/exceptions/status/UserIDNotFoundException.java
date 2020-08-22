/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-11T03:23
 */
package com.example.ems.network.controllers.exceptions.status;

public class UserIDNotFoundException extends RuntimeException {

  public UserIDNotFoundException() {
    super("User ID not found");
  }
}
