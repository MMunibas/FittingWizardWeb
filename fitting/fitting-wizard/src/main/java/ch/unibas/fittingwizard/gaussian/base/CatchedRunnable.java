/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.gaussian.base;

import ch.unibas.fittingwizard.gaussian.base.dialog.OverlayDialog;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

/**
 * User: mhelmer
 * Date: 10.12.13
 * Time: 12:13
 */
public abstract class CatchedRunnable implements Runnable {
    private static final Logger logger = Logger.getLogger(CatchedRunnable.class);
    @Override
    public void run() {
        try {
            safelyRun();
        } catch (Exception e) {
            logger.error("An unhandeled error occured.", e);
            showError(e);
        }
    }

    private void showError(Exception e) {
        String details = ExceptionUtils.getMessage(e);

        OverlayDialog.showError("An unhandeled erro occured",
                "The error contained the following message:\n" + details);
    }

    public abstract void safelyRun();
}
