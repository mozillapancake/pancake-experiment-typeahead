package com.mozillalabs.pancake.typeahead.main;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

public class Development
{
    public static void main(String[] args) throws Exception
    {
        // Configure Jetty

        Server server = new Server();

        Connector connector = new SelectChannelConnector();
        connector.setPort(8080);
        connector.setHost("0.0.0.0");
        server.addConnector(connector);

        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath("/");
        webAppContext.setDescriptor("src/main/webapp/WEB-INF/web.xml");
        webAppContext.setResourceBase("src/main/webapp");
        server.setHandler(webAppContext);

        server.setStopAtShutdown(true);

        server.start();
        server.join();
    }
}
