package Lab6;

import Lab7.DBConnection;
import Lab7.SQLUtils;
import source.*;

import java.io.DataOutputStream;
import java.util.NoSuchElementException;

public class CommandExecution extends Thread {
    private Command command;
    private DataOutputStream dos;
    private ConsoleLineApp app;
    private SQLUtils sqlUtils;

    CommandExecution(Command command, DataOutputStream dos, ConsoleLineApp app) {
        this.command = command;
        this.dos = dos;
        this.app = app;
        this.sqlUtils = new SQLUtils(new DBConnection());
        this.start();
    }

    @Override
    public void run() {
        execution(command);
        this.interrupt();
    }

    private void execution(Command command) {
        try {
            try {
                ServerIO tcpIO = new ServerIO(null, dos);
                String cmd;
                try {
                    cmd = ConsoleLineApp.commandIdentification(command.getStringCommand());
                } catch (NullPointerException e) {
                    tcpIO.sendResponse(Response.createStringResponse("Команда не распознана"));
                    cmd = "";
                    this.interrupt();
                }
                cmd = cmd.trim();
                System.out.println(cmd);
                switch (cmd) {
//                    case "import":
//                        tcpIO.sendResponse(Response.createStringResponse(app.importFile(command)));
//                        break;
                    case "":
                        tcpIO.sendResponse(Response.createStringResponse(""));
                        break;
                    case "show":
                        tcpIO.sendResponse(Response.createFullResponse("collection", app.getCol()));
                        break;
                    case "add":
                        tcpIO.sendResponse(Response.createStringResponse(app.add(command.getObject(),command.getLogin())));
                        break;
                    case "add_if_min":
                        tcpIO.sendResponse(Response.createStringResponse(app.addIfMin(command.getObject(), command.getLogin())));
                        break;
                    case "remove":
                        tcpIO.sendResponse(Response.createStringResponse(app.remove(command.getObject(), command.getLogin())));
                        break;
                    case "info":
                        tcpIO.sendResponse(Response.createStringResponse(app.info(app)));
                        break;
                    case "load":
                        tcpIO.sendResponse(Response.createStringResponse(sqlUtils.loadCollection(app)));
                        break;
                    case "save":
                        tcpIO.sendResponse(Response.createStringResponse(sqlUtils.saveCollection(app.getCol())));
                        break;
                    case "help":
                        tcpIO.sendResponse(Response.createStringResponse(app.help()));
                        break;
                    case "quit":
                    case "exit":
                        break;
                    case "undefined":
                        tcpIO.sendResponse(Response.createStringResponse("Команда не распознана. Чтобы увидеть список доступных команд введите \"help\""));
                }
            } catch (NullPointerException e) {
                new ServerIO(null, dos).sendResponse(Response.createStringResponse("Неверный формат задания объекта"));
            }
        } catch (NoSuchElementException e) {
            new ServerIO(null, dos).sendResponse(Response.createStringResponse("Выход из программы"));
        }
    }
}
