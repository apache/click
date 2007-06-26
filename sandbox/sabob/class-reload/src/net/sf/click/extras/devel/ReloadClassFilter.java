/*
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
package net.sf.click.extras.devel;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import net.sf.click.extras.devel.filemonitor.FileChangeListener;
import net.sf.click.extras.devel.filemonitor.FileMonitor;
import net.sf.click.extras.devel.filemonitor.FileMonitorScheduler;

/**
 *
 * Bob Schellink
 */
public class ReloadClassFilter implements Filter, FileChangeListener {
    
    protected static final Logger LOG = Logger.getLogger(ReloadClassFilter.class.getName());
    
    private FileMonitorScheduler fileScheduler = null;
    private DynamicClassLoader dynamicClassLoader = null;
    private FileMonitor fileMonitor = null;
    private URL[] classpath = null;
    private final Object lock = new Object();
    private List includedPackagesList = new ArrayList();
    private List initialClasspath = new ArrayList();
    
    public ReloadClassFilter() {
        //This option will stop the scanning of other entries in the FileMonitor
        //to save some overhead.
        boolean stopScanningAfterFileChange = true;
        fileMonitor = new FileMonitor(stopScanningAfterFileChange);
        fileScheduler = new FileMonitorScheduler(2000);
        fileScheduler.addFileMonitor(fileMonitor);
        fileMonitor.addFileChangeListener(this);
        fileScheduler.start();
    }
    
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        synchronized (lock) {
            if(dynamicClassLoader == null) {
                createDynamicClassLoader();
            }
        }
        
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(dynamicClassLoader);
            chain.doFilter(request, response);
        } catch(Throwable t) {
            while (t instanceof ServletException) {
                t = ((ServletException) t).getRootCause();
            }
            t.printStackTrace();
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }
    
    public void destroy() {
        fileScheduler.stop();
    }
    
    private static final String INCLUDED_PACKAGES = "included-packages";
    private static final String CLASSPATH = "classpath";
    
    public void init(FilterConfig filterConfig) throws ServletException {
        String includedPackages = filterConfig.getInitParameter(INCLUDED_PACKAGES);
        if(includedPackages != null) {
            StringTokenizer tokens = new StringTokenizer(includedPackages, ", \n\t");
            while(tokens.hasMoreTokens()) {
                String token = tokens.nextToken();
                includedPackagesList.add(token);
            }
            LOG.info("Packages that will be loadable -> " + includedPackagesList);
        }
        
        String classpathParams = filterConfig.getInitParameter(CLASSPATH);
        if(classpathParams != null) {
            StringTokenizer tokens = new StringTokenizer(classpathParams, ", \n\t");
            while(tokens.hasMoreTokens()) {
                String token = tokens.nextToken();
                initialClasspath.add(token);
            }
            LOG.info("Initial classpath used -> " + initialClasspath);
        }
    }
    
    public void createDynamicClassLoader() {
        LOG.info("Creating a new DynamicClassLoader!!!!!");
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        classpath = getClasspath();
        dynamicClassLoader = new DynamicClassLoader(classpath, parent, fileMonitor);
        for(Iterator it = includedPackagesList.iterator(); it.hasNext(); ) {
            String packageName = (String) it.next();
            dynamicClassLoader.addPackageToInclude(packageName);
        }
    }
    
    public URL[] getClasspath() {
        List classpath = new ArrayList();
        for(Iterator it = initialClasspath.iterator(); it.hasNext(); ) {
            String path = (String) it.next();
            addToClasspath(path, classpath);
        }
        classpath.addAll(extractUrlList(Thread.currentThread().getContextClassLoader()));
        LOG.info("Classpath for the ReloadClassFilter (" + classpath + ")");
        return (URL[]) classpath.toArray(new URL[] {null});
    }
    
    protected void addToClasspath(String path, List classpath) {
        try {
            File f = new File(path);
            if(f.exists()) {
                classpath.add(f.toURL());
            }
        } catch (MalformedURLException ex) {
        }
    }
    
    public List extractUrlList(ClassLoader cl) {
        List urlList = new ArrayList();
        try {
            Enumeration en = cl.getResources("");
            while(en.hasMoreElements()) {
                Object url = en.nextElement();
                urlList.add(url);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return urlList;
    }
    
    public void fileChanged(File file) {
        LOG.fine("The file " + file.getAbsolutePath() + " was changed");
        createDynamicClassLoader();
        fileMonitor.clear();
    }
    
    public void fileDeleted(File file) {
        LOG.fine("The file " + file.getAbsolutePath() + " was deleted");
        createDynamicClassLoader();
        fileMonitor.clear();
    }
}
