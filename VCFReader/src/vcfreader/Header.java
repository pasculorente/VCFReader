package vcfreader;

import java.util.AbstractMap;

/**
 *
 * @author Naira Melian
 */
public class Header extends AbstractMap.SimpleEntry<String, String> {

    public Header(String key, String value) {
        super(key, value);
    }
}
