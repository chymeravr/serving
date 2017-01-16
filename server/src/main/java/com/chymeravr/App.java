package com.chymeravr;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.chymeravr.adgroup.AdgroupCache;
import com.chymeravr.guice.CacheModule;
import com.chymeravr.placement.PlacementCache;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

/**
 * The simplest possible Jetty server.
 */
public class App {
    @Parameter(names={"--config", "-c"})
    private String configFilePath;

    public static void main(String ... args) throws Exception {
        App app = new App();
        new JCommander(app, args);
        app.run();
    }

    private void run() throws Exception {

        Server server = new Server(8080);

        ContextHandler context = new ContextHandler("/");
        context.setContextPath("/");
        context.setHandler(new RequestHandler("Root Hello"));

        ContextHandler contextFR = new ContextHandler("/fr");
        contextFR.setHandler(new RequestHandler("Bonjoir"));

        ContextHandler contextIT = new ContextHandler("/it");
        contextIT.setHandler(new RequestHandler("Bongiorno"));

        ContextHandler contextV = new ContextHandler("/");
        contextV.setVirtualHosts(new String[]{"127.0.0.2"});
        contextV.setHandler(new RequestHandler("Virtual Hello"));

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[]{context, contextFR, contextIT, contextV});

        server.setHandler(contexts);
        Injector injector = Guice.createInjector(new CacheModule(this.configFilePath));


        PlacementCache placementCache = injector.getInstance(PlacementCache.class);
        AdgroupCache adgroupCache = injector.getInstance(AdgroupCache.class);


        server.start();
        server.join();
    }
}