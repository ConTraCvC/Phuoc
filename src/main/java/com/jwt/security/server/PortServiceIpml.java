package com.jwt.security.server;

import com.jwt.models.CustomerPort;
import com.jwt.repository.PortsRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.LifecycleException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PortServiceIpml implements PortService {

  private final PortsRepository portsRepository;

  private final PortServiceListener portServiceListener;

  @Override
  public ResponseEntity<?> portRegister(@RequestBody CustomerPort port, HttpServletRequest request) throws LifecycleException {
    Optional<CustomerPort> ports = portsRepository.findByPort(port.getPort());
    int adminPort = request.getLocalPort();
    if (ports.isPresent()) {
      System.out.println("Port is existed !");
      return ResponseEntity.badRequest().body("Port is existed !");
    } else if (adminPort != 8082) {
      System.out.println("You do not have permission to do this !");
      return ResponseEntity.badRequest().body("You do not have permission to do this !");
    } else {
      portsRepository.save(port);
      portServiceListener.addPort(port.getPort());
      System.out.println("Successfully create port: " + port.getPort());
      return ResponseEntity.ok("Successfully create port: " + port.getPort());
    }
  }

  @Override
  public ResponseEntity<?> deletePort(@RequestBody CustomerPort port, HttpServletRequest request) throws LifecycleException {
    int adminPort = request.getLocalPort();
    if(adminPort == 8082){
      portsRepository.deletePort(port.getPort());
      portServiceListener.removePort(port.getPort());
      System.out.println("Successfully destroy port: " + port.getPort());
      return ResponseEntity.ok("Successfully destroy port: " + port.getPort());
    } else {
      System.out.println("You do not have permission to do this !");
      return ResponseEntity.badRequest().body("You do not have permission to do this !");
    }
  }
}