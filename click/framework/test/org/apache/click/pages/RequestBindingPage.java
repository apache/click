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
package org.apache.click.pages;

import java.math.BigDecimal;
import org.apache.click.Page;
import org.apache.click.util.Bindable;

/**
 * Page with bindable variables for testing that request parameters are bound to
 * Page variables.
 */
public class RequestBindingPage extends Page {
    private static final long serialVersionUID = 1L;

    @Bindable public BigDecimal bigDecimal = BigDecimal.ZERO;
    public String string = "";
    @Bindable public boolean bool = false;

    public RequestBindingPage() {
    }

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public String getString() {
        return string;
    }

    public boolean getBoolean() {
        return bool;
    }
}
