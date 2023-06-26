package com.jwt.security.server;

import com.jwt.models.CustomerPort;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.catalina.LifecycleException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface PortService {

  ResponseEntity<?> portRegister(@RequestBody CustomerPort port, HttpServletRequest request) throws LifecycleException;

  ResponseEntity<?> deletePort(CustomerPort port, HttpServletRequest request) throws LifecycleException;
}
