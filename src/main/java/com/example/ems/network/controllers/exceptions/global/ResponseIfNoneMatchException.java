/**
 * @project ems
 * @author Ihor Hryshkov
 * @version 1.0.0
 * @since 2020-07-11T03:23
 */
package com.example.ems.network.controllers.exceptions.global;

public class ResponseIfNoneMatchException extends RuntimeException {

  public ResponseIfNoneMatchException() {
    super("Data is not change in resource");
  }
}
