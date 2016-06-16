/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import javax.mail.Session;

import ch.unibas.fitting.shared.config.ConfigFile;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.commons.validator.routines.EmailValidator;

/**
 * User: mhelmer Date: 16.12.13 Time: 13:28
 */
public class Notifications {

    public static final String SenderKey = "mail.sender";
    public static final String RecipientKey = "mail.recipient";

    private final Properties props;

    public Notifications(ConfigFile props) {
        this.props = props.getProperties();
    }

    private String getSender() {
        return props.getProperty(SenderKey);
    }

    private String getRecipient() {
        return props.getProperty(RecipientKey);
    }

    public boolean isMailRecipientAndSenderDefined() {
        return EmailValidator.getInstance().isValid(getRecipient())
                && EmailValidator.getInstance().isValid(getSender());
    }

    public void sendGaussianDoneNotification(boolean isLogValid) {
        if (isMailRecipientAndSenderDefined()) {
            sendMailGaussian(isLogValid);
        }
    }

    public void sendTestMail() {
        if (isMailRecipientAndSenderDefined()) {
            sendMailTesting();
        }
    }
    
    public void sendLogMail() {
        if (isMailRecipientAndSenderDefined()) {
            sendErrorLogByMail();
        }
    }

    private void sendMailTesting() {
        try {
            Email email = new SimpleEmail();
            email.setMailSession(Session.getDefaultInstance(props));

            email.setSubject("Test mail");
            email.setMsg("This is a test mail for checking parameters from config file");
            email.setFrom(getSender().trim());
            email.addTo(getRecipient().trim());

            email.send();
        } catch (EmailException e) {
            throw new RuntimeException("Could not send notification.", e);
        }
    }

    private void sendMailGaussian(boolean isLogValid) {
        try {
            Email email = new SimpleEmail();
            email.setMailSession(Session.getDefaultInstance(props));

            email.setSubject("Gaussian calculation finished");
            email.setMsg("Gaussian calculation finished. Log file validation returned: " + isLogValid);
            email.setFrom(getSender().trim());
            email.addTo(getRecipient().trim());

            email.send();
        } catch (EmailException e) {
            throw new RuntimeException("Could not send notification.", e);
        }
    }

    private void sendErrorLogByMail()
    {
        try {
            Email email = new SimpleEmail();
            email.setMailSession(Session.getDefaultInstance(props));

            email.setSubject("Log of FW session");
            email.setMsg(
                    new String(Files.readAllBytes(Paths.get("fw-log.txt")))
            );
            email.setFrom(getSender().trim());
            email.addTo(getRecipient().trim());

            email.send();
        } catch (IOException | EmailException e) {
            throw new RuntimeException("Could not send notification.", e);
        }
    }
}
