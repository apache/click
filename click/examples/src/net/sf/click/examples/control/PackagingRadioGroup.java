package net.sf.click.examples.control;

import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.click.control.RadioGroup;

/**
 * Provides a Packaging options RadioGroup Control. Packaging options include:<ul>
 * <li>Standard</li>
 * <li>Protective</li>
 * <li>Gift Wrap</li>
 * </ul>
 * <p/>
 *
 * @author Malcolm Edgar
 */
public class PackagingRadioGroup extends RadioGroup {

    private static final long serialVersionUID = -3535229783883739808L;

    static final Map OPTIONS = new LinkedHashMap();

    static {
        OPTIONS.put("STD", "Standard ");
        OPTIONS.put("PRO", "Protective ");
        OPTIONS.put("GFT", "Gift Wrap ");
    }

    /**
     * Create the Packaging options RadioGroup control with the given field name.
     *
     * @param name the Packaging RadioGroup field name
     */
    public PackagingRadioGroup(String name) {
        super(name);
        addAll(OPTIONS);
    }
}
