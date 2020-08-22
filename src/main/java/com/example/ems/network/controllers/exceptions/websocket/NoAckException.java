/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-08-10T07:31
 */
package com.example.ems.network.controllers.exceptions.websocket;

public class NoAckException extends RuntimeException {
  public NoAckException(String message) {
    super(message, null, false, false);
  }
}
