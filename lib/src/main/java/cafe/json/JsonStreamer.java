package cafe.json;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

@SuppressWarnings("unused")
public interface JsonStreamer {
    /**
     * @return the object that is serialized and ready for writing into JSON
     */
    JSONObject serialize();

    /**
     * @param jsonObject the object for this streamer to get all of its data from
     */
    void deserialize(JSONObject jsonObject);

    /** Writes the JSON from {@link JsonStreamer#serialize()}
     * This includes JsonSaved fields.
     * @param file the file to write to
     * @see JsonSave
     */
    default void write(File file) {
        try {
            JSONObject jsonObject = addSavableFields(serialize());

            FileWriter writer = new FileWriter(file);
            writer.write(new PrettyPrinter(jsonObject).toJSONString());
            writer.close();

        } catch (IllegalAccessException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param path the path to write to
     */
    default void write(String path) {
        write(new File(path));
    }

    private JSONObject addSavableFields(JSONObject base) throws IllegalAccessException {
        Field[] fields = getSavableFields();

        for (Field field : fields) {
            base.put(field.getName(), field.get(this));
        }

        return base;
    }

    private Field[] getSavableFields() {
        Field[] fields = getClass().getDeclaredFields();

        ArrayList<Field> results = new ArrayList<>();

        for (Field field : fields) {
            field.setAccessible(true);

            if (field.getAnnotation(JsonSave.class) != null) {
                results.add(field);
            }
        }

        return results.toArray(new Field[0]);
    }

    private void readSavableFields(JSONObject jsonObject) throws IllegalAccessException {
        Field[] fields = getSavableFields();

        for (Field field : fields) {
            JsonSave annotation = field.getAnnotation(JsonSave.class);

            Object object = jsonObject.get(field.getName());

            field.set(this, annotation.value().parse(object));
        }
    }

    /** Passes the JSON into the {@link JsonStreamer#deserialize(JSONObject)} method
     * @param file the file to read from
     */
    default void read(File file) {
        JSONParser parser = new JSONParser();

        try {
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(file));

            readSavableFields(jsonObject);

            deserialize(jsonObject);

        } catch (IOException | ParseException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param path the path to read from
     */
    default void read(String path) {
        read(new File(path));
    }
}

class PrettyPrinter {
    private final JSONObject object;

    private final StringBuilder builder;

    private final int tabLevel;

    public PrettyPrinter(JSONObject object) {
        this.object = object;
        this.builder = new StringBuilder();
        this.tabLevel = 0;
    }

    public PrettyPrinter(JSONObject object, int tabLevel) {
        this.object = object;
        this.builder = new StringBuilder();
        this.tabLevel = tabLevel;
    }

    public String toJSONString() {
        builder.append('{').append('\n');

        object.forEach(this::addElement);

        cleanupBuilder();

        builder.append("\t".repeat(Math.max(0, tabLevel)));

        builder.append('}');

        return builder.toString();
    }

    private void cleanupBuilder() {
        int index = builder.lastIndexOf(",\n");

        builder.replace(index, index + 1, "");
    }

    private void addElement(Object key, Object value) {
        builder.append("\t".repeat(Math.max(0, tabLevel + 1)));

        builder.append('\"');
        if (key == null) {
            builder.append("null");
        } else {
            builder.append(JSONValue.escape(String.valueOf(key)));
        }

        builder.append('\"').append(':').append(' ');

        if (value instanceof JSONObject jsonObject) {
            builder.append(new PrettyPrinter(jsonObject, this.tabLevel + 1).toJSONString());
        } else {
            builder.append(JSONValue.toJSONString(value));
        }

        builder.append(',').append('\n');
    }
}