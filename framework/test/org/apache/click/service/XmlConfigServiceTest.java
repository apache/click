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
package org.apache.click.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;

import junit.framework.TestCase;
import org.apache.click.Context;
import org.apache.click.MockContainer;
import org.apache.click.Page;
import org.apache.click.PageInterceptor;

import org.apache.click.control.AbstractControl;
import org.apache.click.pages.BinaryPage;
import org.apache.click.pages.JspPage;
import org.apache.click.pages.ListenerPage;
import org.apache.click.service.ConfigService.AutoBinding;
import org.apache.click.util.ClickUtils;
import org.apache.click.util.ErrorPage;
import org.apache.click.util.Format;
import org.apache.click.util.MessagesMap;

/**
 * Tests for the XmlConfigService class.
 */
public class XmlConfigServiceTest extends TestCase {

    public void testDefaults() throws Exception {
        File tmpdir = makeTmpDir();

        PrintStream pstr = makeXmlStream(tmpdir, "WEB-INF/click.xml");
        pstr.println("<click-app charset='UTF-8'>");
        pstr.println(" <pages package='org.apache.click.pages'/>");
        pstr.println("</click-app>");
        pstr.close();

        MockContainer container = new MockContainer(tmpdir.getAbsolutePath());
        container.start();
        ConfigService config = ClickUtils.getConfigService(container.getServletContext());

        assertEquals(ErrorPage.class, config.getErrorPageClass());
        assertEquals(Page.class, config.getNotFoundPageClass());
        assertEquals("development", config.getApplicationMode());
        assertEquals("UTF-8", config.getCharset());
        assertEquals(false, config.isProductionMode());
        assertEquals(false, config.isProfileMode());
        assertEquals(Format.class, config.createFormat().getClass());
        assertEquals(AutoBinding.DEFAULT, config.getAutoBindingMode());
        assertEquals(null, config.getLocale());
        assertEquals(Collections.EMPTY_LIST, config.getPageClassList());
        // Check deployed resource
        assertTrue(new File(tmpdir, "click/control.css").exists());

        container.stop();

        deleteDir(tmpdir);
    }

    public void testLocale() throws Exception {
        File tmpdir = makeTmpDir();

        PrintStream pstr = makeXmlStream(tmpdir, "WEB-INF/click.xml");
        pstr.println("<click-app charset='iso8859-1' locale='en_GB'>");
        pstr.println(" <pages package='org.apache.click.pages'/>");
        //pstr.println(" <mode value='trace'/>");
        pstr.println("</click-app>");
        pstr.close();

        MockContainer container = new MockContainer(tmpdir.getAbsolutePath());
        container.start();
        ConfigService config = ClickUtils.getConfigService(container.getServletContext());

        assertEquals(Locale.UK, config.getLocale());
        assertEquals("iso8859-1", config.getCharset());

        container.stop();

        deleteDir(tmpdir);
    }

    public void testExcludes() throws Exception {
        File tmpdir = makeTmpDir();

        PrintStream pstr = makeXmlStream(tmpdir, "WEB-INF/click.xml");
        pstr.println("<click-app charset='iso8859-1' locale='en_GB'>");
        pstr.println(" <pages package='org.apache.click.pages'>");
        pstr.println("  <excludes pattern='BinaryPage.htm'/>");
        pstr.println(" </pages>");
        pstr.println("</click-app>");
        pstr.close();

        PrintStream f = new PrintStream(makeFile(tmpdir, "BinaryPage.htm"));
        f.print("template");
        f.close();

        MockContainer container = new MockContainer(tmpdir.getAbsolutePath());
        container.start();
        ConfigService config = ClickUtils.getConfigService(container.getServletContext());

        Class<? extends Page> pageClass = config.getPageClass("/BinaryPage.htm");
        assertFalse(BinaryPage.class.isAssignableFrom(pageClass));

        container.testPage("/BinaryPage.htm");
        assertEquals("template", container.getResponse().getDocument());

        container.stop();

        deleteDir(tmpdir);
    }

