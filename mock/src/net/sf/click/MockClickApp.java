/*
 * Copyright 2004-2008 Malcolm A. Edgar
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
package net.sf.click;

import net.sf.click.servlet.MockServletContext;
import java.util.Properties;
import javax.servlet.ServletContext;
import net.sf.click.util.ClickLogger;
import org.w3c.dom.Element;

/**
 * Mock implementation of {@link net.sf.click.ClickApp}.
 *
 * @author Bob Schellink
 */
class MockClickApp extends ClickApp {

    public void init(ClickLogger clickLogger) throws Exception {
        //TODO check if click.xml can be found in WEB-INF or classpath.
        try {
            super.init(clickLogger);
        } catch (Exception exception) {
        }
    }

    Properties getVelocityProperties(ServletContext context) throws Exception {

        Properties properties = super.getVelocityProperties(context);
        MockServletContext servletContext = (MockServletContext)
          getServletContext();
        if (servletContext.getWebappRoot() == null) {

            //If there is no webapp root defined, do not check for velocity
            //library
            properties.remove("velocimacro.library");
        }

        return properties;
    }

    void deployFiles(Element rootElm) throws Exception {

        //Only deploy if webapp root is defined
        MockServletContext servletContext = (MockServletContext)
          getServletContext();
        if (servletContext.getWebappRoot() == null) {

            //If there is no webapp root defined, skip file deployment
            return;
        }
        super.deployFiles(rootElm);
    }
}
