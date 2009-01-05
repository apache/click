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
package org.apache.click.examples.page.general;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.click.Page;
import org.apache.click.util.ClickUtils;

/**
 * Provides a example direct <tt>HttpServletResponse</tt> handling.
 *
 * @author Malcolm Edgar
 */
public class DirectPage extends Page {

    /**
     * Render the Java source file as "text/plain".
     *
     * @see Page#onGet()
     */
    public void onGet() {
        String filename = getClass().getName().replace('.', '/');
        filename = "/WEB-INF/classes/" + filename + ".java";

        HttpServletResponse response = getContext().getResponse();

        response.setContentType("text/plain");
        response.setHeader("Pragma", "no-cache");

        ServletContext context = getContext().getServletContext();

        InputStream inputStream = null;
        try {
            inputStream = context.getResourceAsStream(filename);

            PrintWriter writer = response.getWriter();

            BufferedReader reader =
                new BufferedReader(new InputStreamReader(inputStream));

            String line = reader.readLine();

            while (line != null) {
                writer.println(line);
                line = reader.readLine();
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();

        } finally {
            ClickUtils.close(inputStream);
        }
    }

    /**
     * Return null to specify no further rendering required.
     *
     * @see Page#getPath()
     */
    public String getPath() {
        return null;
    }

}
