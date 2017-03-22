package com.chymeravr.serving.server;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.chymeravr.serving.server.dag.ExecutionDag;
import com.chymeravr.serving.server.guice.AppServletModule;
import com.chymeravr.serving.server.guice.WorkerModule;
import com.google.inject.Guice;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

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

//        Injector configInjector = Guice.createInjector(new ConfigModule(this.configFilePath));
//        Configuration config = configInjector.getInstance(Configuration.class);
//
//        Injector injector = Guice.createInjector(new CacheModule(config), new ProcessingModule(config));
//        V1EntryPoint v1EntryPoint = injector.getInstance(V1EntryPoint.class);
//
//        ContextHandler context = new ContextHandler();
//        context.setContextPath("/api/v1/ads/");
//        context.setHandler(v1EntryPoint);
//
//        ServletContextHandler metricsServletHandler = new ServletContextHandler(server, "/health/");
//
//        MetricsServlet metricsServlet = new MetricsServlet(injector.getInstance(MetricRegistry.class));
//        ServletHolder servletHolder = new ServletHolder(metricsServlet);
//        metricsServletHandler.addServlet(servletHolder, "/metrics");
//
//        metricsServletHandler.addServlet(new ServletHolder(new MetricsServlet(SharedMetricRegistries.getOrCreate(EventLogger.KAFKA_REGISTRY))),
//                "/kafka");
//
//        ContextHandlerCollection contexts = new ContextHandlerCollection();
//        contexts.setHandlers(new Handler[]{context, metricsServletHandler});

//        server.setHandler(contexts);

        Guice.createInjector(
                Stage.PRODUCTION,
                new WorkerModule(),
                new AppServletModule(),
                new ExecutionDag()
        );

        ServletContextHandler context = new ServletContextHandler(server, "/");
        context.addFilter(GuiceFilter.class, "/*", EnumSet.of(javax.servlet.DispatcherType.REQUEST, javax.servlet.DispatcherType.ASYNC));
        context.addServlet(DefaultServlet.class, "/*");

        server.start();
        server.join();
    }
}