    public void testProduction() throws Exception {
        File tmpdir = makeTmpDir();

        PrintStream pstr = makeXmlStream(tmpdir, "WEB-INF/click.xml");
        pstr.println("<click-app>");
        pstr.println(" <pages package='org.apache.click.pages'/>");
        pstr.println(" <mode value='production'/>");
        pstr.println("</click-app>");
        pstr.close();

        MockContainer container = new MockContainer(tmpdir.getAbsolutePath());
        container.start();
        ConfigService config = ClickUtils.getConfigService(container.getServletContext());

        assertEquals(true, config.isProductionMode());
        assertEquals(false, config.isProfileMode());

        container.stop();

        deleteDir(tmpdir);
    }

    public void testProfile() throws Exception {
        File tmpdir = makeTmpDir();

        PrintStream pstr = makeXmlStream(tmpdir, "WEB-INF/click.xml");
        pstr.println("<click-app>");
        pstr.println(" <pages package='org.apache.click.pages'/>");
        pstr.println(" <mode value='profile'/>");
        pstr.println("</click-app>");
        pstr.close();

        MockContainer container = new MockContainer(tmpdir.getAbsolutePath());
        container.start();
        ConfigService config = ClickUtils.getConfigService(container.getServletContext());

        assertEquals(false, config.isProductionMode());
        assertEquals(true, config.isProfileMode());

        container.stop();

        deleteDir(tmpdir);
    }

    public void testDebug() throws Exception {
        File tmpdir = makeTmpDir();

        PrintStream pstr = makeXmlStream(tmpdir, "WEB-INF/click.xml");
        pstr.println("<click-app>");
        pstr.println(" <pages package='org.apache.click.pages'/>");
        pstr.println(" <mode value='debug'/>");
        pstr.println("</click-app>");
        pstr.close();

        MockContainer container = new MockContainer(tmpdir.getAbsolutePath());
        container.start();
        ConfigService config = ClickUtils.getConfigService(container.getServletContext());

        assertEquals(false, config.isProductionMode());
        assertEquals(false, config.isProfileMode());

        container.stop();

        deleteDir(tmpdir);
    }

    public void testPageByPath() throws Exception {
        File tmpdir = makeTmpDir();

        PrintStream pstr = makeXmlStream(tmpdir, "WEB-INF/click.xml");
        pstr.println("<click-app>");
        pstr.println(" <pages package='org.apache.click.pages'/>");
        pstr.println("</click-app>");
        pstr.close();

        PrintStream f = new PrintStream(makeFile(tmpdir, "BinaryPage.htm"));
        f.println("template");
        f.close();

        MockContainer container = new MockContainer(tmpdir.getAbsolutePath());
        container.start();
        ConfigService config = ClickUtils.getConfigService(container.getServletContext());

        assertSame(BinaryPage.class, config.getPageClass("/BinaryPage.htm"));
        assertEquals(3, config.getPageHeaders("/BinaryPage.htm").size());

        assertNull(config.getPageClass("/UnknownPage.htm"));
        assertNull(config.getPageHeaders("/UnknownPage.htm"));

        ArrayList<Class<? extends Page>> list = new ArrayList<Class<? extends Page>>();
        list.add(org.apache.click.pages.BinaryPage.class);
        assertEquals(list, config.getPageClassList());

        container.stop();

        deleteDir(tmpdir);
    }

