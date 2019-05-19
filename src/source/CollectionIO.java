package source;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;

class CollectionIO {
    private String path;

    CollectionIO(String path) {
        this.path = path;
    }

    /**
     * Загружает данные из файла, считываемого программой из переменной окружения в коллекцию, которую получает на вход
     *
     * @param col коллекция, в которую будут записаны считанные из файла объекты
     */
    boolean loadCollection(LinkedHashSet<Protagonist> col) {
        try {
            col.clear();
            FileInputStream fis = new FileInputStream(path);
            String text = "";
            byte[] buffer = new byte[fis.available()];
            BufferedInputStream bis = new BufferedInputStream(fis);
            if (!(bis.read(buffer, 0, fis.available()) > 0)) {
                System.out.println("Коллекция пуста");
            }
            StringBuilder sb = new StringBuilder(text);
            for (byte b : buffer) {
                sb.append((char) b);

            }
            String[] ex = sb.toString().split("\n");
            ex[0] = "";
            ex[1] = "";
            text = "";
            sb = new StringBuilder(text);
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
            System.out.println("Коллекция была загружена успешно");
            return true;
        } catch (IOException e) {
            System.err.println("При загрузке коллекции произошла ошибка. Проверьте формат файла, его наличие " +
                    "в нужной директории и наличие у пользователя прав на чтение данного файла");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("При загрузке коллекции произошла ошибка. Проверьте целостность файла");
        }
        return false;
    }

    /**
     * Сохраняет поданную на вход коллекцию в файл, определенный переменной окружения, в формате ХML
     *
     * @param col коллекция, которая будет сохранена в файл
     */

    void saveCollection(LinkedHashSet<Protagonist> col) {
        try {
            FileWriter fw = new FileWriter(path);
            fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
            fw.write("<collection>\n");
            StringBuilder t = new StringBuilder();
            StringBuilder s = new StringBuilder();
            for (Protagonist i : col) {
                s.append(Converter.fromObjectToXml(i));
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
            System.out.println("Коллекция была успешно сохранена в файл " + path);
        } catch (IOException e) {
            System.err.println("При сохранении коллекции произошла ошибка. Проверьте формат файла, его " +
                    "наличие в нужной директории и наличие у пользователя прав на запись данных в этот файл");
        }
    }
}
