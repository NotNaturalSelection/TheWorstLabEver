package source;

import Lab7.DBConnection;
import Lab7.SQLUtils;
import com.google.gson.JsonSyntaxException;

import java.util.*;


/**
 * <font size=4>Данная программа представляет собой консольное приложение для работы с коллекцией объектов типа Protagonist.
 * При запуске приложения коллекция автоматичекски заполняется данными из файла в формате XML.
 * При выходе из приложения коллекция сохраняется в исходный файл, если не изменить данную установку.После введения команд
 * "add", "add_if_min", "remove" следует ввести данные об объекте в формате JSON.</font>
 * <br><h3>Список команд доступных в версии 1.0:</h3>
 * <br><b>show</b> - вывести содержимое коллекции
 * <br><b>info</b> - получить информацию о коллекции
 * <br><b>load</b> - загрузить коллекцию из файла
 * <br><b>save</b> - сохранить коллекцию в файл
 * <br><b>quit</b> - выйти из консольного приложения, автоматически сохранив коллекцию в файл
 * <br><b>add</b> - добавить элемет в коллекцию
 * <br><b>add_if_min</b> - добавить элемент, если он является минимальным для коллекции
 * <br><b>remove</b> - удалить элемент
 *
 * @author NotNaturalSelection
 * @version 1.0
 */
public class ConsoleLineApp {
    private static final String[] commands = {
            "show", "info", "load",
            "save", "quit", "exit",
            "add", "add_if_min",
            "remove", "help", "filepath", "import"};
    private Date date;
    private Set<Protagonist> col =  Collections.synchronizedSet(new LinkedHashSet<>());

    /**
     * показать объекты, которые на данный момент содержатся в коллекции
     */
    public String show() {
        if (col.isEmpty()) {
            return ("Коллекция пуста");
        } else {
            int i = 0;
            StringBuilder result = new StringBuilder();
            for (Protagonist pr : col) {
                result.append(++i).append("\n");
                result.append(pr.toString()).append("\n");
            }
            return result.toString();
        }
    }

    /**
     * Принимает на вход объект класса Protagonist и добавляет его в коллекцию
     *
     * @param pr объект, который будет добавлен в коллекцию
     */
    public String add(Protagonist pr, String login) {
        try {
            if (pr != null) {
                pr.setOwner(login);
                if (col.contains(pr)) {
                    return ("Коллекция уже содержит такой объект");
                } else {
                    col.add(pr);
                    if (col.contains(pr)) {
                        new SQLUtils(new DBConnection()).saveCollection(this.getCol());
                        return ("Добавление элемента в коллекцию выполнено успешно");
                    } else {
                        return "Неизвестная ошибка, объект не был добавлен";
                    }
                }
            } else {
                return ("Объект не распознан");
            }
        } catch (Exception e) {
            return ("Объект не распознан");
        }
    }

    /**
     * Принимает на вход объект класса Protagonist и добавляет его в коллекцию, если тот имеет самое короткое имя
     *
     * @param pr объект, который будет добавлен в коллекцию
     */
    public String addIfMin(Protagonist pr, String login) {
        try {
            if (pr != null) {
                pr.setOwner(login);
                if (col.contains(pr)) {
                    return ("Коллекция уже содержит такой объект");
                } else {
                    if (!col.isEmpty()) {
                        if (Collections.min(col).getName() != null && pr.getName() != null) {
                            if (Collections.min(col).getName().length() > pr.getName().length()) {
                                col.add(pr);
                                new SQLUtils(new DBConnection()).saveCollection(this.getCol());
                            }
                            if (col.contains(pr)) {
                                return ("Добавление элемента в коллекцию выполнено успешно");
                            } else {
                                return ("Объект не был добавлен в коллекцию, так как не являлся минимальным");
                            }
                        } else {
                            col.add(pr);
                            new SQLUtils(new DBConnection()).saveCollection(this.getCol());
                            if (col.contains(pr)) {
                                return ("Добавление элемента в коллекцию выполнено успешно");
                            } else {
                                return ("Объект не был добавлен в коллекцию, так как не являлся минимальным");
                            }
                        }
                    } else {
                        col.add(pr);
                        new SQLUtils(new DBConnection()).saveCollection(this.getCol());
                        if (col.contains(pr)) {
                            return ("Добавление элемента в коллекцию выполнено успешно");
                        } else {
                            return "Неизвестная ошибка, объект не был добавлен";
                        }
                    }
                }
            } else {
                return ("Объект не распознан");
            }
        } catch (NumberFormatException | JsonSyntaxException e) {
            return ("Объект не распознан");
        }
    }

