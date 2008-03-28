/*
 * Copyright 2008 Malcolm A. Edgar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.click.util;

import java.util.Comparator;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import net.sf.click.control.Column;

/**
 * Provides a table Column comparator for sorting table rows.
 *
 * @see net.sf.click.control.Column
 * @see net.sf.click.control.Table
 *
 * @author Malcolm Edgar
 */
public class ColumnComparator implements Comparator {

    /** The sort ascending flag. */
    protected int ascendingSort;

    /** The column to sort on. */
    protected final Column column;

    /**
     * Create a new Column based row comparator.
     *
     * @param column the colum to sort on
     */
    public ColumnComparator(Column column) {
        this.column = column;
    }

    /**
     * @see Comparator#compare(Object, Object)
     *
     * @param row1 the first row to compare
     * @param row2 the second row to compare
     * @return the comparison result
     */
    public int compare(Object row1, Object row2) {

        this.ascendingSort = column.getTable().isSortedAscending() ? 1 : -1;

        Object value1 = column.getProperty(row1);
        Object value2 = column.getProperty(row2);

        if (value1 instanceof Comparable && value2 instanceof Comparable) {

            if (value1 instanceof String || value2 instanceof String) {
                return stringCompare(value1, value2)  * ascendingSort;

            } else {

                return ((Comparable) value1).compareTo(value2) * ascendingSort;
            }

        } else if (value1 instanceof Boolean
                   && value2 instanceof Boolean) {

            boolean bool1 = ((Boolean) value1).booleanValue();
            boolean bool2 = ((Boolean) value2).booleanValue();

            if (bool1 == bool2) {
                return 0;

            } else if (bool1 && !bool2) {
                return 1 * ascendingSort;

            } else {
                return -1 * ascendingSort;
            }

        } else if (value1 != null && value2 == null) {

            return +1 * ascendingSort;

        } else if (value1 == null && value2 != null) {

            return -1 * ascendingSort;

        } else {
            return 0;
        }
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Perform a comparison on the given string values.
     *
     * @param value1 the first value to compare
     * @param value2 the second value to compare
     * @return the string comparison result
     */
    protected int stringCompare(Object value1, Object value2) {
        String string1 = value1.toString().trim();
        String string2 = value2.toString().trim();

        StringTokenizer st1 = new StringTokenizer(string1);
        StringTokenizer st2 = new StringTokenizer(string2);

        String token1 = null;
        String token2 = null;

        while (st1.hasMoreTokens()) {
            token1 = st1.nextToken();

            if (st2.hasMoreTokens()) {
                token2 = st2.nextToken();

                int comp = 0;

                if (useNumericSort(token1, token2)) {
                    comp = numericCompare(token1, token2);

                } else {
                    comp = token1.toLowerCase().compareTo(token2.toLowerCase());
                }

                if (comp != 0) {
                    return comp;
                }

            } else {
                return -1;
            }
        }

        return 0;
    }

    /**
     * Return true if a numeric sort should be used.
     *
     * @param value1 the first value to test
     * @param value2 the second value to test
     * @return true if a numeric sort should be used
     */
    protected boolean useNumericSort(String value1, String value2) {
        if (isNumericValue(value1)) {
            // Take a second sample
            if (isNumericValue(value2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return true if the string value is a number.
     *
     * @param value the string value to test
     * @return true if the value is a number
     */
    protected boolean isNumericValue(String value) {
        if (StringUtils.containsOnly(value, "-.0123456789")) {
            int negIndex = value.indexOf("-");
            return (negIndex < 1);

        } else {
            return false;
        }
    }

    /**
     * Perform a numeric compare on the given string values.
     *
     * @param string1 the first string value to compare
     * @param string2 the second string value to compare
     * @return the numeric comparison result
     */
    protected int numericCompare(String string1, String string2) {
        if (string1.length() > 0 && string2.length() > 0) {
            Double double1 = Double.valueOf(string1);
            Double double2 = Double.valueOf(string2);

            return double1.compareTo(double2);

        } else if (string1.length() > 0) {
            return 1;

        } else if (string2.length() > 0) {
            return -1;

        } else {
            return 0;
        }
    }

}
