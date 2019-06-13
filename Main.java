package ru.byprogminer.Lab5_Programming;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import ru.byprogminer.Lab3_Programming.LivingObject;
import ru.byprogminer.Lab3_Programming.Object;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.function.Function;

@SuppressWarnings("Duplicates")
public class Main {

    private final static String USAGE = "Usage: java -jar lab5.jar <filename>\n" +
            "  - filename\n" +
            "    Name of CSV file with LivingObject objects";

    private final static List<String> csvColumns = new ArrayList<>();

    private final Map<String, String> metadata = new HashMap<>();
    private final Set<LivingObject> livingObjects = new HashSet<>();
    private final TreeSet<LivingObject> sortedLivingObjects = new TreeSet<>();

    private String filename;

    static {
        csvColumns.add("type");
        csvColumns.add("id");
        csvColumns.add("key");
        csvColumns.add("value");
    }

    public static void main(final String[] args) {
        try {
            ClassLoader.getSystemClassLoader().loadClass("com.alibaba.fastjson.JSON");
        } catch (ClassNotFoundException e) {
            // Try to load fastjson.jar if it isn't loaded already

            try {
                URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();

                Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                method.setAccessible(true);

                File dir = new File(".");
                for (File jar: Objects.requireNonNull(dir.listFiles((file, s) -> s.contains("fastjson") && s.endsWith(".jar")))) {
                    method.invoke(classLoader, jar.toURI().toURL());
                }
            } catch (Throwable ignored) {}

            try {
                ClassLoader.getSystemClassLoader().loadClass("com.alibaba.fastjson.JSON");
            } catch (ClassNotFoundException e1) {
                System.err.println("Cannot start without fastjson.jar");
                System.exit(3);
            }
        }

        // Check is argument provided
        if (args.length < 1) {
            System.err.println("Filename is not provided");
            System.err.println(USAGE);
            System.exit(1);
        }

        final Main main = new Main();
        main.filename = args[0];

        final Console console = new Console(CommandRunner.getCommandRunner(main));

        // Try to load file
        try {
            main.loadCSV();
        } catch (final FileNotFoundException e) {
            console.printWarning("file \"" + main.filename + "\" isn't exists. It will be created");
        } catch (final Throwable e) {
            System.err.printf("Execution error: %s\n", e.getMessage());
            System.err.println(USAGE);
            System.exit(2);
        }

        // Delegate control to console
        System.out.println("Lab5_Programming. Type `help` to get help");
        console.exec();
    }

