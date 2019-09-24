package Lab6;

import Lab7.*;
import source.Command;
import source.ConsoleLineApp;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class Server extends Thread {

    private Socket socket;
    private int numberOfClient;
    private ConsoleLineApp app;
    private Map<String, String> accounts;

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
            updateAccountsMap();
            boolean auth = false;
            serverIO.sendResponse(Response.createLoggingResponse("Соединение выполнено успешно"));


//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            while (!auth) {
                Command cmd = serverIO.receiveCommand();
                switch (cmd.getLogin()) {
                    case "reg":
                        serverIO.sendResponse(Response.createLoggingResponse("reg"));
                        cmd = serverIO.receiveCommand();
                        if(MailSending.isMailAddressCorrect(cmd.getLogin())){
                            if(accounts.containsKey(cmd.getLogin())){
                                serverIO.sendResponse(Response.createLoggingResponse("На данный почтовый адрес уже зарегистрирована учетная запись"));
                            } else {
                                Registration reg = new Registration();
                                String password = reg.createRandomPassword();
                                reg.sendPasswordEmail(cmd.getLogin(), password);
                                accounts.put(cmd.getLogin(), Registration.sha1Coding(password));
                                SQLUtils utils = new SQLUtils(new DBConnection());
                                utils.addNewAccount(cmd.getLogin(), password);
                                serverIO.sendResponse(Response.createLoggingResponse("На введенный вами адрес электронной почты было отправлено письмо с данными вашей учетной записи. Процесс регистрации завершен"));
                            }
                        } else {
                            serverIO.sendResponse(Response.createLoggingResponse("Почтовый адрес некорректен. Процесс регистрации прерван"));
                        }
                        break;
                    case "auth":
                        serverIO.sendResponse(Response.createLoggingResponse("auth"));
                        Command log = serverIO.receiveCommand();
                        if (isLoginValid(log.getLogin())) {

                            if (isPasswordValid(log.getLogin(), log.getPassword())) {
                                auth = true;
                                serverIO.sendResponse(Response.createStringResponse("Вход выполнен успешно"));
                            } else {
                                serverIO.sendResponse(Response.createLoggingResponse("Пароль неверен. Процесс авторизации прерван"));
                            }
                        } else {
                            serverIO.sendResponse(Response.createLoggingResponse("Такой учетной записи не существует. Процесс авторизации прерван"));
                        }
                        break;
                    case "quit":
                        disconnectClient(dos);
                        break;
                    default:
                        serverIO.sendResponse(Response.createLoggingResponse("Команда не распознана"));
                        break;
                }
            }
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


            while (true) {
                Command command = serverIO.receiveCommand();
                String line = command.getStringCommand();
                if (line == null) {
                    disconnectClient(dos);
                    break;
                } else if (line.equals("quit") || line.equals("Клиент отсоединился")) {
                    disconnectClient(dos);
                    break;
                } else if (line.equals("test")) {
                    serverIO.sendResponse(Response.createStringResponse("test"));
                } else {
                    new CommandExecution(command, dos, app);
                }
            }
            this.interrupt();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void disconnectClient(DataOutputStream dos) {
        System.out.println("Клиент " + numberOfClient + " отсоединился");
        new ServerIO(null, dos).sendResponse(Response.createStringResponse("Клиент отсоединился"));
        this.interrupt();
    }

    private boolean isLoginValid(String login) {
        return accounts.containsKey(login);
    }

    private boolean isPasswordValid(String login, String password) {
        return accounts.get(login).equals(Registration.sha1Coding(password));
    }

    private void  updateAccountsMap() {
        SQLUtils sqlUtils = new SQLUtils(new DBConnection());
        accounts = sqlUtils.parseAccountsResultSet(sqlUtils.getUsersTable());
    }
}
