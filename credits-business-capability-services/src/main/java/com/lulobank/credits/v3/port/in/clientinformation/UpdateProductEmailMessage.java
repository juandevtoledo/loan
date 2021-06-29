package com.lulobank.credits.v3.port.in.clientinformation;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UpdateProductEmailMessage {

  private String idClient;
  private String newEmail;

}
