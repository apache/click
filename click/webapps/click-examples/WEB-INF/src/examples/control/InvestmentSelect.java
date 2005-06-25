package examples.control;

import java.util.ArrayList;
import java.util.List;

import net.sf.click.control.Option;
import net.sf.click.control.OptionGroup;
import net.sf.click.control.Select;

/**
 * Provides a Investment options Select Control. Investment options include:<ul>
 * <li>Commercial Property</li>
 * <li>Residential Property</li>
 * <li>Bonds</li>
 * <li>Options</li>
 * <li>Stocks</li>
 * </ul>
 * <p/>
 * The Investment options are statically loaded.
 *
 * @author Malcolm Edgar
 */
public class InvestmentSelect extends Select {

    static final List INVESTMENT_OPTIONS = new ArrayList();

    static {
        INVESTMENT_OPTIONS.add(new Option("None"));

        OptionGroup property = new OptionGroup("Property");
        property.add(new Option("Commerical Property", "Commercial"));
        property.add(new Option("Residential Property", "Residential"));
        INVESTMENT_OPTIONS.add(property);

        OptionGroup securities = new OptionGroup("Securities");
        securities.add(new Option("Bonds"));
        securities.add(new Option("Options"));
        securities.add(new Option("Stocks"));
        INVESTMENT_OPTIONS.add(securities);
    }

    /**
     * Create the Investment option Select control with the given field label.
     *
     * @param label the Selection option field label
     */
    public InvestmentSelect(String label) {
        super(label);
        setOptionList(INVESTMENT_OPTIONS);
    }
}
