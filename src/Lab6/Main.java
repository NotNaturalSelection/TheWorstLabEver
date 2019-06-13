package Lab6;

import source.ConsoleLineApp;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Main {
    private static final int port = 3745;

    public static void main(String[] args) {
        try {
            try {
                int numberOfClient = 0;
                InetAddress inetAddress = InetAddress.getByName("localhost");
                ServerSocket serverSocket = new ServerSocket(port, 0, inetAddress);
                ConsoleLineApp app = new ConsoleLineApp(new Date());
                while (true) {
                    Socket socket = serverSocket.accept();
                    new Server().setSocket(socket, numberOfClient++, app);
                }
            } catch (Exception e) {
                System.out.println("Порт или хост недоступен");
            }
        } catch (Exception e) {
        }
    }
}
