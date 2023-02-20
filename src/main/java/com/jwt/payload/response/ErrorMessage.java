package com.jwt.payload.response;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class ErrorMessage {

  private String timestamp;

  private String status;

  private String path;

  private List<String> errors;

  private String edesc;

  public ErrorMessage(String s) {
  }

  public ErrorMessage(int value, Date date, String message, String description) {
  }
}
