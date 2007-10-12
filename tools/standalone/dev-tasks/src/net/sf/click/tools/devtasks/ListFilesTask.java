package net.sf.click.tools.devtasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import java.io.*;

/**
 * Task that generates a list of files required by a <code>Control</code> to easily do
 * the <code>Control#onDeploy()</code> operation in case it has many files to deploy. This applies
 * especially for javascript libraries like TinyMCE, jQuery or Yahoo UI where the number of files
 * to deploy is extremly high. <p/>
 *
 * <b>Usage:</b>
 * <ol>
 *   <li>Add click-dev-tasks-xxx.jar to your build path.</li>
 *   <li>Define/specify this task in your <code>build.xml</code> file:
 *    <pre class="codeHtml">
 *    &lt;taskdef name="listfiles"
 *        classname="net.sf.click.tools.devtasks.ListFilesTask"
 *        classpath="click-dev-tasks-xxx.jar"/&gt;</pre>
 *    </li>
 *   <li>Use this task before building your JARs:
 *    <pre class="codeHtml">
 *     &lt;listfiles controlPath="src/com/mycorp/control/HeavyControl" dirName="heavy_js_lib"/&gt;</pre>
 *   This task will produce a <tt>src/com/mycorp/control/HeavyControl.files</tt> file. 
 *   As a convention all files for deploy are to be found in a subdirectory where the current control is located. 
 *   </li>
 *   <li>Use a simplified way of doing <code>Control#onDeploy()</code> since everything will work automatically from now on:
 *    <pre class="codeJava">
 *    public void onDeploy(ServletContext servletContext) {
 *         ClickUtils.deployFileList(servletContext, HeavyControl.class,"click");
 *    }
 *    </pre>
 *   </li>
 * </ol>
 */
public class ListFilesTask extends Task {
    /** Path to the control that requires a file list, except it's extension. E.g.: <tt>src/com/mycorp/control/HeavyControl</tt>*/
    public String controlPath;
    /** The directory name where the resources are placed. This should be a subdirectory of the controls location.E.g.:<tt>heavy_js_lib</tt>*/
    public String dirName;
    /** Verbose mode to display each file that is added to the list */
    public boolean verbose;

    public void setControlPath(String controlPath) {
        this.controlPath = controlPath;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void execute() throws BuildException {
        if(controlPath==null)
            throw new BuildException("controlPath attribute must be set!",getLocation());
        if(dirName==null)
            throw new BuildException("dirName attribute must be set!",getLocation());

        File controlFile = new File(controlPath+".java");        
        if(!controlFile.exists())
            throw new BuildException("specified control does not exist!", getLocation());

        File parentDir = controlFile.getParentFile();
        String controlName = controlFile.getName();
        controlName = controlName.substring(0,controlName.indexOf(".java"));        
        
        if(parentDir ==null)
            throw new BuildException("ERRRRR:",getLocation());
        
        File dir2search = new File(parentDir,dirName);
        if(!dir2search.exists())
            throw new BuildException("specified directory does not exist!", getLocation());

        log("Process filelist for '"+controlName+"' with diretory: "+dir2search);
        File destFile = new File(parentDir,controlName+".files");

        PrintStream ps = null;
        try {
            ps = new PrintStream(new FileOutputStream(destFile));
            writeFileEntry(dir2search,ps);
        } catch (IOException e) {
            throw new BuildException(e,getLocation());
        } finally {
            ps.flush();
            ps.close();        
        }
    }

    private void writeFileEntry(File parent, PrintStream ps) throws IOException {
        if(verbose)
            log("Process Directory: "+parent);
        
        File[] files = parent.listFiles(new FilenameFilter(){
            public boolean accept(File dir, String name) {
                // todo: ANT should have some API to automatically exclude all type of VCS related files/dirs.
                if(name.startsWith(".svn"))
                    return false;
                else
                    return true;
            }
        });
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if(file.isDirectory()) {
                writeFileEntry(file, ps);
            } else {
                if(verbose)
                    log("Add file: "+file);
                String path = file.getPath();
                path = path.substring(path.indexOf(dirName));
                path = path.replace('\\','/');
                ps.println(path);
            }
        }
    }
}
