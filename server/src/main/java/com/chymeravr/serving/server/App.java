package com.chymeravr.serving.server;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.chymeravr.serving.processing.rqhandler.V1EntryPoint;
import com.chymeravr.serving.server.guice.CacheModule;
import com.chymeravr.serving.server.guice.ConfigModule;
import com.chymeravr.serving.server.guice.ProcessingModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.configuration.Configuration;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

public class App {
    @Parameter(names = {"--config", "-c"}, description = "config file", required = true)
    private String configFilePath;

    @Parameter(names = {"--port", "-p"}, description = "port", required = true)
    private int port;

    public static void main(String... args) throws Exception {
        App app = new App();
        new JCommander(app, args);
        app.run();
    }

    private void run() throws Exception {
        Server server = new Server(port);

        Injector configInjector = Guice.createInjector(new ConfigModule(this.configFilePath));
        Configuration config = configInjector.getInstance(Configuration.class);

        Injector injector = Guice.createInjector(new CacheModule(config), new ProcessingModule(config));
        V1EntryPoint v1EntryPoint = injector.getInstance(V1EntryPoint.class);

        ContextHandler context = new ContextHandler();
        context.setContextPath("/api/v1/ads/");
        context.setHandler(v1EntryPoint);

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[]{context});

        server.setHandler(contexts);

        server.start();
        server.join();
    }
}