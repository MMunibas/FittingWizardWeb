package ch.unibas.fitting.web;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.wicket.protocol.http.ContextParamWebApplicationFactory;
import org.apache.wicket.protocol.http.WicketFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;

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
        ServletContextHandler sch = new ServletContextHandler(ServletContextHandler.SESSIONS);
        FilterHolder fh2 = new FilterHolder(WicketFilter.class);
        fh2.setInitParameter(ContextParamWebApplicationFactory.APP_CLASS_PARAM, App.class.getName());
        fh2.setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, "/*");
        sch.addFilter(fh2, "/*", EnumSet.of(DispatcherType.REQUEST,DispatcherType.ERROR));
        sch.addServlet(DefaultServlet.class, "/*");

        srv.setHandler(sch);

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

        logger.setLevel(Level.INFO);
    }
}
