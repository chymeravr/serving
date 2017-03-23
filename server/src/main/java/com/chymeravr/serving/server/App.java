package com.chymeravr.serving.server;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.chymeravr.serving.logging.EventLogger;
import com.chymeravr.serving.server.dag.ExecutionDag;
import com.chymeravr.serving.server.guice.AppServletModule;
import com.chymeravr.serving.server.guice.CacheModule;
import com.chymeravr.serving.server.guice.ConfigModule;
import com.chymeravr.serving.server.guice.WorkerModule;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.servlets.MetricsServlet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceFilter;
import org.apache.commons.configuration.Configuration;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.EnumSet;

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

        ConfigModule configModule = new ConfigModule(this.configFilePath);

        Injector configInjector = Guice.createInjector(configModule);
        Configuration config = configInjector.getInstance(Configuration.class);

        Injector injector = Guice.createInjector(
                Stage.PRODUCTION,
                new AppServletModule(),
                new WorkerModule(),
                new ExecutionDag(),
                new CacheModule(config),
                configModule
        );


        ServletContextHandler metricsServletHandler = new ServletContextHandler(server, "/health/");
        MetricsServlet metricsServlet = new MetricsServlet(injector.getInstance(MetricRegistry.class));
        ServletHolder servletHolder = new ServletHolder(metricsServlet);
        metricsServletHandler.addServlet(servletHolder, "/metrics");

        metricsServletHandler.addServlet(new ServletHolder(),
                "/kafka");

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[]{metricsServletHandler});

        server.setHandler(contexts);


        ServletContextHandler context = new ServletContextHandler(server, "/");
        context.addFilter(GuiceFilter.class, "/*", EnumSet.of(javax.servlet.DispatcherType.REQUEST, javax.servlet.DispatcherType.ASYNC));
        context.addServlet(DefaultServlet.class, "/*");

        server.start();
        server.join();
    }
}