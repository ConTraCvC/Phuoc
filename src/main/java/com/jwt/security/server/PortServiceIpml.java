package com.jwt.security.server;

import com.jwt.models.CustomerPort;
import com.jwt.repository.PortsRepository;
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
  public ResponseEntity<?> portRegister(@RequestBody CustomerPort port) throws LifecycleException {
    Optional<CustomerPort> ports = portsRepository.findByPort(port.getPort());
    if (ports.isPresent()) {
      System.out.println("Port is existed !");
      return ResponseEntity.badRequest().body("Port is existed !");
    } else {
      portsRepository.save(port);
      portServiceListener.addPort(port.getPort());
      System.out.println("Successfully create port: " + port.getPort());
      return ResponseEntity.ok("Successfully create port: " + port.getPort());
    }
  }

  @Override
  public ResponseEntity<?> deletePort(@RequestBody CustomerPort port) throws LifecycleException {
    portsRepository.deletePort(port.getPort());
    portServiceListener.removePort(port.getPort());
    System.out.println("Successfully destroy port: " + port.getPort());
    return ResponseEntity.ok("Successfully destroy port: " + port.getPort());
  }
}

