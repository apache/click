package net.sf.click.extras.control;

import net.sf.click.control.Select;
import net.sf.click.control.Option;

import java.util.*;

import java.text.Collator;

import org.apache.commons.lang.StringUtils;

/**
 * Country Selection Control that is fully i18n aware. The country list is not
 * hardcoded but genereated by the JDK, i.e. there's an <code>Option</code>for each country that has
 * a <i>supported</i> <code>Locale</code> from the side of the JDK.<p/>
 *
 * <i>Note:</i> Newer versions of the JDK seems to support more <code>Locale</code>s.<p/>
 * <i>Obs.:</i> Especially practial for registration forms.
 *
 * @author Ahmed Mohombe
 */
public class CountrySelect extends Select {
    private Locale locale;

    /**
     * Create a CountrySelect field with no name defined.
     * <p/>
     * <b>Please note</b> the control's name must be defined before it is valid.
     */
    public CountrySelect() {
        super();
    }

    /**
     * Create a CountrySelect field with the given name.
     *
     * @param name the name of the field
     */
    public CountrySelect(String name) {
        super(name);
        this.locale = getContext().getLocale();
        buildCountryList();
    }

    /**
     * Create a CountrySelect field with the given name and label.
     *
     * @param name the name of the field
     * @param label the label of the field
     */    
    public CountrySelect(String name, String label) {
        super(name, label);
        this.locale = getContext().getLocale();
        buildCountryList();
    }

    /**
     * Create a CountrySelect field with the given name, label and locale.
     *
     * @param name the name of the field
     * @param label the label of the field
     * @param locale the Locale of the filed
     */
    public CountrySelect(String name, String label, Locale locale) {
        super(name, label);
        this.locale = locale;
        buildCountryList();
    }

    /**
     * @see net.sf.click.Context#getLocale()
     * 
     * @return the locale of this control.
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Set the locale of this control to something else than the <code>Context</code> locale. 
     * @see net.sf.click.Context#getLocale() 
     * @param locale the locale to set for this control
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Build a List of Options for all the available countries. Uses
     * the two letter uppercase ISO name of the country as the value and the
     * localized country name as the label.
     */
    public void buildCountryList() {
        final Locale[] available = Locale.getAvailableLocales();
//        String[] countriesIso = Locale.getISOCountries();
//        System.out.println("*------> available Locales total: = " + available.length);
//        System.out.println("*------> available ISO countries: = " + countriesIso.length);
//        System.out.println("*------> countriesIso = " + Arrays.asList(countriesIso));
        Map countries = new HashMap();
        
        for (int i = 0; i < available.length; i++) {
            final String iso = available[i].getCountry();
            final String name = available[i].getDisplayCountry(locale);
            if (StringUtils.isNotEmpty(iso) && StringUtils.isNotEmpty(name)) {
                countries.put(iso, name);
//                System.out.println("Found = iso[" + iso+"],name["+name+"]");
            }
        }
        add(Option.EMPTY_OPTION);
        addAll(countries);
        Collections.sort(getOptionList(), new OptionLabelComparator(locale));
    }


    /**
     * Class to compare Options using their labels with
     * locale-sensitive behaviour. <p/>
     * TODO: move this to net.sf.click.control.Select. 
     */
    public class OptionLabelComparator implements Comparator {
        private Comparator c;

        /**
         * Creates a new OptionLabelComparator object.
         *
         * @param locale The Locale used for localized String comparison.
         */
        public OptionLabelComparator(Locale locale) {
            c = Collator.getInstance(locale);
        }

        /**
         * Compares the localized labels of two Options.
         *
         * @param o1 The first Option to compare.
         * @param o2 The second Option to compare.
         * @return The value returned by comparing the localized labels.
         */
        public final int compare(Object o1, Object o2) {
            Option lhs = (Option) o1;
            Option rhs = (Option) o2;

            return c.compare(lhs.getLabel(), rhs.getLabel());
        }
    }
}
