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
package net.sf.click.extras.devel.filemonitor;

import com.sun.rsasign.i;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bob Schellink
 */
public class FileMonitor {
    
    protected static final Logger LOG = Logger.getLogger(FileMonitor.class.getName());
    
    private Map entries;
    private boolean initialized = false;
    
    private List listeners;
    
    private boolean exitOnChange = false;
    
    public FileMonitor() {
        init();
    }
    
    public FileMonitor(boolean exitOnChange) {
        this.exitOnChange = exitOnChange;
        init();
    }
    
    public void init() {
        if(initialized) {
            return;
        }
        
        entries = new HashMap();
        initialized = true;
    }
    
    public void clear() {
        if(!initialized) {
            return;
        }
        entries.clear();
    }
    
    public void remove(File file) {
        if(file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        
        if(!file.exists()) {
            return;
        }
        
        if(file.isDirectory()) {
            //Directories are not monitored.
            return;
        }
        
        try {
            entries.remove(file.getCanonicalPath());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Could not retrieve the canonical path for file -> " + file.getAbsolutePath(), ex);
        }
    }
    
    /**
     * Only files can be added to the monitor.
     * @param file
     */
    public void add(File file) {
        if(file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        
        if(!file.exists()) {
            return;
        }
        
        if(file.isDirectory()) {
            //Directories are not monitored.
            return;
        }
        
        try {
            //Check for duplicates
            if(entries.keySet().contains(file.getCanonicalPath())) {
                return;
            }
        } catch (IOException ex) {
            //ignore
        }
        
        try {
            FileEntry entry = new FileEntry(file);
            entries.put(file.getCanonicalPath(), entry);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Could not retrieve the canonical path for file -> " + file.getAbsolutePath(), ex);
        }
    }
    
    public Collection getMonitoredFiles() {
        return entries.values();
    }
    
    public boolean checkForModifications() {
        if(!initialized) {
            return false;
        }
        
        //int size = entries.size();
        int size = entries.keySet().size();
        LOG.fine("Number of file entries monitored -> " + size);
        
        for(Iterator it = entries.values().iterator(); it.hasNext(); ) {
            
            FileEntry monitor = (FileEntry) it.next();
            
            if(monitor.wasModified()) {
                
                LOG.info("File was changed -> " + monitor.getFile().getAbsolutePath());
                notifyListenersOfChange(monitor);
                if(exitOnChange) {
                    return true;
                }
                
            } else if(monitor.wasDeleted()) {
                
                LOG.info("File was deleted -> " + monitor.getFile().getAbsolutePath());
                notifyListenersOfDelete(monitor);
                if(exitOnChange) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Monitor a file for any changes.
     */
    public static class FileEntry {
        private long lastModified;
        private File file;
        
        public FileEntry(File file) {
            setLastModified(file.lastModified());
            setFile(file);
        }
        
        public long getLastModified() {
            return lastModified;
        }
        
        public void setLastModified(long lastModified) {
            this.lastModified = lastModified;
        }
        
        public File getFile() {
            return file;
        }
        
        public void setFile(File file) {
            this.file = file;
        }
        
        public boolean wasModified() {            
            if(lastModified < file.lastModified()) {
                lastModified = file.lastModified();
                return true;
            }
            return false;
        }
        
        public boolean wasDeleted() {
            return !file.exists();
        }
        
        public String toString() {
            return "File monitored -> " + getFile().getAbsolutePath();
        }
    }
    
    public void addFileChangeListener(FileChangeListener listener) {
        getListeners().add(listener);
    }
    
    public void removeFileChangeListener(FileChangeListener listener) {
        getListeners().remove(listener);
    }
    
    public List getListeners() {
        if(listeners == null) {
            listeners = new ArrayList();
        }
        return listeners;
    }
    
    private void notifyListenersOfChange(FileEntry monitor) {
        int size = getListeners().size();
        for(int i = 0; i < size; i++) {
            FileChangeListener listener = (FileChangeListener) listeners.get(i);
            listener.fileChanged(monitor.getFile());
        }
    }
    
    private void notifyListenersOfDelete(FileEntry monitor) {
        int size = getListeners().size();
        for(int i = 0; i < size; i++) {
            FileChangeListener listener = (FileChangeListener) listeners.get(i);
            listener.fileDeleted(monitor.getFile());
        }
    }
}
