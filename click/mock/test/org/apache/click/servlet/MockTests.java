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
package org.apache.click.servlet;

import org.apache.click.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Sanity checks for Mock package.
 */
public class MockTests extends TestCase {

    /**
     * Sanity checks for MockContext.
     */
    public void testMockContext() {
        Context context = MockContext.initContext();
        ServletConfig servletConfig = context.getServletConfig();
        ServletContext servletContext = context.getServletContext();
        Assert.assertNotNull(context);
        Assert.assertNotNull(servletConfig);
        Assert.assertNotNull(servletContext);
    }

    /**
     * Some sanity checks for ServletConfig.
     */
    public void testMockServletConfig() {
        final String servletName = "click servlet";
        final String key = "initKey";
        final String value = "initValue";
        ServletContext servletContext = new MockServletContext();
        MockServletConfig servletConfig = new MockServletConfig(servletName,
          servletContext, new HashMap<String, String>());

        //test that the method is null safe
        servletConfig.addInitParameters(null);

        servletConfig.addInitParameter(key, value);

        Assert.assertEquals(value, servletConfig.getInitParameter(key));
        Assert.assertEquals(servletName, servletConfig.getServletName());
        Assert.assertEquals(servletContext, servletConfig.getServletContext());
    }

    /**
     * Some sanity checks for ServletContext.
     */
    public void testMockServletContext() {
        try {
            final String contextPath = "/mock-context";
            final String emptyWebappPath = "";
            final String emptyTempPath = "";
            final String key = "initKey";
            final String value = "initValue";
            MockServletContext servletContext = new MockServletContext(contextPath, emptyWebappPath, emptyTempPath);

            servletContext.addInitParameter(key, value);
            Assert.assertEquals(value, servletContext.getInitParameter(key));
            Assert.assertEquals(contextPath, servletContext.getContextPath());

            //Test if the temporary directory is created
            String tempPath = "click-temp";
            File tempDir = new File(tempPath);
            System.out.println("TEMP DIR : " + tempDir.exists());
            Assert.assertFalse(tempDir.exists());
            servletContext.setTempPath("click-temp");
            Assert.assertTrue(tempDir.exists());

            File resource = new File(tempDir, "resource.html");
            resource.createNewFile();
            FileWriter writer = new FileWriter(resource);
            String origData = "some test data";
            writer.write(origData);
            writer.flush();
            writer.close();

            //Set the webapp root to the temporary directory
            servletContext.setWebappPath(tempPath);

            //Test that getResource locates resource.html
            URL url = servletContext.getResource(resource.getName());
            Assert.assertNotNull(url);

            //Test that getResourceAsStream can read resource.html
            InputStream resourceStream = servletContext.getResourceAsStream(resource.getName());

            //Test that the data read from resource.html is the same as was 
            //written to it
            byte[] resultData = new byte[origData.length()];
            resourceStream.read(resultData);
            boolean sameBytes = Arrays.equals(resultData, origData.getBytes());
            Assert.assertTrue(sameBytes);
            resourceStream.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }
    }

    /**
     * Check if two temporary directories can be deleted.
     */
    public void testTemporaryDirectorySetup() {
        try {
            //Check that MockServletContext constructor creates the temp dir
            String tempPath = "click-temp-1";
            //Check that the temporary directory does not exist 
            File tempDir = new File(tempPath);
            Assert.assertFalse(tempDir.exists());

            MockServletContext servletContext = new MockServletContext(
              null, null, tempPath);

            //Check that the temporary directory was created
            Assert.assertTrue(tempDir.exists());

            //Check that the method setTempPath creates the temp dir
            String tempPath2 = "click-temp-2";
            //Point the temporary directory to tempPath2
            File tempDir2 = new File(tempPath2);
            Assert.assertFalse(tempDir2.exists());
            servletContext.setTempPath(tempPath2);
            Assert.assertTrue(tempDir2.exists());

            //Check that when specifying the java property java.io.tmpdir,
            //a mock directory is added to avoid file locking issues when
            //deleting the entire operating system temp dir.
            String tempPath3 = System.getProperty("java.io.tmpdir");
            servletContext.setTempPath(tempPath3);
            File convertedTemp = new File(servletContext.getTempPath());
            Assert.assertTrue(convertedTemp.exists());

        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail("Should not happen");
        }
    }

    /**
     * Sanity check for deleting a directory recursively.
     */
    public void testDeleteDirectoryOnShutdown() {

        try {
            final File tmpDir = new File(System.getProperty("java.io.tmpdir") + "/click-tests");
            tmpDir.mkdir();
            Assert.assertTrue(tmpDir.isDirectory() && tmpDir.canWrite());

            final File subDir = new File(tmpDir, "subdir");
            subDir.mkdir();
            Assert.assertTrue(subDir.isDirectory() && subDir.canWrite());

            File tmpFile = new File(subDir, "temp-file.txt");
            tmpFile.createNewFile();

            //Write data to file
            final FileOutputStream fis = new FileOutputStream(tmpFile);
            fis.write(1);
            
            //Close file so it will not lock
            fis.close();

            //Test if delete directory will wipe the directory
            boolean result = MockServletContext.deleteDirectory(tmpDir);
            Assert.assertTrue(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail("Temporary directory must be deleted");
        }
    }
}
