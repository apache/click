package examples.control;

import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.click.control.Select;

/**
 * Provides a Titles options Select Control. Title options include:<ul>
 * <li>Mr</li>
 * <li>Mrs</li>
 * <li>Ms</li>
 * <li>Miss</li>
 * <li>Dr</li>
 * </ul>
 * <p/>
 *
 * @author Malcolm Edgar
 */
public class TitleSelect extends Select {

    private static final long serialVersionUID = -3535229783883739808L;

    static final Map OPTIONS = new LinkedHashMap();

    static {
        OPTIONS.put("", "");
        OPTIONS.put("Mr", "Mr");
        OPTIONS.put("Mrs", "Mrs");
        OPTIONS.put("Ms", "Ms");
        OPTIONS.put("Miss", "Miss");
        OPTIONS.put("Dr", "Dr");
    }

    /**
     * Create the Titles option Select control with the given field name.
     *
     * @param name the Title Select field name
     */
    public TitleSelect(String name) {
        super(name);
        addAll(OPTIONS);
    }
}
