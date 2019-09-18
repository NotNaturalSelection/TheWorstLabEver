package source;

import com.google.gson.Gson;

//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.JAXBException;
//import javax.xml.bind.Marshaller;
//import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Scanner;

public class Converter {
//    /**
//     * Преобразует строку, содержащую объект, представленный в формате XML, в объект класса Protagonist
//     *
//     * @param xmldata строка, содержащяя объект в формате XML. Подается на вход без пробелов между тегами
//     * @return объект, полученный из строки
//     */
//    public static Protagonist fromXmlToObject(String xmldata) {
//        try {
//            JAXBContext jaxbContext = JAXBContext.newInstance(Protagonist.class);
//            Unmarshaller un = jaxbContext.createUnmarshaller();
//            StringReader reader = new StringReader(xmldata);
//            return (Protagonist) un.unmarshal(reader);
//        } catch (JAXBException e) {
//            return null;
//        }
//    }
//
//    /**
//     * Преобразует объект класса Protagonist, полученный на вход в строку в формате XML
//     *
//     * @param pr объект класса Protagonist, который необходимо преобразовать
//     * @return строка, содержащяя объект представленный в формате XML
//     */
//
//    public static String fromObjectToXml(Protagonist pr) {
//        try {
//            StringWriter sw = new StringWriter();
//            JAXBContext context = JAXBContext.newInstance(Protagonist.class);
//            Marshaller marshaller = context.createMarshaller();
//            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//            marshaller.marshal(pr, sw);
//            return sw.toString();
//        } catch (JAXBException e) {
//            return null;
//        }
//    }

    /**
     * Считывает из консоли объект в формате JSON.
     * @param bPart строка, в которой могут частично располагаться команды.
     * @return возвращает объект класса Protagonist
     */
    public static Protagonist fromConsoleToObject(String bPart, Scanner scanner) {
        Gson gson = new Gson();
        Protagonist pr1;
        int braceCounter = 0;
        boolean braceCounterFlag = true;
        StringBuilder str = new StringBuilder();
        bPart = bPart.trim();
        if (bPart.contains("{")) {
            bPart = bPart.substring(bPart.indexOf('{'));
            braceCounter++;
            braceCounterFlag = false;
        } else {
            bPart = "";
        }
        if (bPart.contains("}")) {
            braceCounter--;
        }
        str.append(bPart);
        while (braceCounter > 0 || braceCounterFlag) {
            braceCounterFlag = false;
            String substr = scanner.nextLine();
            str.append(substr.replaceAll("\n", ""));
            if (substr.contains("{")) {
                braceCounter++;
            }
            if (substr.contains("}")) {
                braceCounter--;
            }
        }
        String str1 = str.toString().replaceAll(" ", "");
        try {
            pr1 = gson.fromJson(str1, Protagonist.class);
        } catch (Exception e) {
            return null;
        }
        return pr1;
    }
}
