package com.jwt.security.server;

import com.jwt.models.CustomerPort;
import org.apache.catalina.LifecycleException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface PortService {

  ResponseEntity<?> portRegister(@RequestBody CustomerPort port) throws LifecycleException;

  ResponseEntity<?> deletePort(CustomerPort port) throws LifecycleException;
}
