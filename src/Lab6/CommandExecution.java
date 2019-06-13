package Lab6;

import source.*;

import java.io.DataOutputStream;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class CommandExecution extends Thread {
    private Command command;
    private DataOutputStream dos;
    private ConsoleLineApp app;

    CommandExecution(Command command, DataOutputStream dos, ConsoleLineApp app) {
        this.command = command;
        this.dos = dos;
        this.app = app;
        this.start();
    }

    @Override
    public void run() {
        execution(command);
        this.interrupt();
    }

    void execution(Command command) {
        try {
            try {
                ServerIO tcpIO = new ServerIO(null, dos);
                CollectionIO colIO = new CollectionIO(command, app.getCol());
                String cmd;
                try {
                    cmd = ConsoleLineApp.commandIdentification(command.getStringCommand());
                } catch (NullPointerException e) {
                    tcpIO.sendResponse(Response.createStringResponse("Команда не распознана"));
                    cmd = "";
                    this.interrupt();
                }
                cmd = cmd.trim();
                switch (cmd) {
                    case "import":
                        tcpIO.sendResponse(Response.createStringResponse(app.importFile(command)));//todo допилить import
                        break;
                    case "":
                        tcpIO.sendResponse(Response.createStringResponse(""));
                        break;
                    case "show":
                        tcpIO.sendResponse(Response.createFullResponse("collection", app.getCol()));
                        break;
                    case "add":
                        tcpIO.sendResponse(Response.createStringResponse(app.add(command.getObject())));
                        break;
                    case "add_if_min":
                        tcpIO.sendResponse(Response.createStringResponse(app.addIfMin(command.getObject())));
                        break;
                    case "remove":
                        tcpIO.sendResponse(Response.createStringResponse(app.remove(command.getObject())));
                        break;
                    case "info":
                        tcpIO.sendResponse(Response.createStringResponse(app.info(app)));
                        break;
                    case "load":
                        tcpIO.sendResponse(Response.createStringResponse(colIO.loadCollection()));
                        break;
                    case "save":
                        tcpIO.sendResponse(Response.createStringResponse(colIO.saveCollection()));
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
