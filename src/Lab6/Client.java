package Lab6;

import source.Command;
import source.ConsoleLineApp;
import source.Converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.*;

public class Client {
    private static final List<String> argumentCommands = Arrays.asList("import", "add", "add_if_min", "remove");
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Недостаточное количество аргументов. Повторите попытку указав адрес и номер порта");
            System.exit(0);
        }
        SocketChannel channel;
        boolean connection = false;

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


        System.out.println(clientIO.receiveResponse());

        while (true) {
            String str;
            try {
                str = scanner.nextLine();
            } catch (NoSuchElementException e) {
                str = "quit";
            }
            Command command;

            if (argumentCommands.contains(ConsoleLineApp.commandIdentification(str))) {
                if (ConsoleLineApp.commandIdentification(str).equals("import")) {
                    command = importFile(str);
                } else {
                    command = new Command(str, Converter.fromConsoleToObject(str, scanner), null);
                }
            } else {
                command = new Command(str, null, null);
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

    static Command importFile(String str) {
        File file;
        try {
            file = new File(str.split(" ")[1]);
            try {
                FileInputStream fis = new FileInputStream(file);
                byte[] fileContentBuffer = new byte[fis.available()];
                System.out.println("Отправлен файл размером " + fis.read(fileContentBuffer) + " байт");
                return new Command(str, null, new String(fileContentBuffer));
            } catch (FileNotFoundException e) {
                System.out.println("Указанный вами файл не найден");
                return new Command("", null, null);
            } catch (IOException e) {
                System.out.println("Ошибка во время выполнения команды");
                return new Command("", null, null);

            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Неверный формат команды");
            return new Command("", null, null);
        }
    }

}