    public void testPageByClass() throws Exception {
        File tmpdir = makeTmpDir();

        PrintStream pstr = makeXmlStream(tmpdir, "WEB-INF/click.xml");
        pstr.println("<click-app>");
        pstr.println(" <mode value='trace'/>");
        pstr.println(" <pages package='org.apache.click.pages'/>");
        pstr.println(" <pages package='org.apache.click'/>");
        pstr.println("</click-app>");
        pstr.close();

        PrintStream f = new PrintStream(makeFile(tmpdir, "BinaryPage.htm"));
        f.println("template");
        f.close();

        PrintStream f1 = new PrintStream(makeFile(tmpdir, "ListenerPage.htm"));
        f1.println("template");
        f1.close();
        PrintStream f2 = new PrintStream(makeFile(tmpdir, "pages/ListenerPage.htm"));
        f2.println("template");
        f2.close();

        MockContainer container = new MockContainer(tmpdir.getAbsolutePath());
        container.start();
        ConfigService config = ClickUtils.getConfigService(container.getServletContext());

        assertEquals("/BinaryPage.htm", config.getPagePath(BinaryPage.class));
        assertEquals(2, config.getPageFieldArray(BinaryPage.class).length);
        assertEquals(2, config.getPageFields(BinaryPage.class).size());

        try {
            assertEquals("/pages/ListenerPage.htm", config.getPagePath(ListenerPage.class));
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            // empty
        }
        assertEquals(4, config.getPageFieldArray(ListenerPage.class).length);
        assertEquals(4, config.getPageFields(ListenerPage.class).size());

        assertNull(config.getPagePath(JspPage.class));
        assertNull(config.getPageFieldArray(JspPage.class));
        assertEquals(Collections.emptyMap(), config.getPageFields(JspPage.class));

        container.stop();

        deleteDir(tmpdir);
    }

    public void testJsp() throws Exception {
        File tmpdir = makeTmpDir();

        PrintStream pstr = makeXmlStream(tmpdir, "WEB-INF/click.xml");
        pstr.println("<click-app>");
        pstr.println("<pages package='org.apache.click.pages'/>");
        pstr.println("</click-app>");
        pstr.close();

        PrintStream f = new PrintStream(makeFile(tmpdir, "BinaryPage.jsp"));
        f.println("template");
        f.close();

        MockContainer container = new MockContainer(tmpdir.getAbsolutePath());
        container.start();
        ConfigService config = ClickUtils.getConfigService(container.getServletContext());

        assertTrue(config.isJspPage("/BinaryPage.htm"));
        assertTrue(config.isJspPage("/BinaryPage"));
        assertSame(BinaryPage.class, config.getPageClass("/BinaryPage.jsp"));
        assertEquals(3, config.getPageHeaders("/BinaryPage.jsp").size());

        assertNull(config.getPageClass("/UnknownPage.jsp"));
        assertNull(config.getPageHeaders("/UnknownPage.jsp"));

        container.stop();

        deleteDir(tmpdir);
    }

    public void testXml() throws Exception {
        File tmpdir = makeTmpDir();

        PrintStream pstr = makeXmlStream(tmpdir, "WEB-INF/click.xml");
        pstr.println("<!DOCTYPE click-app PUBLIC " +
                     "\"-//Apache Software Foundation//DTD Click Configuration 2.2//EN\" " +
                     "\"http://click.apache.org/dtds/click-2.3.dtd\">");
        pstr.println("<click-app>");
        pstr.println("<pages package='org.apache.click.pages'/>");
        pstr.println("</click-app>");
        pstr.close();

        MockContainer container = new MockContainer(tmpdir.getAbsolutePath());
        container.start();

        container.stop();

        deleteDir(tmpdir);
    }

    public void testLoadPages() throws Exception {
        File tmpdir = makeTmpDir();

        PrintStream pstr = makeXmlStream(tmpdir, "WEB-INF/click.xml");
        pstr.println("<click-app>");
        pstr.println("<pages package='org.apache.click.pages' automap='false'/>");
        pstr.println("<pages package='org.apache.click.pages' autobinding='annotation'/>");
        pstr.println("<pages package='org.apache.click.pages' autobinding='public'/>");
        pstr.println("<pages package='org.apache.click.pages' autobinding='default'/>");
        pstr.println("<pages package='org.apache.click.pages' autobinding='none'/>");
        pstr.println("<pages package='org.apache.click.pages' autobinding='true'/>");
        pstr.println("<pages package='org.apache.click.pages' autobinding='false'/>");
        pstr.println("<pages/>");
        pstr.println("<pages package='org.apache.click.'/>");
        pstr.println("<pages>");
        pstr.println("  <page path='page.htm' classname='org.apache.click.pages.BinaryPage'>");
        pstr.println("    <header name='Header1' value='Value'/>");
        pstr.println("    <header name='Header2' value='Value' type='String'/>");
        pstr.println("    <header name='Header3' value='123' type='Integer'/>");
        pstr.println("    <header name='Header4' value='1' type='Date'/>");
        pstr.println("  </page>");
        pstr.println("</pages>");
        pstr.println("</click-app>");
        pstr.close();

        MockContainer container = new MockContainer(tmpdir.getAbsolutePath());
        container.start();

        container.stop();

        deleteDir(tmpdir);
    }

