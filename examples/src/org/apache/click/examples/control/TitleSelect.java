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

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.click.control.Select;

/**
 * Provides a Titles options Select Control. Title options include:<ul>
 * <li>Mr</li>
 * <li>Mrs</li>
 * <li>Ms</li>
 * <li>Miss</li>
 * <li>Dr</li>
 * </ul>
 * <p/>
 */
public class TitleSelect extends Select {

    private static final long serialVersionUID = 1L;

    static final Map<String, String> OPTIONS = new LinkedHashMap<String, String>();

    static {
        OPTIONS.put("", "");
        OPTIONS.put("Mr", "Mr");
        OPTIONS.put("Mrs", "Mrs");
        OPTIONS.put("Ms", "Ms");
        OPTIONS.put("Miss", "Miss");
        OPTIONS.put("Dr", "Dr");
    }

    /**
     * Create the Titles option Select control with the given field name.
     *
     * @param name the Title Select field name
     */
    public TitleSelect(String name) {
        super(name);
        addAll(OPTIONS);
    }
}
