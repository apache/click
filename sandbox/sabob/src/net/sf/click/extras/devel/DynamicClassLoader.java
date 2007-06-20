/*
 * Copyright 2002-2006 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.click.extras.devel;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import net.sf.click.extras.devel.filemonitor.FileMonitor;

/**
 * This class loader reverses the search order for classes.  It checks this classloader
 * before it checks its parent. In addition it can be configured with includes and excludes.
 *
 * @version $Id: DefaultClassLoader.java 490762 2006-12-28 17:03:53Z danielf $
 * @author Bob Schellink
 *
 * NOTE: This class was adapted and modified from the Apache Cocoon
 * implementation https://svn.apache.org/repos/asf/cocoon/tags/cocoon-2.2/cocoon-bootstrap/cocoon-bootstrap-1.0.0-M1/src/main/java/org/apache/cocoon/classloader/DefaultClassLoader.java
 *
 * Other articles of interest :
 * http://tech.puredanger.com/2006/11/09/classloader/ 
 * http://www.javaworld.com/javaworld/javaqa/2003-06/01-qa-0606-load.html
 * http://www.javalobby.org/java/forums/t18345.html
 */

public class DynamicClassLoader extends URLClassLoader {
    
    protected static final Logger LOG = Logger.getLogger(DynamicClassLoader.class.getName());
    
    private FileMonitor fileMonitor;
    private List includedPackages = new ArrayList();
    
    public DynamicClassLoader(URL[] classpath, ClassLoader parent) {
        super(classpath, parent);
    }
    
    public DynamicClassLoader(URL[] classpath, ClassLoader parent, FileMonitor fileMonitor) {
        super(classpath, parent);
        this.fileMonitor = fileMonitor;
        LOG.info("DynamicClassLoader <<init>>");
    }
    
    public void addPackageToInclude(String packageName) {
        includedPackages.add(packageName);
    }
    
    protected boolean shouldLoadClass(String name) {
        if(name == null || name.length() == 0) {
            return false;
        }
        
        if (name.startsWith("java.") || name.startsWith("javax.servlet")) {
            return false;
        }
        
        for(Iterator it = includedPackages.iterator(); it.hasNext(); ) {
            String packageName = (String) it.next();
            if(name.startsWith(packageName)) {
                return true;
            }
        }
        
        return false;
    }
    
    protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        //First, check if the class has already been loaded
        Class c = findLoadedClass(name);
        
        if (c == null) {
            
            //If not loaded yet, check if this class's package is included in the list
            //of allowed packages
            if(shouldLoadClass(name)) {
                try {
                    LOG.fine("Trying to load class -> " + name + ".");
                    c = findClass(name);
                    
                    //The class loaded successfully. Start monitoring the class for
                    //any modifications.
                    monitorForChange(c);
                    LOG.fine("    SUCCESS: " + name + " was loaded.");
                } catch (ClassNotFoundException ex) {
                    LOG.fine("    FAILURE: " + name + " was not loaded.");
                    if(getParent() == null) {
                        throw ex;
                    }
                }
            }
            
            if(c == null) {                

                if(getParent() == null) {                    
                    throw new ClassNotFoundException(name);
                } else {
                    
                    //The class was not loaded so delegate to parent class loader
                    c = getParent().loadClass(name);
                }
            }
        }
        
        if (resolve) {
            resolveClass(c);
        }
        return c;
    }
    
    public final URL getResource(String name) {
        LOG.info("get resource from child classloader -> " + name);
        
        //Try to find resource locally
        URL resource = findResource(name);
        
        if (resource == null) {
            //If not found try parent
            LOG.fine("get resource from parent classloader -> " + name);
            resource = getParent().getResource(name);
        }
        return resource;
    }
    
    private void monitorForChange(Class clazz) {
        LOG.fine("Start monitoring class -> " + clazz.getName());
        String className = clazz.getName().replace('.', '/').concat(".class");
        URL classAsResource = getResource(className);
        File file = new File(classAsResource.getFile());
        if(!file.exists()) {
            LOG.warning("The class -> " + file.getAbsolutePath() + " does not exist as a file and cannot be monitored");
        }
        fileMonitor.add(file);
    }
}
