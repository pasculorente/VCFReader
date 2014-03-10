package vcfreader;

/**
 *
 * @author Pascual Lorente Arencibia
 */
public class Filter {
    private final String id, description;

    public Filter(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
