package ch.scs.unibas.fittingwizard.application.tools;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.commons.validator.routines.EmailValidator;

import javax.mail.Session;
import java.util.Properties;

/**
 * User: mhelmer
 * Date: 16.12.13
 * Time: 13:28
 */
public class Notifications {
    public static final String SenderKey = "mail.sender";
    public static final String RecipientKey = "mail.recipient";

    private final Properties props;

    public Notifications(Properties props) {
        this.props = props;
    }

    public void sendGaussianDoneNotification(boolean isLogValid) {
        if (isMailRecipientAndSenderDefined()) {
            sendMail(isLogValid);
        }
    }

    public boolean isMailRecipientAndSenderDefined() {
        return EmailValidator.getInstance().isValid(getRecipient()) &&
                EmailValidator.getInstance().isValid(getSender());
    }

    private void sendMail(boolean isLogValid) {
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

    private String getSender() {
        return props.getProperty(SenderKey);
    }

    private String getRecipient() {
        return props.getProperty(RecipientKey);
    }
}
