package examples.control;

import java.util.ArrayList;
import java.util.List;

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
        Select.Option none = new Select.Option("None");
        INVESTMENT_OPTIONS.add(none);
        
        Select.OptionGroup property = new Select.OptionGroup("Property");
        property.add(new Select.Option("Commerical Property", "Commercial"));
        property.add(new Select.Option("Residential Property", "Residential"));    
        INVESTMENT_OPTIONS.add(property);
        
        Select.OptionGroup securities = new Select.OptionGroup("Securities");
        securities.add(new Select.Option("Bonds"));
        securities.add(new Select.Option("Options"));
        securities.add(new Select.Option("Stocks"));
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
