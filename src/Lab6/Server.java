package Lab6;

import source.Command;
import source.ConsoleLineApp;

import java.io.*;
import java.net.Socket;

public class Server extends Thread {

    private Socket socket;
    private int numberOfClient;
    private ConsoleLineApp app;

    Server() {
    }

    void setSocket(Socket socket, int numberOfClient, ConsoleLineApp app) {
        this.app = app;
        this.socket = socket;
        this.numberOfClient = numberOfClient;
        setDaemon(true);
        start();
    }

    @Override
    public void run() {
        try {
            System.out.println("Клиент " + numberOfClient + " присоединился");
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            ServerIO serverIO = new ServerIO(dis, dos);
            serverIO.sendResponse(Response.createStringResponse("Соединение выполнено успешно"));
            while (true) {
                Command command = serverIO.receiveCommand();
                String line = command.getStringCommand();
                if (line == null) {
                    disconnectClient(dos);
                    break;
                } else if (line.equals("quit") || line.equals("Клиент отсоединился")) {
                    disconnectClient(dos);
                    break;
                } else if(line.equals("test")){
                    serverIO.sendResponse(Response.createStringResponse("test"));
                } else {
                    new CommandExecution(command, dos, app);
                }
            }
            this.interrupt();
        } catch (IOException e) {
            e.printStackTrace(); // todo исправить по окончании
        }
    }

    private void disconnectClient(DataOutputStream dos) {
        System.out.println("Клиент " + numberOfClient + " отсоединился");
        new ServerIO(null,dos).sendResponse(Response.createStringResponse("Клиент отсоединился"));
        this.interrupt();
    }
}