    /**
     * Удаляет элемент из коллекции, если такой существует
     *
     * @param pr объект, отсутствие которого будет обеспечено в коллекции
     */
    public String remove(Protagonist pr, String login) {
        try {
            if (pr != null) {
                pr.setOwner(login);
                if (!col.contains(pr)) {
                    return ("Коллекция не содержит такой объект или вы не являетесь его владельцем");
                } else {
                    col.remove(pr);
                    new SQLUtils(new DBConnection()).saveCollection(this.getCol());
                    if (!col.contains(pr)) {
                        return "Удаление элемента из коллекции выполнено успешно";
                    } else {
                        return "Неизвестная ошибка, объект не был удален";
                    }
                }
            } else {
                return ("Объект не распознан");
            }
        } catch (JsonSyntaxException | NumberFormatException e) {
            return ("Объект не распознан");
        }
    }

    /**
     * Выводит информацию о коллекции
     */
    public String info(ConsoleLineApp app) {
        return ("Тип коллекции - HashSet;\nКоличество элементов = " + col.size() + ";\n" +
                "Дата инициализации: " + app.date);
    }

    /**
     * Выводит сообщение о доступных функциях программы
     */
    public String help() {
        return ("Список доступных команд:\nshow - вывести содержимое " +
                "коллекции\ninfo - получить информацию о коллекции\nload - загрузить коллекцию из файла\n" +
                "save - сохранить коллекцию в файл\nquit - выйти из консольного приложения\nadd - добавить " +
                "элемет в коллекцию\nadd_if_min - " +
                "добавить элемент, если он является минимальным для коллекции\nremove - удалить элемент.\n" +
                "После введение команд add, add_if_min, remove следует ввести объект в формате JSON:\n" +
                "{\n" +
                "  \"Name\":\"value\",\n" +
                "  \"gender\":\"value\",\n" +
                "  \"Strength\":6,\n" +
                "  \"Agility\":4,\n" +
                "  \"Intelligence\":-1,\n" +
                "  \"Luck\":4,\n" +
                "  \"wealth\":2,\n" +
                "  \"LevelOfPain\":8,\n" +
                "  \"ballcounter\":1,\n" +
                "  \"Defence\":6.5,\n" +
                "  \"location\":{\n" +
                "  \"x\":1,\n" +
                "  \"y\":2,\n" +
                "  \"z\":3\n" +
                "  }\n" +
                "}\n"
        );
    }

    public String importFile(Command command){
        return new CollectionIO(command, this.getCol()).loadCollection();
    }

    public ConsoleLineApp(Date date) {
        this.date = date;
    }

    public static String commandIdentification(String command) {
        String cmd;
        try {
            cmd = command.split(" ")[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            return "undefined";
        }
        if (command.equals("")) {
            return command;
        }
        if (!Character.isAlphabetic(cmd.charAt(0))) {
            return "undefined";
        }
        command = command.replaceAll(" ", "");
        if (command.indexOf("{") > command.indexOf("}") && command.contains("{") && command.contains("}")) {
            throw new NullPointerException();
        }
        if (command.contains("{")) {
            cmd = command.substring(0, command.indexOf("{"));
        }
        for (String c : commands) {
            if (cmd.equals(c)) {
                return c;

            }
        }
        return "undefined";
    }

    public Set<Protagonist> getCol() {
        return col;
    }

}