package Lab6;

import source.Command;

import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientChannelIO {
    private SocketChannel channel;

    private boolean isConnected;

    private int port;

    private String host;

    private String clientLogin;

    private String clientPassword;

    private boolean isClientLogged = false;

    ClientChannelIO(String host, int port, SocketChannel channel) {
        isConnected = true;
        this.channel = channel;
        this.port = port;
        this.host = host;
    }

    public ClientChannelIO(String host, int port) {
        isConnected = connectionAttempt(host, port);
        this.port = port;
        this.host = host;
    }

    void sendCommand(Command command) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(10000);
            new ObjectOutputStream(byteArrayOutputStream).writeObject(command);
            ByteBuffer buffer = ByteBuffer.allocate(byteArrayOutputStream.size());
            buffer.put(byteArrayOutputStream.toByteArray());
            buffer.flip();
            channel.write(buffer);

        } catch (IOException | NotYetConnectedException e) {
            isConnected = false;
            if (connectionAttempt(host, port)) {
                registration(true);
            } else {
                System.out.println("Во время передачи сообщения произошла ошибка. Сервер недоступен");
            }
        }
    }

    Response receiveResponse() {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(100000);
            channel.read(buffer);
            return (Response) new ObjectInputStream(new ByteArrayInputStream(buffer.array())).readObject();
        } catch (IOException | ClassNotFoundException | NotYetConnectedException e) {
            isConnected = false;
            return Response.createStringResponse("");
        }
    }

    boolean checkConnection() {
        sendCommand(new Command("test", null, null, "", ""));
        return receiveResponse().toString().equals("test");
    }

    private boolean connectionAttempt(String host, int port) {
        if (!isConnected) {
            System.out.println("Установка соединения с сервером");
            try {
                channel = SocketChannel.open();
                channel.connect(new InetSocketAddress(host, port));
                System.out.println("Соединение установлено, необходимо пройти процесс авторизации заново");
                return true;
            } catch (Exception e) {
                System.out.println("Ошибка соединения, сервер недоступен");
                return false;
            }
        } else {
            return true;
        }
    }

    String getClientLogin() {
        return clientLogin;
    }

    String getClientPassword() {
        return clientPassword;
    }

    Command importFile(String str) {
        File file;
        try {
            file = new File(str.split(" ")[1]);
            try {
                FileInputStream fis = new FileInputStream(file);
                byte[] fileContentBuffer = new byte[fis.available()];
                System.out.println("Отправлен файл размером " + fis.read(fileContentBuffer) + " байт");
                return new Command(str, null, new String(fileContentBuffer),clientLogin, clientPassword);
            } catch (FileNotFoundException e) {
                System.out.println("Указанный вами файл не найден");
                return new Command("", null, null,clientLogin, clientPassword);
            } catch (IOException e) {
                System.out.println("Ошибка во время выполнения команды");
                return new Command("", null, null,clientLogin, clientPassword);

            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Неверный формат команды");
            return new Command("", null, null,clientLogin, clientPassword);
        }
    }

    void setClientLogged(String login, String password){
        clientLogin = login;
        clientPassword = password;
        isClientLogged = true;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    void registration(boolean isReconnect){
        ClientChannelIO clientIO = this;
        Scanner scanner = Client.getScanner();
        Response rs = clientIO.receiveResponse();
        System.out.println(rs.toString());
        while (true) {
            try {

                System.out.println("Для авторизации введите \"auth\", для регистрации - \"reg\", для выхода - \"quit\"");
                String line = scanner.nextLine();
                clientIO.sendCommand(new Command("", null, null, line, ""));
                rs = clientIO.receiveResponse();
                switch (rs.toString()) {
                    case "auth":
                        System.out.println("Введите логин");
                        String login = scanner.nextLine();
                        System.out.println("Введите пароль для " + login);
                        String password = scanner.nextLine();
                        clientIO.sendCommand(new Command("", null, null, login, password));
                        rs = clientIO.receiveResponse();
                        System.out.println(rs.toString());
                        if (!rs.isLoggingResponse()) {
                            clientIO.setClientLogged(login, password);
                            if(isReconnect){
                                clientIO.sendCommand(new Command("",null,null,clientLogin, clientPassword));
                            }
                            return;
                        }
                        break;
                    case "reg":
                        System.out.println("Введите адрес электронной почты, на который будет отправлено письмо с вашими данными.");
                        String log = scanner.nextLine();
                        clientIO.sendCommand(new Command("", null, null, log, ""));
                        System.out.println(clientIO.receiveResponse());
                        break;
                    case "Команда не распознана":
                        System.out.println("Команда не распознана. Используйте \"reg\", " +
                                "чтобы создать новую учетную запись или \"auth\", чтобы войти в существующую. Для выхода введите \"quit\"");
                        break;
                    case "Клиент отсоединился":
                        System.exit(0);
                        break;
                }
            } catch (NoSuchElementException e){
                clientIO.sendCommand(new Command("", null, null, "quit", ""));
            }
        }
    }
}