    /**
     * Test that manually loaded pages should specify absolute classnames, but
     * for backward compatibility fallback to appending package to classname if
     * absolute classname is not found.
     *
     * CLK-704
     */
    public void testLoadManualPagesByClassname() throws Exception {
        File tmpdir = makeTmpDir();

        PrintStream pstr = makeXmlStream(tmpdir, "WEB-INF/click.xml");
        pstr.println("<click-app>");

        // Dclare the pages package
                pstr.println("<pages package='org.apache.click.pages'/>");
        pstr.println("<pages package='org.apache.click.pages'>");

        // Check that page with absolute classname is resolved
        pstr.println("  <page path='page.htm' classname='org.apache.click.pages.BinaryPage'/>");
        // For backward compatibility, check that page with classname is resolved as well.
        // In this case Click will prefix the classname with the package declared above.
        pstr.println("  <page path='page.htm' classname='BinaryPage'/>");
        pstr.println("</pages>");
        pstr.println("</click-app>");
        pstr.close();

        MockContainer container = new MockContainer(tmpdir.getAbsolutePath());
        container.start();

        container.stop();
        deleteDir(tmpdir);
    }

    /**
     * Test that manually loaded pages that does not exist, throws appropriate
     * exception.
     *
     * CLK-704
     */
    public void testLoadManualNonExistentPageByClassname() throws Exception {
        File tmpdir = makeTmpDir();

        PrintStream pstr = makeXmlStream(tmpdir, "WEB-INF/click.xml");
        pstr.println("<click-app>");

        // Declare the pages package
                pstr.println("<pages package='org.apache.click.pages'/>");
        pstr.println("<pages package='org.apache.click.pages'>");

        // Check non existent page
        pstr.println("  <page path='page.htm' classname='org.apache.click.pages.noSuchPage'/>");
        pstr.println("</pages>");
        pstr.println("</click-app>");
        pstr.close();

        MockContainer container = null;
        try {
            container = new MockContainer(tmpdir.getAbsolutePath());
            container.start();
            fail("No class called NoSuchPage exists. Container should fail to start up");
        } catch (Exception expected) {
        } finally {
            container.stop();
        }

        deleteDir(tmpdir);
    }

    public void testHeaders() throws Exception {
        File tmpdir = makeTmpDir();

        PrintStream pstr = makeXmlStream(tmpdir, "WEB-INF/click.xml");
        pstr.println("<click-app>");
        pstr.println(" <pages package='org.apache.click.pages'/>");
        pstr.println(" <headers>");
        pstr.println("  <header name='Header1' value='Value'/>");
        pstr.println("  <header name='Header2' value='Value' type='String'/>");
        pstr.println("  <header name='Header3' value='123' type='Integer'/>");
        pstr.println("  <header name='Header4' value='1' type='Date'/>");
        pstr.println(" </headers>");
        pstr.println("</click-app>");
        pstr.close();

        PrintStream f = new PrintStream(makeFile(tmpdir, "BinaryPage.htm"));
        f.println("template");
        f.close();

        MockContainer container = new MockContainer(tmpdir.getAbsolutePath());
        container.start();

        ConfigService config = ClickUtils.getConfigService(container.getServletContext());

        Map<String, Object> headers = config.getPageHeaders("/BinaryPage.htm");
        assertEquals(4, headers.size());
        assertEquals("Value", headers.get("Header1"));
        assertEquals("Value", headers.get("Header2"));
        assertEquals(123, headers.get("Header3"));
        assertEquals(new Date(1), headers.get("Header4"));


        container.stop();

        deleteDir(tmpdir);
    }

