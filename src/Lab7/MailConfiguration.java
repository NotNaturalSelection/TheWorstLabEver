package Lab7;

import javax.mail.*;
import java.util.Properties;

class MailConfiguration {

    private Session session;
    private static final String ENCODING = "UTF-8";
    private static final String SMTP_SERVER = "smtp.yandex.ru";
    private static final int SMTP_Port = 465;
    private static final String SMTP_AUTH_USER = "NotNaturalSelection";
    private static final String SMTP_AUTH_PWD = "ScAk9aFpI1";
    static final String EMAIL_FROM = "NotNaturalSelection@yandex.ru";

    MailConfiguration() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", SMTP_SERVER);
        properties.put("mail.smtp.port", SMTP_Port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.mime.charset", ENCODING);
        Authenticator auth = new EmailAuthenticator(SMTP_AUTH_USER, SMTP_AUTH_PWD);
        session = Session.getDefaultInstance(properties, auth);
        session.setDebug(false);
    }

    boolean connectTransportSMTP() {
        try {
            Transport transport = session.getTransport("smtp");

            transport.connect(
                    SMTP_SERVER,
                    SMTP_Port,
                    SMTP_AUTH_USER,
                    SMTP_AUTH_PWD);
            return true;
        } catch (MessagingException ignored) {
            return false;
        }
    }

    Session getSession() {
        return session;
    }
}
