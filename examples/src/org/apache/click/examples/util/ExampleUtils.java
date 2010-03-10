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
package org.apache.click.examples.util;

import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.click.Context;

/**
 * Provides examples helper methods.
 */
public class ExampleUtils {

    private static final Map<Currency, String> CURRENCY_SYMBOLS = new HashMap<Currency, String>();

    @SuppressWarnings("unchecked")
    public static Object getSessionObject(Class aClass) {
        if (aClass == null) {
            throw new IllegalArgumentException("Null class parameter.");
        }
        Object object = getContext().getSessionAttribute(aClass.getName());
        if (object == null) {
            try {
                object = aClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return object;
    }

    public static void setSessionObject(Object object) {
        if (object != null) {
            getContext().setSessionAttribute(object.getClass().getName(), object);
        }
    }

    @SuppressWarnings("unchecked")
    public static void removeSessionObject(Class aClass) {
        if (getContext().hasSession() && aClass != null) {
            getContext().getSession().removeAttribute(aClass.getName());
        }
    }

    public static String getCurrencySymbol(Currency currency) {
        if(currency == null) {
            return "";
        }

        String symbol = CURRENCY_SYMBOLS.get(currency);
        if(symbol != null) {
            return symbol;
        }

        String currencyCode = currency.getCurrencyCode();

        Locale locale = Locale.getDefault();
        symbol = currency.getSymbol(locale);
        if(!symbol.equals(currencyCode)) {
            CURRENCY_SYMBOLS.put(currency, symbol);
            return symbol;
        }

        Locale[] allLocales = Locale.getAvailableLocales();
        for (int i = 0; i < allLocales.length; i++) {
            symbol = currency.getSymbol(allLocales[i]);
            if(!symbol.equals(currencyCode)) {
                CURRENCY_SYMBOLS.put(currency, symbol);
                return symbol;
            }
        }

        CURRENCY_SYMBOLS.put(currency, currencyCode);
        return currencyCode;
    }

    private static Context getContext() {
        return Context.getThreadLocalContext();
    }

}