    public void testFormat() throws Exception {
        File tmpdir = makeTmpDir();

        PrintStream pstr = makeXmlStream(tmpdir, "WEB-INF/click.xml");
        pstr.println("<click-app>");
        pstr.println(" <pages/>");
        pstr.println(" <format classname='org.apache.click.util.Format'/>");
        pstr.println("</click-app>");
        pstr.close();

        MockContainer container = new MockContainer(tmpdir.getAbsolutePath());
        container.start();

        ConfigService config = ClickUtils.getConfigService(container.getServletContext());

        assertTrue(config.createFormat() instanceof Format);

        container.stop();
        deleteDir(tmpdir);
    }

    public void testLogService() throws Exception {
        File tmpdir = makeTmpDir();

        PrintStream pstr = makeXmlStream(tmpdir, "WEB-INF/click.xml");
        pstr.println("<click-app>");
        pstr.println(" <pages/>");
        pstr.println(" <log-service classname='org.apache.click.service.ConsoleLogService'/>");
        pstr.println("</click-app>");
        pstr.close();

        MockContainer container = new MockContainer(tmpdir.getAbsolutePath());
        container.start();

        ConfigService config = ClickUtils.getConfigService(container.getServletContext());

        assertTrue(config.getLogService() instanceof ConsoleLogService);

        container.stop();
        deleteDir(tmpdir);
    }

    public void testResourceService() throws Exception {
        File tmpdir = makeTmpDir();

        PrintStream pstr = makeXmlStream(tmpdir, "WEB-INF/click.xml");
        pstr.println("<click-app>");
        pstr.println(" <pages/>");
        pstr.println(" <resource-service classname='org.apache.click.service.ClickResourceService'/>");
        pstr.println("</click-app>");
        pstr.close();

        MockContainer container = new MockContainer(tmpdir.getAbsolutePath());
        container.start();

        ConfigService config = ClickUtils.getConfigService(container.getServletContext());

        assertTrue(config.getResourceService() instanceof ClickResourceService);

        container.stop();
        deleteDir(tmpdir);
    }

    public void testTemplateService() throws Exception {
        File tmpdir = makeTmpDir();

        PrintStream pstr = makeXmlStream(tmpdir, "WEB-INF/click.xml");
        pstr.println("<click-app>");
        pstr.println(" <pages/>");
        pstr.println(" <template-service classname='org.apache.click.service.VelocityTemplateService'/>");
        pstr.println("</click-app>");
        pstr.close();

        MockContainer container = new MockContainer(tmpdir.getAbsolutePath());
        container.start();

        ConfigService config = ClickUtils.getConfigService(container.getServletContext());

        assertTrue(config.getTemplateService() instanceof VelocityTemplateService);

        container.stop();
        deleteDir(tmpdir);
    }

    public void testFileUploadService() throws Exception {
        File tmpdir = makeTmpDir();

        PrintStream pstr = makeXmlStream(tmpdir, "WEB-INF/click.xml");
        pstr.println("<click-app>");
        pstr.println(" <pages/>");
        pstr.println(" <file-upload-service classname='org.apache.click.service.CommonsFileUploadService'/>");
        pstr.println("</click-app>");
        pstr.close();

        MockContainer container = new MockContainer(tmpdir.getAbsolutePath());
        container.start();

        ConfigService config = ClickUtils.getConfigService(container.getServletContext());

        assertTrue(config.getFileUploadService() instanceof CommonsFileUploadService);

        container.stop();
        deleteDir(tmpdir);
    }

    public void testMessagesMapService() throws Exception {
        File tmpdir = makeTmpDir();

        PrintStream pstr = makeXmlStream(tmpdir, "WEB-INF/click.xml");
        pstr.println("<click-app>");
        pstr.println(" <pages/>");
        pstr.println(" <messages-map-service classname='org.apache.click.service.XmlConfigServiceTest$MyMessagesMapService'/>");
        pstr.println("</click-app>");
        pstr.close();

        MockContainer container = new MockContainer(tmpdir.getAbsolutePath());
        container.start();

        ConfigService config = ClickUtils.getConfigService(container.getServletContext());

        assertTrue(config.getMessagesMapService() instanceof MyMessagesMapService);

        container.stop();
        deleteDir(tmpdir);
    }

