package com.jwt.security;

import com.jwt.models.CustomerPort;
import com.jwt.repository.PortsRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class ServerPortHandle implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

//  @Value("${server.additionalPorts}")
//  private String additionalPorts;

  @Value("${server.portRangeStart}")
  private int portRangeStart;

  @Value("${server.portRangeEnd}")
  private int portRangeEnd;

  private final PortsRepository portsRepository;

  @Override
  public void customize(TomcatServletWebServerFactory factory) {
    int ports = portsRepository.findAllBy();
    Connector[] additionalsConnector = additionalsConnector(ports);
    if (additionalsConnector != null && additionalsConnector.length>0) {
      factory.addAdditionalTomcatConnectors(additionalsConnector);
    }
  }

  public Connector[] additionalsConnector(int ports) {
    if (portRangeStart < 8082 || portRangeEnd > 20000 || portRangeStart > portRangeEnd) {
      return null;
    }
    Set<Connector> port = new HashSet<>();
    Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
    connector.setScheme("http");
    connector.setPort(ports);
    port.add(connector);

    return port.toArray(new Connector[]{});
  }
}
