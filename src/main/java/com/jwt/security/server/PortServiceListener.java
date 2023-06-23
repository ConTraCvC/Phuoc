package com.jwt.security.server;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.springframework.boot.web.context.ConfigurableWebServerApplicationContext;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PortServiceListener {

  private final ConfigurableWebServerApplicationContext context;

  public void addPort(int port) throws LifecycleException {
    TomcatWebServer webServer = (TomcatWebServer) context.getWebServer();
    Tomcat tomcat = webServer.getTomcat();
    Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
    connector.setPort(port);
    tomcat.getService().addConnector(connector);
    connector.start();
  }

  public void removePort(int port) throws LifecycleException {
    TomcatWebServer webServer = (TomcatWebServer) context.getWebServer();
    Tomcat tomcat = webServer.getTomcat();
    Connector[] connectors = tomcat.getService().findConnectors();
    for(Connector connector : connectors) {
      if (connector.getPort() == port) {
        connector.stop();
        connector.destroy();
        tomcat.getService().removeConnector(connector);
        break;
      }
    }
  }
}