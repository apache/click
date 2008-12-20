package org.apache.click.tools.devtasks;

import java.io.*;
import org.apache.tools.ant.*;
import org.apache.tools.ant.types.FileSet;

public class LineTrimTask extends Task {

    private File mSrcDir;
    protected FileSet mFileSet;

    public LineTrimTask() {
        mFileSet = new FileSet();
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
                String lTrimmed = getTrimmedLine(lLine);
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
                        System.out.println("Trimmed: " + aFilename);
                    else
                        System.err.println("Could not trim: " + aFilename);
                } else {
                    System.err.println("Could not trim: " + aFilename);
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

    private String getTrimmedLine(String aLine) {
        int lLength = aLine.length();
        int lLastCharIndex = 0;
        for(int i = lLength - 1; i >= 0; i--) {
            if(Character.isWhitespace(aLine.charAt(i)))
                continue;
            lLastCharIndex = i;
            break;
        }

        if(lLength > 0) {
            if(lLastCharIndex != lLength - 1) {
                if(!Character.isWhitespace(aLine.charAt(lLastCharIndex)))
                    return aLine.substring(0, lLastCharIndex + 1);
                else
                    return "";
            } else {
                return aLine;
            }
        } else {
            return aLine;
        }
    }
}
