package source;

import com.google.gson.JsonSyntaxException;

import java.io.File;
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
    private boolean saveFlag = false;
    private static final Scanner sc = new Scanner(System.in);
    private static final String[] commands = {
            "show", "info", "load",
            "save", "quit", "exit",
            "add", "add_if_min",
            "remove", "help", "filepath"};
    private String path;
    private Date date;
    private LinkedHashSet<Protagonist> col = new LinkedHashSet<>();
    public static void main(String[] args) {
        String subPath = "";
        boolean bool = true;
        if (System.getenv("DATAXML") != null && new File(System.getenv("DATAXML")).exists()) {
            subPath = System.getenv("DATAXML");
        } else {
            try {
                String sub;
                System.out.println("Файл в пути по умолчанию не найден. Введите путь самостоятельно" +
                        "или введите команду выхода \"/quit\"");
                do {
                    sub = sc.nextLine();
                    if (!new File(sub).exists() && !sub.equals("/quit")) {
                        System.out.println("Файл не существует. Проверьте правильность введенных данных или введите " +
                                "\"/quit\", чтобы выйти из программы");
                    } else if (!new File(sub).canRead() && new File(sub).exists()) {
                        System.out.println("Отказано в доступе при попытке прочтения файла. " +
                                "Проверьте правильность введенных данных или введите " +
                                "\"/quit\", чтобы выйти из программы");
                        bool = false;
                    } else if (!new File(sub).canWrite() && new File(sub).exists()) {
                        System.out.println("У вас нет доступа к изменению этого файла. " +
                                "Проверьте правильность введенных данных или введите " +
                                "\"/quit\", чтобы выйти из программы");
                        bool = false;
                    }
                    if (sub.equals("/quit")) {
                        System.exit(0);
                    }
                } while (!new File(sub).exists());
                subPath = sub;
            } catch (NoSuchElementException e) {
                System.out.println("Выход из программы");
                System.exit(0);
            }
        }
        ConsoleLineApp app = new ConsoleLineApp(subPath, new Date());
        app.saveFlag = bool;
        CollectionIO colIO = new CollectionIO(subPath);
        app. saveFlag = colIO.loadCollection(app.col);
        System.out.println("Вас приветствует приложение для работы с коллекцией" +
                ". Чтобы узнать список доступных команд введите \"help\"");
        String str;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if(app.saveFlag) {
                    colIO.saveCollection(app.col);
                }
            } catch (Throwable e) {
                System.err.println("Произошла ошибка во время сохранения файла");
            }
        }));
        try {
            while (true) {
                try {
                    str = sc.nextLine();
                    String command;
                    try {
                        command = commandIdentification(str);
                    } catch (NullPointerException e) {
                        System.out.println("Команда не распознана");
                        continue;
                    }
                    str = str.replaceAll(command, "");
                    switch (command) {
                        case "show":
                            app.show();
                            break;
                        case "add":
                            app.add(Converter.fromConsoleToObject(str));
                            break;
                        case "add_if_min":
                            app.addIfMin(Converter.fromConsoleToObject(str));
                            break;
                        case "remove":
                            app.remove(Converter.fromConsoleToObject(str));
                            break;
                        case "info":
                            app.info(app);
                            break;
                        case "load":
                            colIO.loadCollection(app.col);
                            break;
                        case "save":
                            colIO.saveCollection(app.col);
                            break;
                        case "filepath":
                            app.showPath();
                            break;
                        case "help":
                            app.help();
                            break;
                        case "quit":
                        case "exit":
                            System.exit(0);
                            break;
                        case "undefined":
                            System.out.println("Команда не распознана. Чтобы увидеть список доступных команд введите \"help\"");
                    }
                } catch (NullPointerException e) {
                    System.err.println("Неверный формат задания объекта");
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("Выход из программы");
        }

    }

    /**
     * Выводит путь файла, в котором содержится коллекция
     */
    private void showPath() {
        System.out.println(path);
    }

    /**
     * показать объекты, которые на данный момент содержатся в коллекции
     */
    private void show() {
        if (col.isEmpty()) {
            System.out.println("Коллекция пуста");
        } else {
            int i = 0;
            for (Protagonist pr : col) {
                System.out.println(++i);
                System.out.println(pr.toString() + "\n");
            }
        }
    }

    /**
     * Принимает на вход объект класса Protagonist и добавляет его в коллекцию
     *
     * @param pr объект, который будет добавлен в коллекцию
     */
    private void add(Protagonist pr) {
        try {
            if (pr != null) {
                if (col.contains(pr)) {
                    System.out.println("Коллекция уже содержит такой объект");
                } else {
                    col.add(pr);
                    if (col.contains(pr)) {
                        System.out.println("Добавление элемента в коллекцию выполнено успешно");
                    }
                }
            } else {
                System.err.println("Объект не распознан");
            }
        } catch (Exception e) {
            System.err.println("Объект не распознан");
        }
    }

    /**
     * Принимает на вход объект класса Protagonist и добавляет его в коллекцию, если тот имеет самое короткое имя
     *
     * @param pr объект, который будет добавлен в коллекцию
     */
    private void addIfMin(Protagonist pr) {
        try {
            if (pr != null) {
                if (col.contains(pr)) {
                    System.out.println("Коллекция уже содержит такой объект");
                } else {
                    if (!col.isEmpty()) {
                        if (Collections.min(col).getName() != null && pr.getName() != null) {
                            if (Collections.min(col).getName().length() > pr.getName().length()) {
                                col.add(pr);
                            }
                            if (col.contains(pr)) {
                                System.out.println("Добавление элемента в коллекцию выполнено успешно");
                            } else {
                                System.out.println("Объект не был добавлен в коллекцию, так как не являлся минимальным");
                            }
                        } else {
                            col.add(pr);
                            if (col.contains(pr)) {
                                System.out.println("Добавление элемента в коллекцию выполнено успешно");
                            } else {
                                System.out.println("Объект не был добавлен в коллекцию, так как не являлся минимальным");
                            }
                        }
                    } else {
                        col.add(pr);
                        if (col.contains(pr)) {
                            System.out.println("Добавление элемента в коллекцию выполнено успешно");
                        }
                    }
                }
            } else {
                System.err.println("Объект не распознан");
            }
        } catch (NumberFormatException | JsonSyntaxException e) {
            System.out.println("Объект не распознан");
        }
    }

    /**
     * Удаляет элемент из коллекции, если такой существует
     *
     * @param pr объект, отсутствие которого будет обеспечено в коллекции
     */
    private void remove(Protagonist pr) {
        try {
            if (pr != null) {
                if (!col.contains(pr)) {
                    System.out.println("Коллекция не содержит такой объект");
                } else {
                    col.remove(pr);
                    if (!col.contains(pr)) {
                        System.out.println("Удаление элемента из коллекции выполнено успешно");
                    }
                }
            } else {
                System.err.println("Объект не распознан");
            }
        } catch (JsonSyntaxException | NumberFormatException e) {
            System.out.println("Объект не распознан");
        }
    }

    /**
     * Выводит информацию о коллекции
     */
    private void info(ConsoleLineApp app) {
        System.out.println("Тип коллекции - LinkedHashSet;\nКоличество элементов = " + col.size() + ";\n" +
                "Дата инициализации: " + app.date);
        System.out.print("Путь к файлу коллекции:");
        showPath();
    }

    /**
     * Выводит сообщение о доступных функциях программы
     */
    private void help() {
        System.out.println("Список доступных команд:\nshow - вывести содержимое " +
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

    public ConsoleLineApp(String path, Date date) {
        this.path = path;
        this.date = date;
    }

    private static String commandIdentification(String command) {
        if (command.equals("")) {
            return command;
        }
        if (!Character.isAlphabetic((int) command.charAt(0))) {
            return "undefined";
        }
        command = command.replaceAll(" ", "");
        if (command.indexOf("{") > command.indexOf("}") && command.contains("{") && command.contains("}")) {
            throw new NullPointerException();
        }
        if (command.contains("{")) {
            command = command.substring(0, command.indexOf("{"));
        }
        for (String c : commands) {
            if (command.equals(c)) {
                return c;

            }
        }
        return "undefined";
    }

    static Scanner getSc(){
        return sc;
    }
}