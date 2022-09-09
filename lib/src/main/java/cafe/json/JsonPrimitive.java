package cafe.json;

/**
 * Used to tell the JsonStreamer reading engine how to parse this value from the JSON
 * Declared with the JsonSave annotation
 * @see JsonStreamer
 * @see JsonSave
 */
public enum JsonPrimitive {
    /**
     * The default JsonPrimitive chosen by {@link JsonSave}
     */
    OBJECT() {
        @Override
        public Object parse(Object object) {
            return object;
        }
    },
    STRING() {
        @Override
        public String parse(Object object) {
            return (String) object;
        }
    },
    INT() {
        @Override
        public Integer parse(Object object) {
            return (int) (long) object;
        }
    },
    FLOAT() {
        @Override
        public Float parse(Object object) {
            return (float) (double) object;
        }
    },
    LONG() {
        @Override
        public Long parse(Object object) {
            return (long) object;
        }
    },
    BOOLEAN() {
        @Override
        public Boolean parse(Object object) {
            return (boolean) object;
        }
    };

    /** Parses the return from an {@link org.json.simple.JSONObject#get(Object)} call
     * @param object the object to parse from
     * @param <T> the Type that this method will return
     * @return the parsed value
     */
    public abstract <T> T parse(Object object);
}
