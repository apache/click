/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.click.extras.control;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;
import org.apache.click.MockContext;
import org.apache.click.control.Option;

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
        MockContext.initContext();

        CountrySelect countrySelect = new CountrySelect("select");
        countrySelect.bindRequestValue();
        List<?> countries = countrySelect.getOptionList();
        Iterator<?> it = countries.iterator();
        
        Set<String> uniqueChecker = new HashSet<String>();
        while(it.hasNext()) {
            Option option = (Option) it.next();
            
            // Check that no country already exists in checker. If a country
            // already exists, it means that CountrySelect returns duplicate countries
            assertFalse(uniqueChecker.contains(option.getLabel()));

            uniqueChecker.add(option.getLabel());
        }
    }
}
