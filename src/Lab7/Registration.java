package Lab7;

import org.apache.commons.codec.digest.DigestUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

public class Registration {
    private static final String passwordAlphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";

    public boolean sendPasswordEmail(String emailAddress, String password){
        MailSending ms = new MailSending();
        ms.configureMessage(emailAddress, password);
        return ms.sendMessage();
    }

    public String createRandomPassword() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        int i = 8 + random.nextInt(5);
        for (int j = 0; j<i; j++){
            sb.append(passwordAlphabet.charAt(random.nextInt(passwordAlphabet.length())));
        }
        return sb.toString();
    }

    public static String sha1Coding(String str)  {
        return DigestUtils.sha1Hex(str);
    }

    public static void main(String[] args) {
        System.out.println(sha1Coding("Hello"));
        System.out.println(sha1Coding("Hello"));
    }
}
