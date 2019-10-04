package Lab6;

import source.Command;
import source.ConsoleLineApp;
import source.Converter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.*;

public class Client {
    private static final List<String> argumentCommands = Arrays.asList("add", "add_if_min", "remove");
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Недостаточное количество аргументов. Повторите попытку указав адрес и номер порта");
            System.exit(0);
        }
        SocketChannel channel;
        while (true) {
            try {
                channel = SocketChannel.open();
                channel.connect(new InetSocketAddress(args[0], Integer.parseInt(args[1])));
                break;
            } catch (IOException | NullPointerException e) {
                System.out.println("Сервер недоступен, следующая попытка соединения через 5 секунд");
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException | IllegalMonitorStateException ignored) {

            }
        }
        ClientChannelIO clientIO = new ClientChannelIO(args[0], Integer.parseInt(args[1]), channel);
        boolean auth = false;



        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        Response rs = clientIO.receiveResponse();
        System.out.println(rs.toString());
        while (!auth) {
            System.out.println("Для авторизации введите \"auth\", для регистрации - \"reg\", для выхода - \"quit\"");
            String line = scanner.nextLine();
            clientIO.sendCommand(new Command("", null, null, line, ""));
            rs = clientIO.receiveResponse();
            switch (rs.toString()){
                case "auth":
                    System.out.println("Введите логин");
                    String login = scanner.nextLine();
                    System.out.println("Введите пароль для "+login);
                    String password = scanner.nextLine();
                    clientIO.sendCommand(new Command("", null,null, login, password));
                    rs = clientIO.receiveResponse();
                    System.out.println(rs.toString());
                    if(!rs.isLoggingResponse()){
                        auth = true;
                        clientIO.setClientLogged(login, password);
                    }
                    break;
                case "reg":
                    System.out.println("Введите адрес электронной почты, на который будет отправлено письмо с вашими данными.");
                    String log = scanner.nextLine();
                    clientIO.sendCommand(new Command("",null,null,log,""));
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
        }
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


        while (true) {
            String str;
            try {
                str = scanner.nextLine();
            } catch (NoSuchElementException e) {
                str = "quit";
            }
            Command command;

            if (argumentCommands.contains(ConsoleLineApp.commandIdentification(str))) {
//                if (ConsoleLineApp.commandIdentification(str).equals("import")) {
//                    command = clientIO.importFile(str);
//                } else {
                    command = new Command(str, Converter.fromConsoleToObject(str, scanner), null, clientIO.getClientLogin(), clientIO.getClientPassword());
//                }
            } else {
                command = new Command(str, null, null, clientIO.getClientLogin(), clientIO.getClientPassword());
            }

            if (command.getStringCommand().equals("quit") || command.getStringCommand().equals("exit")) {
                System.out.println("Завершение работы приложения");
                System.exit(0);
            }

            clientIO.sendCommand(command);

            Response callback = clientIO.receiveResponse();

            System.out.println(callback);

            if (callback.toString().equals("Клиент отсоединился")) {
                System.exit(0);
            }
        }
    }
}
