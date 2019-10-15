package Lab7;

import org.apache.commons.codec.digest.DigestUtils;
import java.util.Map;
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
        return DigestUtils.shaHex(str);
    }

    public static boolean isAccountCorrect(String login, String password){
        SQLUtils utils = new SQLUtils();
        Map<String, String> accounts = utils.parseAccountsResultSet(utils.getUsersTable());
        boolean result = false;
        if(accounts.containsKey(login)){
            if(accounts.get(login).equals(sha1Coding(password))){
                result = true;
            }
        }
        return result;
    }
}
