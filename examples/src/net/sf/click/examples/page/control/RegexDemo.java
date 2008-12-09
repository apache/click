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
package net.sf.click.examples.page.control;

import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.examples.page.BorderPage;
import net.sf.click.examples.page.HomePage;
import net.sf.click.extras.control.PageSubmit;
import net.sf.click.extras.control.RegexField;

/**
 * Provides a form RegexField example.
 *
 * @author Naoki Takezoe
 */
public class RegexDemo extends BorderPage {

    public Form form = new Form();

    public RegexDemo() {
        RegexField versionField = new RegexField("version", "Version", 10);
        versionField.setPattern("[0-9]+\\.[0-9]+\\.[0-9]+");
        versionField.setRequired(true);
        versionField.setTitle("Version number, e.g. '1.2.0'");
        form.add(versionField);

        RegexField urlField = new RegexField("url", "URL", 30);
        urlField.setPattern("(http|https)://.+");
        urlField.setRequired(true);
        urlField.setTitle("URL address, e.g. 'http://www.google.com'");
        form.add(urlField);

        form.add(new Submit("submit", "  OK  "));
        form.add(new PageSubmit("cancel", HomePage.class));;
    }

}
