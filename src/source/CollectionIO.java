package source;

import java.io.*;
import java.util.Collection;

public class CollectionIO {
    private Command command;
    private Collection<Protagonist> col;

    public CollectionIO(Command command, Collection<Protagonist> col) {
        this.col = col;
        this.command = command;
    }

    /**
     * Загружает данные из файла, считываемого программой из переменной окружения в коллекцию, которую получает на вход
     */
    String loadCollection() {
        try {
            col.clear();
            String address;
            StringBuilder sb;
            if(command.getStringCommand().substring(0,6).equals("import")){
                sb = new StringBuilder(command.getFileContent());
            } else {
                try {
                    address = command.getStringCommand().split(" ")[1];
                    FileInputStream fis = new FileInputStream(address);
                    byte[] buffer = new byte[fis.available()];
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    if (!(bis.read(buffer, 0, fis.available()) > 0)) {
                        return("Файл пуст");
                    }
                    sb = new StringBuilder();
                    for (byte b : buffer) {
                        sb.append((char) b);

                    }
                } catch (ArrayIndexOutOfBoundsException e){
                    return "Некорректное задание имени файла";
                }
            }
            String[] ex = sb.toString().split("\n");
            ex[0] = "";
            ex[1] = "";
            sb = new StringBuilder();
            for (String i : ex) {
                sb.append(i);
            }
            String[] expressions = sb.toString().split("</protagonist>");
            for (int i = 0; i + 1 < expressions.length; i++) {
                expressions[i] = expressions[i].replaceAll(" ", "");
                expressions[i] = expressions[i].replaceAll("</collection>", "");
                expressions[i] += "</protagonist>";
                col.add(Converter.fromXmlToObject(expressions[i]));
            }
            if(!col.isEmpty()){
                return("Коллекция была загружена успешно");
            } else {
                return "Коллекция пуста";
            }
        } catch (IOException e) {
            return("При загрузке коллекции произошла ошибка. Проверьте формат файла, его наличие " +
                    "в нужной директории и наличие у пользователя прав на чтение данного файла");
        } catch (ArrayIndexOutOfBoundsException e) {
            return("При загрузке коллекции произошла ошибка. Проверьте целостность файла");
        }
    }

    /**
     * Сохраняет поданную на вход коллекцию в файл, определенный переменной окружения, в формате ХML
     */

    public String saveCollection() {
        try {
            String address;
            try {
                address = command.getStringCommand().split(" ")[1];
            } catch (ArrayIndexOutOfBoundsException e){
                return "Некорректное задание имени файла";
            }
            FileWriter fw = new FileWriter(address);
            fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
            fw.write("<collection>\n");
            StringBuilder t = new StringBuilder();
            StringBuilder s = new StringBuilder();
            for (Protagonist i : col) {
//                s.append(Converter.fromObjectToXml(i)); РАСКОММЕНТИТЬ ЕСЛИ ПОНАДОБИТСЯ

                String[] arr = s.toString().split("\n");
                s = new StringBuilder();
                for (int j = 1; j < arr.length; j++) {
                    t.append(arr[j]);
                    t.append("\n");
                }
            }
            fw.write(t.toString());
            fw.write("</collection>");
            fw.flush();
            if(new File(address).exists()){
                return("Коллекция была успешно сохранена в файл");
            } else {
                return ("Коллекция не была сохранена в файл");
            }
        } catch (IOException e) {
            return("При сохранении коллекции произошла ошибка. Проверьте формат файла, его " +
                    "наличие в нужной директории и наличие у пользователя прав на запись данных в этот файл");
        }
    }
}
