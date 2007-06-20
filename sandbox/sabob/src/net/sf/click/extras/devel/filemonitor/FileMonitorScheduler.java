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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * Each scheduler can have multiple FileMonitors registered.
 * All registered FileMonitors are run one after the other after
 * the specified period elapsed.
 *
 * @author Bob Schellink
 */
public class FileMonitorScheduler extends TimerTask {
    
    protected static final Logger LOG = Logger.getLogger(FileMonitorScheduler.class.getName());
    
    private int period = 2000;//default rescan period of 2 seconds
    private Timer timer = null;
    private List fileMonitors;
    
    public FileMonitorScheduler(int rescanPeriod) {
        this.period = rescanPeriod;
    }
    
    public void start() {
        timer = new Timer(true);
        timer.schedule(this, new Date(), period);
    }
    
    public void stop() {
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    
    public void run() {
        LOG.fine("Starting file scanning task for bob");
        for(Iterator it = fileMonitors.iterator(); it.hasNext(); ) {
            FileMonitor fm = (FileMonitor) it.next();
            fm.checkForModifications();
        }
        LOG.fine("End of file scanning task");
    }
    
    public void addFileMonitor(FileMonitor fm) {
        if(fileMonitors == null) {
            fileMonitors = new ArrayList();
        }
        fileMonitors.add(fm);
    }
    
    public void removeFileMonitor(FileMonitor fm) {
        if(fileMonitors != null) {
            fileMonitors.remove(fm);
        }
    }
}