    {
        // Set initialize date to current by default
        metadata.put("Initialize date", new Date().toString());

        // Add shutdown hook for automatic file saving
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                saveCSV();
                System.out.println("Collection saved to " + filename);
            } catch (Throwable e) {
                System.err.println("An error occurred while file saving :(");
            }
        }));
    }

    /**
     * Usage: <code>add &lt;element&gt;</code><br>
     * Adds element to collection.<br>
     * Element represents in JSON and must have a 'name' field
     *
     * @param elementString element in JSON
     */
    @CommandHandler(
            usage = "add <element>",
            description = "Adds element to collection.\n" +
                    "Element represents in JSON and must have a 'name' field"
    )
    public void add(final String elementString, final Console console) {
        final LivingObject element = parseLivingObject(elementString);

        if (livingObjects.contains(element)) {
            console.printWarning("provided element is already contains in collection");
        }

        livingObjects.add(element);
        sortedLivingObjects.add(element);
    }

    /**
     * Usage: <code>remove_greater &lt;element&gt;</code><br>
     * Removes all greater than provided element elements from collection.<br>
     * Element represents in JSON and must have a 'name' field
     *
     * @param elementString element in JSON
     */
    @CommandHandler(
            usage = "remove_greater <element>",
            description = "Removes all greater than provided element elements from collection.\n" +
                    "Element represents in JSON and must have a 'name' field"
    )
    public void remove_greater(String elementString) {
        final LivingObject element = parseLivingObject(elementString);

        LivingObject greater;
        while (true) {
            greater = sortedLivingObjects.higher(element);

            if (greater == null) {
                break;
            }

            livingObjects.remove(greater);
            sortedLivingObjects.remove(greater);
        }
    }

    /**
     * Usage: <code>show</code><br>
     * Shows all elements in collection
     */
    @CommandHandler(description = "Shows all elements in collection")
    public void show() {
        for (LivingObject livingObject: livingObjects) {
            System.out.println(livingObject);
        }
    }

    /**
     * Usage: <code>ls</code><br>
     * Alias for <code>show</code>
     */
    @CommandHandler(description = "Alias for `show`")
    public void ls() {
        show();
    }

    /**
     * Usage: <code>save</code><br>
     * Saves collection to file
     */
    @CommandHandler(description = "Saves collection to file")
    public void save() {
        try {
            saveCSV();

            System.out.printf("Saved in %s\n", filename);
        } catch (Throwable e) {
            System.out.printf("Unexpected error: %s\n", e.getMessage());
        }
    }

    /**
     * Usage: <code>info</code><br>
     * Prints information about collection
     */
    @CommandHandler(description = "Prints information about collection")
    public void info() {
        System.out.printf("Elements in collection: %d\n", livingObjects.size());

        for (Map.Entry<String, String> field: metadata.entrySet()) {
            System.out.printf("%s: %s\n", field.getKey(), field.getValue());
        }
    }

    /**
     * Usage: <code>remove_lower &lt;element&gt;</code><br>
     * Removes all lower than provided element elements from collection.<br>
     * Element represents in JSON and must have a 'name' field
     *
     * @param elementString element in JSON
     */
    @CommandHandler(
            usage = "remove_lower <element>",
            description = "Removes all lower than provided element elements from collection.\n" +
                    "Element represents in JSON and must have a 'name' field"
    )
    public void remove_lower(String elementString) {
        final LivingObject element = parseLivingObject(elementString);

        LivingObject lower;
        while (true) {
            lower = sortedLivingObjects.lower(element);

            if (lower == null) {
                break;
            }

            livingObjects.remove(lower);
            sortedLivingObjects.remove(lower);
        }
    }

    /**
     * Usage: <code>remove &lt;element&gt;</code><br>
     * Removes element from collection.<br>
     * Element represents in JSON and must have a 'name' field
     *
     * @param elementString element in JSON
     */
    @CommandHandler(
            usage = "remove <element>",
            description = "Removes element from collection.\n" +
                    "Element represents in JSON and must have a 'name' field"
    )
    public void remove(String elementString) {
        final LivingObject element = parseLivingObject(elementString);

        livingObjects.remove(element);
        sortedLivingObjects.remove(element);
    }

    /**
     * Usage: <code>help</code><br>
     * Shows available commands
     *
     * @param console console object
     */
    @CommandHandler(usage = "help [command]", description = "Shows available commands or description of provided command")
    public void help(final Console console) {
        console.printHelp(new String[0]);
    }

    /**
     * Usage: <code>help &lt;command&gt;</code><br>
     * Shows description of provided command
     *
     * @param command command
     * @param console console object
     */
    @CommandHandler
    public void help(final String command, final Console console) {
        console.printHelp(new String[] { command });
    }

    /**
     * Usage: <code>exit</code><br>
     * Exit
     *
     * @param console console object
     */
    @CommandHandler(description = "Exit")
    public void exit(final Console console) {
        console.quit();
    }

    private void loadCSV() throws FileNotFoundException {
        final File file = new File(Objects.requireNonNull(filename));

        if (!file.exists()) {
            throw new FileNotFoundException("file " + filename + " isn't exists");
        }

        final CSVParser parser = new CSVParser();

        final Scanner scanner = new Scanner(new FileInputStream(file));
        while (scanner.hasNextLine()) {
            parser.parse(scanner.nextLine());
        }

        final Map<String, String> columns = new HashMap<>();
        for (final String col: parser.getColumns()) {
            if (csvColumns.contains(col.toLowerCase())) {
                columns.put(col.toLowerCase(), col);
            }
        }

        if (!columns.keySet().containsAll(csvColumns)) {
            throw new IllegalArgumentException("bad CSV file (bad column titles)");
        }

        final Map<String, Map<Integer, Map<String, String>>> entries = new HashMap<>();
        for (final Map<String, String> row: parser.getRows()) {
            final Map<Integer, Map<String, String>> typeMap = entries.computeIfAbsent(row.get(columns.get("type")).toLowerCase(), k -> new HashMap<>());

            try {
                final Map<String, String> idMap = typeMap.computeIfAbsent(Integer.parseInt(row.get(columns.get("id"))), k -> new HashMap<>());

                final Function<String, String> function = k -> row.get(columns.get("value"));
                if (row.get(columns.get("type")).toLowerCase().equals("meta")) {
                    idMap.computeIfAbsent(row.get(columns.get("key")), function);
                } else {
                    idMap.computeIfAbsent(row.get(columns.get("key")).toLowerCase(), function);
                }
            } catch (final NumberFormatException e) {
                throw new IllegalArgumentException("bad CSV file (not numeric id \"" + row.get(columns.get("id")) + "\")");
            }
        }

        final Map<String, String> meta = new HashMap<>();
        final Map<Integer, Map<String, String>> metaEntries = entries.get("meta");
        if (metaEntries != null) {
            for (final Map<String, String> values: metaEntries.values()) {
                for (final Map.Entry<String, String> value: values.entrySet()) {
                    if (meta.containsKey(value.getKey())) {
                        throw new IllegalArgumentException("bad CSV file (duplicated metadata)");
                    }

                    meta.put(value.getKey(), value.getValue());
                }
            }
        }

        // Recording metadata
        metadata.putAll(meta);

        // (Re)set collection type in metadata
        metadata.put("Collection type", "HashSet");

        final SortedMap<Integer, LivingObject> livingObjects = new TreeMap<>();
        final Map<Integer, Map<String, String>> objectEntries = entries.get("object");
        if (objectEntries != null) {
            for (final Map.Entry<Integer, Map<String, String>> objectEntriesEntry: objectEntries.entrySet()) {
                final Map<String, String> objectEntry = objectEntriesEntry.getValue();
                final Integer id = objectEntriesEntry.getKey();

                if (!objectEntry.containsKey("name")) {
                    throw new IllegalArgumentException("bad CSV file (living object name isn't provided)");
                }

                final LivingObject livingObject = new LivingObject(objectEntry.get("name")) {};

                boolean lives = true;
                if (objectEntry.containsKey("lives")) {
                    lives = Boolean.parseBoolean(objectEntry.get("lives"));
                }

                setLivingObjectLives(livingObject, lives);
                livingObjects.put(id, livingObject);
            }
        }

        final Map<Integer, Map<String, String>> itemEntries = entries.get("item");
        if (itemEntries != null) {
            for (final Map.Entry<Integer, Map<String, String>> itemEntriesEntry: itemEntries.entrySet()) {
                final Map<String, String> itemEntry = itemEntriesEntry.getValue();
                final Integer objectId = itemEntriesEntry.getKey();

                if (!itemEntry.containsKey("name")) {
                    throw new IllegalArgumentException("bad CSV file (item name isn't provided)");
                }

                final Object itemObject = new Object(itemEntry.get("name")) {};

                try {
                    livingObjects.get(objectId).getItems().add(itemObject);
                } catch (NullPointerException e) {
                    throw new IllegalArgumentException("bad CSV file (item of not existing object");
                }
            }
        }

        // Recording living objects
        for (final LivingObject livingObject: livingObjects.values()) {
            this.livingObjects.add(livingObject);
            sortedLivingObjects.add(livingObject);
        }
    }

    private void saveCSV() throws IOException {
        final CSVWriter writer = new CSVWriter();

        writer.setColumns(new ArrayList<>(csvColumns));

        metadata.forEach((key, value) -> {
            final Map<String, String> row = new HashMap<>();
            row.put("type", "meta");
            row.put("id", "0");
            row.put("key", key);
            row.put("value", value);

            writer.getRows().add(row);
        });

        int id = 0;
        for (final LivingObject livingObject: livingObjects) {
            final Map<String, String> objectRow = new HashMap<>();
            objectRow.put("type", "object");
            objectRow.put("id", Integer.toString(id));
            objectRow.put("key", "name");
            objectRow.put("value", livingObject.getName());
            writer.getRows().add(objectRow);

            final Map<String, String> livesRow = new HashMap<>();
            livesRow.put("type", "object");
            livesRow.put("id", Integer.toString(id));
            livesRow.put("key", "lives");
            livesRow.put("value", Boolean.toString(livingObject.isLives()));
            writer.getRows().add(livesRow);

            for (final Object item: livingObject.getItems()) {
                final Map<String, String> row = new HashMap<>();

                row.put("type", "item");
                row.put("id", Integer.toString(id));
                row.put("key", "name");
                row.put("value", item.getName());

                writer.getRows().add(row);
            }

            ++id;
        }

        writer.writeTo(new FileWriter(filename));
    }

    private LivingObject parseLivingObject(final String elementString) {
        try {
            final java.lang.Object elementJsonObject = JSON.parse(elementString);

            if (!(elementJsonObject instanceof JSONObject)) {
                throw new IllegalArgumentException("element should be an object");
            }

            final JSONObject elementJson = (JSONObject) elementJsonObject;

            final java.lang.Object name = elementJson.get("name");
            if (!(name instanceof String)) {
                throw new IllegalArgumentException("name is not provided or it isn't a string");
            }

            final LivingObject element = new LivingObject((String) name) {};

            final java.lang.Object lives = elementJson.get("lives");
            if (lives instanceof Boolean) {
                setLivingObjectLives(element, (Boolean) lives);
            }

            final java.lang.Object items = elementJson.get("items");
            if (items instanceof JSONArray) {
                for (final java.lang.Object object: (JSONArray) items) {
                    if (object instanceof JSONObject) {
                        final java.lang.Object itemName = ((JSONObject) object).get("name");

                        if (!(itemName instanceof String)) {
                            throw new IllegalArgumentException("item name is not provided or it isn't a string");
                        }

                        element.getItems().add(new Object((String) itemName) {});
                    }
                }
            }

            return element;
        } catch (Throwable e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void setLivingObjectLives(final LivingObject object, final Boolean lives) {
        try {
            final Field livesField = LivingObject.class.getDeclaredField("lives");
            livesField.setAccessible(true);
            livesField.set(object, lives);
        } catch (final NoSuchFieldException | IllegalAccessException ignored) {}
    }
}
