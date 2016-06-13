package ch.unibas.fitting.web;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.wicket.protocol.http.ContextParamWebApplicationFactory;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.protocol.http.WicketServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

/**
 * Created by martin on 26.05.2016.
 */
public class Main {

    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        setupConsoleLogger();
        Logger.getLogger(Main.class).info("Starting fitting web");

        Server srv = new Server(8080);

        // https://cwiki.apache.org/confluence/pages/viewpage.action?pageId=27848170
        ServletHolder sh = new ServletHolder(WicketServlet.class);
        sh.setInitParameter(ContextParamWebApplicationFactory.APP_CLASS_PARAM, WebApp.class.getName());
        sh.setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, "/*");

        ServletContextHandler sch = new ServletContextHandler(ServletContextHandler.SESSIONS);
        sch.addServlet(sh, "/*");

        ResourceHandler js_resource_handler = new ResourceHandler();
        js_resource_handler.setResourceBase("build/resources/main/js/");
        js_resource_handler.setDirectoriesListed(true);
        ContextHandler context_handler = new ContextHandler("/js");
        context_handler.setHandler(js_resource_handler);

        ResourceHandler mol_resource_handler = new ResourceHandler();
        mol_resource_handler.setResourceBase("build/resources/main/mol/");
        mol_resource_handler.setDirectoriesListed(true);
        ContextHandler mol_context_handler = new ContextHandler("/mol");
        mol_context_handler.setHandler(mol_resource_handler);

        HandlerList handlers = new HandlerList();
        handlers.addHandler(context_handler);
        handlers.addHandler(mol_context_handler);

        handlers.addHandler(sch);

        srv.setHandler(handlers);

        logger.info("Starting jetty server");
        srv.start();
        logger.info("Server started");
        srv.join();
    }

    private static void setupConsoleLogger() {

        BasicConfigurator.configure();

//        FileAppender app = null;
//        File log = new File("fw-log.txt");
//
//        try {
//            app = new FileAppender(new PatternLayout(TTCC_CONVERSION_PATTERN), log.getAbsolutePath() ,false);
//        } catch (IOException ex) {
//
//        }
//        BasicConfigurator.configure(app);

        logger.setLevel(Level.DEBUG);
    }
}
