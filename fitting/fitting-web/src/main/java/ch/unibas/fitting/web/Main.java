package ch.unibas.fitting.web;

import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.web.application.Version;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.log4j.*;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.protocol.http.WicketServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.io.File;
import java.io.IOException;

import static org.apache.log4j.EnhancedPatternLayout.TTCC_CONVERSION_PATTERN;

/**
 * Created by martin on 26.05.2016.
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        WebSettings settings = WebSettings.load();
        setupConsoleLogger(settings);
        LOGGER.info("Starting fitting web " + Version.getManifestVersion());

        Injector injector = Guice.createInjector(new WebModule(settings));

        WebApp webApp = injector.getInstance(WebApp.class);
        Server srv = new Server(settings.getServerPort());

        // https://cwiki.apache.org/confluence/pages/viewpage.action?pageId=27848170
        ServletHolder sh = new ServletHolder();
        sh.setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, "/*");

        WicketServlet servlet = new WicketServlet() {
            @Override
            protected WicketFilter newWicketFilter() {
                return new WicketFilter(webApp);
            }
        };
        sh.setServlet(servlet);
        ServletContextHandler sch = new ServletContextHandler(ServletContextHandler.SESSIONS);
        sch.addServlet(sh, "/*");

        ResourceHandler data_resource_handler = new ResourceHandler();
        data_resource_handler.setResourceBase(settings.getDataDir().getAbsolutePath());
        data_resource_handler.setDirectoriesListed(true);
        ContextHandler data_context_handler = new ContextHandler("/data");
        data_context_handler.setHandler(data_resource_handler);

        ResourceHandler js_resource_handler = new ResourceHandler();
        js_resource_handler.setResourceBase(settings.getJavaScriptDir().getAbsolutePath());
        js_resource_handler.setDirectoriesListed(true);
        ContextHandler context_handler = new ContextHandler("/javascript");
        context_handler.setHandler(js_resource_handler);

        HandlerList handlers = new HandlerList();
        handlers.addHandler(data_context_handler);
        handlers.addHandler(context_handler);
        handlers.addHandler(sch);

        srv.setHandler(handlers);

        injector.getInstance(DataLoader.class).loadExistingData();

        LOGGER.info("Starting jetty server");
        srv.start();
        LOGGER.info("Server started");
        srv.join();
    }

    private static void setupConsoleLogger(Settings settings) {

        BasicConfigurator.configure();

        RollingFileAppender app = null;
        settings.getLogDir().mkdir();
        File log = new File(settings.getLogDir(), "web.log");
        try {
            app = new RollingFileAppender(new PatternLayout(TTCC_CONVERSION_PATTERN), log.getAbsolutePath(), true);
            app.setMaxBackupIndex(20);
        } catch (IOException ex) {
        }
        BasicConfigurator.configure(app);

        LOGGER.setLevel(Level.DEBUG);
    }
}
