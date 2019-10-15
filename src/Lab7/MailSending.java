package Lab7;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSending {

    private boolean sendable;
    private Message message;
    private MailConfiguration configuration;

    MailSending(){
        this.configuration = new MailConfiguration();
        sendable = false;
    }

    void configureMessage(String address, String password) {
        try {
            this.message = new MimeMessage(configuration.getSession());
            this.message.setText("Вы успешно зарегистрировали свою учетную запись в моей лабе. Ваш логин: " + address + ". Ваш пароль: " + password + ". Никому не сообщайте ваши данные.");
            this.message.setSubject("Регистрация прошла успешно");
            this.message.setFrom(new InternetAddress(MailConfiguration.EMAIL_FROM));
            this.message.setRecipient(Message.RecipientType.TO, new InternetAddress(address));
            sendable = true;
        } catch (MessagingException ignored){
        }
    }

    boolean sendMessage(){
        if(sendable) {
            if(configuration.connectTransportSMTP()){
                try {
                    Transport.send(message);
                    sendable = false;
                    return true;
                } catch (MessagingException e){
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean isMailAddressCorrect(String address){
        return address.indexOf('@') > 0 && address.indexOf('.') < address.length() - 1;
    }
}
