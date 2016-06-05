package ch.unibas.fitting.web.application;

import org.apache.log4j.Logger;

import javax.inject.Singleton;

/**
 * Created by martin on 04.06.2016.
 */

public class SomeService {

    private static final Logger logger = Logger.getLogger(SomeService.class);

    public SomeService() {
        logger.info("hello service");
    }
}
