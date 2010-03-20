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
package org.apache.click.examples.control;

import java.util.ArrayList;
import java.util.List;

import org.apache.click.control.Option;
import org.apache.click.control.OptionGroup;
import org.apache.click.control.Select;

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
 */
@SuppressWarnings("unchecked")
public class InvestmentSelect extends Select {

    private static final long serialVersionUID = 1L;

    static final List INVESTMENT_OPTIONS = new ArrayList();

    static {
        INVESTMENT_OPTIONS.add(new Option("None"));

        OptionGroup property = new OptionGroup("Property");
        property.add(new Option("Commercial Property", "Commercial"));
        property.add(new Option("Residential Property", "Residential"));
        INVESTMENT_OPTIONS.add(property);

        OptionGroup securities = new OptionGroup("Securities");
        securities.add(new Option("Bonds"));
        securities.add(new Option("Options"));
        securities.add(new Option("Stocks"));
        INVESTMENT_OPTIONS.add(securities);
    }

    /**
     * Create the Investment option Select control with the given field name
     * and required status.
     *
     * @param name the Selection option field name
     * @param required the field required status
     */
    public InvestmentSelect(String name, boolean required) {
        super(name, required);
        setOptionList(INVESTMENT_OPTIONS);
    }

    /**
     * Create the Investment option Select control with the given field name.
     *
     * @param name the Selection option field name
     */
    public InvestmentSelect(String name) {
        super(name);
        setOptionList(INVESTMENT_OPTIONS);
    }

    /**
     * Create the Investment option Select control.
     */
    public InvestmentSelect() {
        super();
        setOptionList(INVESTMENT_OPTIONS);
    }
}