    static public class MyMessagesMapService implements MessagesMapService {

        public void onInit(ServletContext servletContext) throws Exception {
        }

        public void onDestroy() {
        }

        public Map<String, String> createMessagesMap(Class<?> baseClass,
                String globalResource, Locale locale) {
            return new MessagesMap(baseClass, globalResource, locale);
        }
    }
    
    public void testControls() throws Exception {
        File tmpdir = makeTmpDir();

        PrintStream pstr = makeXmlStream(tmpdir, "WEB-INF/click.xml");
        pstr.println("<click-app>");
        pstr.println("<pages package='org.apache.click.pages'/>");
        pstr.println("<controls>");
        pstr.println(" <control classname='org.apache.click.service.XmlConfigServiceTest$MyControl'/>");
        pstr.println(" <control-set name='MyControlSet.xml'/>");
        pstr.println("</controls>");
        pstr.println("</click-app>");
        pstr.close();

        MockContainer container = new MockContainer(tmpdir.getAbsolutePath());
        container.start();

        container.stop();

        deleteDir(tmpdir);
    }

    static public class MyControl extends AbstractControl {
        private static final long serialVersionUID = 1L;

        @Override
        public void onDeploy(ServletContext servletContext) {
            System.out.println("onDeploy");
        }
    }

    public void testPageInterceptors() throws Exception {
        File tmpdir = makeTmpDir();

        PrintStream pstr = makeXmlStream(tmpdir, "WEB-INF/click.xml");
        pstr.println("<click-app>");
        pstr.println(" <pages/>");
        pstr.println(" <page-interceptor classname='org.apache.click.service.XmlConfigServiceTest$MyPageInterceptor'>");
        pstr.println("  <property name='type' value='std'/>");
        pstr.println(" </page-interceptor>");
        pstr.println(" <page-interceptor classname='org.apache.click.service.XmlConfigServiceTest$MyPageInterceptor' scope='application'>");
        pstr.println("  <property name='type' value='app'/>");
        pstr.println(" </page-interceptor>");
        pstr.println("</click-app>");
        pstr.close();

        MockContainer container = new MockContainer(tmpdir.getAbsolutePath());
        container.start();

        ConfigService config = ClickUtils.getConfigService(container.getServletContext());

        List<PageInterceptor> list = config.getPageInterceptors();
        assertEquals(2, list.size());
        assertEquals("std", list.get(0).toString());
        assertEquals("app", list.get(1).toString());
        container.stop();

        deleteDir(tmpdir);
    }


    static public class MyPageInterceptor implements PageInterceptor {
        public String type;

        public boolean postCreate(Page page) {
            return false;
        }

        public void postDestroy(Page page) {
        }

        public boolean preCreate(Class<? extends Page> pageClass,
                Context context) {
            return false;
        }

        public boolean preResponse(Page page) {
            return false;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }


    private File makeTmpDir() throws IOException {
        File tmpdir = File.createTempFile("click", "");
        tmpdir.delete();
        tmpdir.mkdir();
        return tmpdir;
    }

    private PrintStream makeXmlStream(File dir, String filename) throws FileNotFoundException {
        File file = makeFile(dir, filename);
        PrintStream pstr = new PrintStream(file);
        pstr.println("<?xml version='1.0' encoding=\"UTF-8\" standalone=\"yes\"?>");
        return pstr;
    }

    private File makeFile(File dir, String filename) {
        File file = new File(dir, filename);
        file.getParentFile().mkdirs();
        return file;
    }

    private void deleteDir(File tmpdir) throws IOException {
        for (File f : tmpdir.listFiles()) {
            if (f.isDirectory()) {
                deleteDir(f);
            }
            f.delete();
        }
        tmpdir.delete();
    }
}
