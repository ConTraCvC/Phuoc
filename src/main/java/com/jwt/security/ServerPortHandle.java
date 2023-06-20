package com.jwt.security;

import org.apache.catalina.connector.Connector;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class ServerPortHandle implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

  @Value("${server.additionalPorts}")
  private String additionalPorts;

  @Override
  public void customize(TomcatServletWebServerFactory factory) {
    Connector[] additionalsConnector = additionalsConnector();
    if (additionalsConnector != null && additionalsConnector.length>0) {
      factory.addAdditionalTomcatConnectors(additionalsConnector);
    }
  }

  private Connector[] additionalsConnector() {
    if (StringUtils.isBlank(additionalPorts)) {
      return null;
    }
    Set<Connector> results = new HashSet<>();
    String[] ports = this.additionalPorts.split(",");
    for (String port : ports) {
      Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
      connector.setScheme("http");
      connector.setPort(Integer.parseInt(port));
      results.add(connector);
    }
    return results.toArray(new Connector[]{});
  }
}
