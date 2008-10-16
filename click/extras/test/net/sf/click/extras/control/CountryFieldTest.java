package net.sf.click.extras.control;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;
import net.sf.click.MockContext;
import net.sf.click.control.Option;

/**
 * Test for CountrySelect.
 */
public class CountryFieldTest extends TestCase {

    /**
     * Check that duplicate countries are filtered out. CountrySelect looks up
     * countries using Locale.getAvailableLocales() and this array can return
     * the same country for different languages. Example are Canada and Spain.
     * 
     * CLK-458
     */
    public void testDuplicateCountries() {
        MockContext mockContext = MockContext.initContext();

        CountrySelect countrySelect = new CountrySelect("select");
        countrySelect.bindRequestValue();
        List countries = countrySelect.getOptionList();
        Iterator it = countries.iterator();
        
        Set uniqueChecker = new HashSet();
        while(it.hasNext()) {
            Option option = (Option) it.next();
            
            // Check that no country already exists in checker. If a country
            // already exists, it means that CountrySelect returns duplicate countries
            assertFalse(uniqueChecker.contains(option.getLabel()));

            uniqueChecker.add(option.getLabel());
            System.out.println(option.getValue() + " " + option.getLabel());
        }
    }
}
