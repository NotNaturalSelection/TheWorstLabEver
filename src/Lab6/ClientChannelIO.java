package Lab6;

import source.Command;

import java.io.*;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ClientChannelIO {
    private SocketChannel channel;

    private boolean isConnected;

    private int port;

    private String host;

    private String clientLogin;

    private String clientPassword;

    boolean isClientLogged = false;

    public ClientChannelIO(String host, int port, SocketChannel channel) {
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
            //e.printStackTrace();
            if (connectionAttempt(host, port)) {
                System.out.println(receiveResponse().toString());
                sendCommand(command);
            } else {
                System.out.println("Во время передачи сообщения произошла ошибка. Сервер недоступен");
            }
        }
    }

    Response receiveResponse() {
        try {
//            return (Response) new ObjectInputStream(channel.socket().getInputStream()).readObject();
//            byte[] array = new byte[100000];
            ByteBuffer buffer = ByteBuffer.allocate(100000);
            channel.read(buffer);
            return (Response) new ObjectInputStream(new ByteArrayInputStream(buffer.array())).readObject();
        } catch (IOException | ClassNotFoundException | NotYetConnectedException e) {
            isConnected = false;
//            //e.printStackTrace();
//            if (connectionAttempt(host, port)) {
//                return Response.createStringResponse("");
//            }
//            return Response.createStringResponse("Во время получения ответа сервера произошла ошибка. Сервер недоступен");
            return Response.createStringResponse("");
        }
    }

    boolean checkConnection() {
        sendCommand(new Command("test", null, null, "", ""));
        return receiveResponse().toString().equals("test");
    }

    boolean connectionAttempt(String host, int port) {
        if (!isConnected) {
            System.out.println("Установка соединения с сервером");
            try {
                channel = SocketChannel.open();
                channel.connect(new InetSocketAddress(host, port));
                System.out.println("Соединение установлено");
                return true;
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println("Ошибка соединения, сервер недоступен");
                return false;
            }
        } else {
            return isConnected;
        }
    }

    public String getClientLogin() {
        return clientLogin;
    }

    public String getClientPassword() {
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

    public void setClientLogged(String login, String password){
        clientLogin = login;
        clientPassword = password;
        isClientLogged = true;
    }

    public SocketChannel getChannel() {
        return channel;
    }
}
