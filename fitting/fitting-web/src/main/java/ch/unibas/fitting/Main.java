package ch.unibas.fitting;

import ch.unibas.fitting.application.Version;
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
        var settings = Settings.loadConfig();
        setupConsoleLogger(settings);
        LOGGER.info("Starting fitting web " + Version.getManifestVersion());

        Injector injector = Guice.createInjector(new WebModule(settings));

        var webApp = injector.getInstance(WebApp.class);

        // https://cwiki.apache.org/confluence/pages/viewpage.action?pageId=27848170
        var sh = new ServletHolder();
        sh.setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, "/*");

        var servlet = new WicketServlet() {
            @Override
            protected WicketFilter newWicketFilter() {
                return new WicketFilter(webApp);
            }
        };
        sh.setServlet(servlet);
        var sch = new ServletContextHandler(ServletContextHandler.SESSIONS);
        sch.addServlet(sh, "/*");
        sch.getSessionHandler().getSessionManager().setMaxInactiveInterval(60*60);

        var data_resource_handler = new ResourceHandler();
        data_resource_handler.setResourceBase(settings.getDataDir().getAbsolutePath());
        data_resource_handler.setDirectoriesListed(true);
        data_resource_handler.setCacheControl("no-cache, private");
        ContextHandler data_context_handler = new ContextHandler("/data");
        data_context_handler.setHandler(data_resource_handler);

        var js_resource_handler = new ResourceHandler();
        js_resource_handler.setResourceBase(settings.getJavaScriptDir().getAbsolutePath());
        js_resource_handler.setDirectoriesListed(true);
        ContextHandler js_context_handler = new ContextHandler("/javascript");
        js_context_handler.setHandler(js_resource_handler);

        var help_handler = new ResourceHandler();
        help_handler.setResourceBase(settings.getHelpPagesDir().getAbsolutePath());
        help_handler.setDirectoriesListed(true);
        help_handler.setWelcomeFiles(new String[] {"index.html"});
        ContextHandler help_context_handler = new ContextHandler("/help");
        help_context_handler.setHandler(help_handler);

        var handlers = new HandlerList();
        handlers.addHandler(help_context_handler);
        handlers.addHandler(data_context_handler);
        handlers.addHandler(js_context_handler);
        handlers.addHandler(sch);

        var srv = new Server(settings.getServerPort());
        srv.setHandler(handlers);

        LOGGER.info("Starting jetty server");
        srv.start();
        LOGGER.info("Server started");
        srv.join();
    }

    private static void setupConsoleLogger(Settings settings) {

        BasicConfigurator.configure();

        RollingFileAppender app = null;
        settings.getLogDir().mkdir();
        var log = new File(settings.getLogDir(), "web.log");
        try {
            app = new RollingFileAppender(new PatternLayout(TTCC_CONVERSION_PATTERN), log.getAbsolutePath(), true);
            app.setMaxBackupIndex(20);
        } catch (IOException ex) {
        }
        BasicConfigurator.configure(app);

        LOGGER.setLevel(Level.DEBUG);
    }
}
