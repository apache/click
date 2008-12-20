package org.apache.click.tools.devtasks;

import java.io.*;
import org.apache.tools.ant.*;
import org.apache.tools.ant.types.FileSet;

public class ReplaceTabsTask extends Task {

    private String tabReplacement;
    private File mSrcDir;
    private String mNumberSpaces;
    protected FileSet mFileSet;

    public ReplaceTabsTask() {
        tabReplacement = "";
        mNumberSpaces = "4";
        mFileSet = new FileSet();
    }

    public void setNumberspaces(String aNumberSpaces) {
        mNumberSpaces = aNumberSpaces;
    }

    public void setSrcdir(File aSrcDir) {
        mSrcDir = aSrcDir;
    }

    public void setExcludes(String aExcludes) {
        mFileSet.setExcludes(aExcludes);
    }

    public void setIncludes(String aIncludes) {
        mFileSet.setIncludes(aIncludes);
    }

    public void execute() throws BuildException {
        if(mSrcDir == null)
            throw new BuildException("srcdir attribute must be set!");
        if(!mSrcDir.exists())
            throw new BuildException("srcdir does not exist!");
        if(!mSrcDir.isDirectory())
            throw new BuildException("srcdir is not a directory!");
        try {
            int numberSpaces = Integer.parseInt(mNumberSpaces);
            if(numberSpaces > 0) {
                for(int i = 0; i < numberSpaces; i++)
                    tabReplacement += " ";

            } else {
                throw new BuildException("numberspaces must be greater than 0");
            }
        }
        catch(NumberFormatException nfe) {
            throw new BuildException("numberspaces invalid: " + mNumberSpaces);
        }
        mFileSet.setDir(mSrcDir);
        mFileSet.setDefaultexcludes(true);
        DirectoryScanner lDirectoryScanner = mFileSet.getDirectoryScanner(this.project);
        String lFilesArray[] = lDirectoryScanner.getIncludedFiles();
        for(int i = 0; i < lFilesArray.length; i++)
            processFile(lFilesArray[i]);

    }

    private void processFile(String aFilename) throws BuildException {
        String lSrcFilename = mSrcDir.getAbsolutePath() + File.separatorChar + aFilename;
        String lDstFilename = lSrcFilename + ".tmp";
        BufferedReader lReader = null;
        PrintWriter lWriter = null;
        try {
            lReader = new BufferedReader(new FileReader(lSrcFilename));
            lWriter = new PrintWriter(new BufferedWriter(new FileWriter(lDstFilename)));
            boolean lHadToTrim = false;
            for(String lLine = lReader.readLine(); lLine != null; lLine = lReader.readLine()) {
                String lTrimmed = getTabReplacedLine(lLine);
                if(lTrimmed != lLine)
                    lHadToTrim = true;
                lWriter.println(lTrimmed);
            }

            lReader.close();
            lWriter.close();
            File lSrcFile = new File(lSrcFilename);
            File lDstFile = new File(lDstFilename);
            if(lHadToTrim) {
                if(lSrcFile.canWrite()) {
                    lSrcFile.delete();
                    if(lDstFile.renameTo(lSrcFile))
                        System.out.println("Tab replaced: " + aFilename);
                    else
                        System.err.println("Could not tab replace: " + aFilename);
                } else {
                    System.err.println("Could not tab replace: " + aFilename);
                }
            } else {
                lDstFile.delete();
            }
        }
        catch(IOException ioe) {
            throw new BuildException(ioe.getClass().getName() + ":" + ioe.getMessage());
        }
        finally {
            if(lReader != null)
                try {
                    lReader.close();
                }
                catch(IOException ioe) {
                    ioe.printStackTrace();
                }
        }
    }

    private String getTabReplacedLine(String aLine) {
        if(aLine.indexOf('\t') == -1)
            return aLine;
        StringBuffer buffer = new StringBuffer(aLine.length());
        char aChar = '0';
        int i = 0;
        for(int size = aLine.length(); i < size; i++) {
            aChar = aLine.charAt(i);
            if(aChar == '\t')
                buffer.append(tabReplacement);
            else
                buffer.append(aChar);
        }

        return buffer.toString();
    }
}
