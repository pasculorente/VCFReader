package vcfreader;

import java.util.TreeMap;

/**
 *
 * @author Pascual Lorente Arencibia
 */
public class Info extends TreeMap<String, String> {

    private final String id, number, type, description;

    public Info(String id, String number, String type, String description) {
        this.id = id;
        this.number = number;
        this.type = type;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
