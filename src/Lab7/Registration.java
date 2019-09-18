package Lab7;

import org.apache.commons.codec.digest.DigestUtils;

import java.security.SecureRandom;
import java.util.Random;

public class Registration {
    private static final String passwordAlphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";

    public boolean sendPasswordEmail(String emailAddress){
        MailSending ms = new MailSending();
        ms.configureMessage(emailAddress, createRandomPassword());
        return ms.sendMessage();
    }

    private String createRandomPassword() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        int i = 8 + random.nextInt(5);
        for (int j = 0; j<i; j++){
            sb.append(passwordAlphabet.charAt(random.nextInt(passwordAlphabet.length())));
        }
        return sb.toString();
    }
}
