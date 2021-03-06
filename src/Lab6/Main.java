package Lab6;

import Lab7.SQLUtils;
import source.ConsoleLineApp;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Main {
    private static final int port = 3745;

    public static void main(String[] args) {
        try {
            int numberOfClient = 0;
            InetAddress inetAddress = InetAddress.getByName("localhost");
            ServerSocket serverSocket = new ServerSocket(port, 0, inetAddress);
            ConsoleLineApp app = new ConsoleLineApp(new Date());
            SQLUtils utils = new SQLUtils();
            utils.createTableAccounts();
            utils.createTableProtagonists();
            while (true) {
                Socket socket = serverSocket.accept();
                new Server().setSocket(socket, numberOfClient++, app);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Порт или хост недоступен");
        }
    }
}
