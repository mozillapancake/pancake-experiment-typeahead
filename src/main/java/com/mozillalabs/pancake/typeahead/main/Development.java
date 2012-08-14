package com.mozillalabs.pancake.typeahead.main;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

public class Development
{
//    static TypeaheadElement createTypeaheadElement(String title)
//    {
//        TypeaheadElement element = new SimpleTypeaheadElement(getNextElementId());
//        element.setTerms(title.toLowerCase().split("\\s+"));
//        element.setLine1(title);
//        element.setScore(1000);
//        return element;
//    }
//
//    static void indexElements(Indexer<TypeaheadElement> indexer) throws Exception
//    {
//
//        logger.info("Indexing titles.txt");
//
//        FileInputStream fileInputStream = new FileInputStream("titles.txt");
//        DataInputStream in = new DataInputStream(fileInputStream);
//        BufferedReader br = new BufferedReader(new InputStreamReader(in), 16 * 1024 * 1024);
//
//        int i = 0;
//
//        String line;
//        while ((line = br.readLine()) != null)   {
//            indexer.index(createTypeaheadElement(line));
//            i++;
//            if ((i % 5000) == 0) {
//                logger.info("Indexed " + i);
//                indexer.flush();
//            }
//
//            if (i == 250000) {
//                break;
//            }
//        }
//
//        indexer.flush();
//    }

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
