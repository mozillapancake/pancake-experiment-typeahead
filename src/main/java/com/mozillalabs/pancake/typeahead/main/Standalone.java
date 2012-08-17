
package com.mozillalabs.pancake.typeahead.main;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class Standalone
{
    // parse output-file pagecounts-*

    public static void parseMain(String[] args) throws IOException
    {
        Map<String,Long> titles = new HashMap<String,Long>();

        // Parse the pagecount files

        long total = 0;

        for (int i = 2; i < args.length; i++)
        {
            File file = new File(args[i]);
            System.out.println(new Date() + " +++ Processing: " + file.getName());

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                try {
                    String[] elements = line.split("\t");
                    String word = URLDecoder.decode(elements[0], "UTF-8").replace("_", " ");
                    long count = Long.parseLong(elements[1]);
                    titles.put(word, count);
                    total += count;
                } catch (Throwable t) {
                    // Ignored
                }
            }

            System.out.println(new Date() + " Heap: total=" + (Runtime.getRuntime().totalMemory()/1024/1024) + " max=" + (Runtime.getRuntime().maxMemory()/1024/1024) + " free=" + (Runtime.getRuntime().freeMemory()/1024/1024));
        }

        System.out.println(new Date() + " Number of titles: " + titles.size());
        System.out.println(new Date() + " Total: " + total);

        // Write the words out to words.dump as [ ["Foo Cheese", ["foo", "cheese"], 0.1234], ... ]

        System.out.println(new Date() + " Writing words out");

        FileOutputStream fileOutputStream = new FileOutputStream(args[1]);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        for (String word : titles.keySet()) {
            float score = (float) titles.get(word) / (float) total;
            String[] terms = word.toLowerCase().split("\\s+");
            objectOutputStream.writeObject(new Object[]{word, terms, score});
        }
        objectOutputStream.close();
        fileOutputStream.close();

        System.out.println(new Date() + " Done writing words out");
        System.out.println(new Date() + " Heap: " + Runtime.getRuntime().totalMemory() + " / " + Runtime.getRuntime().maxMemory() + " / " + Runtime.getRuntime().freeMemory());
    }

    public static void importMain(String[] args)
    {
    }

    public static void serveMain(String[] args)
    {
        Server server = new Server();

        Connector connector = new SelectChannelConnector();
        connector.setPort(8080);
        connector.setHost("0.0.0.0");
        server.addConnector(connector);

        WebAppContext context = new WebAppContext();
        context.setServer(server);
        context.setContextPath("/");

        ProtectionDomain protectionDomain = Standalone.class.getProtectionDomain();
        URL location = protectionDomain.getCodeSource().getLocation();
        context.setWar(location.toExternalForm());

        server.setHandler(context);

        try {
            server.start();
            System.in.read();
            server.stop();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) throws IOException
    {
        if (args[0].equals("parse")) {
            parseMain(args);
        } else if (args[0].equals("import")) {
            importMain(args);
        } else if (args[0].equals("serve")) {
            serveMain(args);
        }
    }
}
