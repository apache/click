package org.apache.click.tools.devtasks;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.util.Date;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class MakeTestCaseTask extends Task {

    private String IND;
    private String mIndentWidth;
    private File mSrcDir;
    private String mClassname;

    public MakeTestCaseTask() {
        IND = "";
        mIndentWidth = "4";
    }

    public void setIndentWidth(String aIndentWidth) {
        mIndentWidth = aIndentWidth;
    }

    public void setSrcdir(File aSrcDir) {
        mSrcDir = aSrcDir;
    }

    public void setClassname(String aClassname) {
        mClassname = aClassname;
    }

    public void execute() throws BuildException {
        if(mSrcDir == null)
            throw new BuildException("srcdir attribute must be set!");
        if(!mSrcDir.exists())
            throw new BuildException("srcdir does not exist!");
        if(!mSrcDir.isDirectory())
            throw new BuildException("srcdir is not a directory!");
        if(mClassname == null || mClassname.equals("${classname}"))
            throw new BuildException("classname not specified");
        try {
            int indentWidth = Integer.parseInt(mIndentWidth);
            if(indentWidth > 0) {
                for(int i = 0; i < indentWidth; i++)
                    IND += " ";

            } else {
                throw new BuildException("indentwidth must be greater than 0");
            }
        }
        catch(NumberFormatException nfe) {
            throw new BuildException("indentwidth invalid: " + mIndentWidth);
        }
        String lPackagename = mClassname.substring(0, mClassname.lastIndexOf("."));
        String lPackagenamePath = lPackagename.replace('.', File.separatorChar);
        String lShortClassname = mClassname.substring(mClassname.lastIndexOf(".") + 1);
        String lShortTestCaseClassname = "TestCase_" + lShortClassname;
        String lFilename = mSrcDir.getAbsolutePath() + File.separatorChar + lPackagenamePath + File.separatorChar + lShortTestCaseClassname + ".java";
        File lFile = new File(lFilename);
        if(lFile.exists())
            throw new BuildException("File already exists: " + lFilename);
        try {
            Class lClass = Class.forName(mClassname);
            if(lClass.isInterface())
                throw new BuildException(lShortClassname + " is an interface. A TestCase cannot be" + " created for a Java interface.");
            java.lang.reflect.Constructor lConstructors[] = lClass.getDeclaredConstructors();
            Method lMethods[] = lClass.getDeclaredMethods();
            PrintWriter lOut = new PrintWriter(new BufferedWriter(new FileWriter(lFilename)));
            lOut.println("/*");
            lOut.println(" * " + lShortTestCaseClassname + ".java");
            lOut.println(" *");
            lOut.println(" * Created on " + DateFormat.getDateInstance(3).format(new Date()));
            lOut.println(" */");
            lOut.println();
            lOut.println("package " + lPackagename + ";");
            lOut.println();
            lOut.println("import junit.framework.TestCase;");
            lOut.println();
            lOut.println("/**");
            lOut.println(" * The <tt>" + lShortClassname + "</tt> class <tt>TestCase</tt>.");
            lOut.println(" *");
            lOut.println(" * @author " + System.getProperty("user.name"));
            lOut.println(" */");
            lOut.println("public class " + lShortTestCaseClassname + " extends TestCase {");
            lOut.println();
            lOut.println(ind(1) + "/**");
            lOut.println(ind(1) + " * Instatiate a new <tt>" + lShortTestCaseClassname + "</tt>.");
            lOut.println(ind(1) + " *");
            lOut.println(ind(1) + " * @param aName the name of the test case");
            lOut.println(ind(1) + " */");
            lOut.println(ind(1) + "public " + lShortTestCaseClassname + "(String name) {");
            lOut.println(ind(2) + "super(name);");
            lOut.println(ind(1) + "}");
            lOut.println();
            if(lConstructors.length > 0) {
                lOut.println(ind(1) + "/**");
                lOut.println(ind(1) + " * Test <tt>" + lShortClassname + "</tt> " + " constructors.");
                lOut.println(ind(1) + " */");
                lOut.println(ind(1) + "public void test_constructors() {");
                lOut.println(ind(2) + "// Test constructor with ...");
                lOut.println(ind(1) + "}");
                lOut.println();
            }
            String lLastMethodName = "";
            for(int i = 0; i < lMethods.length; i++)
                if(Modifier.isPublic(lMethods[i].getModifiers()) || Modifier.isProtected(lMethods[i].getModifiers())) {
                    String lMethodName = lMethods[i].getName();
                    if(!lMethodName.equals(lLastMethodName)) {
                        lOut.println(ind(1) + "/**");
                        lOut.println(ind(1) + " * Test <tt>" + lShortClassname + "." + lMethodName + "()</tt> method.");
                        lOut.println(ind(1) + " */");
                        lOut.println(ind(1) + "public void test_" + lMethodName + "() {");
                        lOut.println(ind(2) + "try {");
                        lOut.println(ind(3) + "// " + lShortClassname + "." + lMethodName + "();");
                        lOut.println(ind(2) + "} catch (Throwable t) {");
                        lOut.println(ind(3) + "assertTrue(false);");
                        lOut.println(ind(2) + "}");
                        lOut.println(ind(1) + "}");
                        lOut.println();
                        lLastMethodName = lMethodName;
                    }
                }

            lOut.print("}");
            lOut.close();
            System.out.println("Created " + lShortTestCaseClassname);
            createUpdateTestSuite(lPackagename, lShortTestCaseClassname);
        }
        catch(ClassNotFoundException cnfe) {
            throw new BuildException(cnfe.getClass().getName() + ":" + cnfe.getMessage());
        }
        catch(IOException ioe) {
            throw new BuildException(ioe.getClass().getName() + ":" + ioe.getMessage());
        }
    }

    private String ind(int aTab) {
        StringBuffer lBuffer = new StringBuffer();
        for(int i = 0; i < aTab; i++)
            lBuffer.append(IND);

        return lBuffer.toString();
    }

    private void createUpdateTestSuite(String aPackageName, String aTestCaseName) throws IOException {
        if(aPackageName == null)
            throw new IllegalArgumentException("Null aPackageName parameter");
        if(aTestCaseName == null)
            throw new IllegalArgumentException("Null aTestCaseName parameter");
        String lPackagenamePath = aPackageName.replace('.', File.separatorChar);
        String lFilename = mSrcDir.getAbsolutePath() + File.separatorChar + lPackagenamePath + File.separatorChar + "TestSuite.java";
        File lFile = new File(lFilename);
        if(!lFile.exists()) {
            PrintWriter lOut = new PrintWriter(new BufferedWriter(new FileWriter(lFilename)));
            lOut.println("/*");
            lOut.println(" * TestCase.java");
            lOut.println(" *");
            lOut.println(" * Created on " + DateFormat.getDateInstance(3).format(new Date()));
            lOut.println(" */");
            lOut.println();
            lOut.println("package " + aPackageName + ";");
            lOut.println();
            lOut.println("import junit.framework.Test;");
            lOut.println();
            lOut.println("/**");
            lOut.println(" * The <tt>" + aPackageName + "</tt> package ");
            lOut.println(" * unit testing <tt>TestSuite</tt>.");
            lOut.println(" *");
            lOut.println(" * @author " + System.getProperty("user.name"));
            lOut.println(" */");
            lOut.println("public class TestSuite extends junit.framework.TestSuite {");
            lOut.println();
            lOut.println(ind(1) + "/**");
            lOut.println(ind(1) + " * @see junit.framework.TestSuite#suite()");
            lOut.println(ind(1) + " */");
            lOut.println(ind(1) + "public static Test suite() {");
            lOut.println(ind(2) + "TestSuite suite = new TestSuite();");
            lOut.println(ind(2) + "suite.addTestSuite(" + aTestCaseName + ".class);");
            lOut.println(ind(2) + "return suite;");
            lOut.println(ind(1) + "}");
            lOut.print("}");
            lOut.close();
            System.out.println("Created TestSuite");
        } else {
            updateTestSuiteFile(lFilename, aTestCaseName);
        }
    }

    private void updateTestSuiteFile(String aFilename, String aTestCaseName) throws BuildException {
        String lDstFilename = aFilename + ".tmp";
        BufferedReader lReader = null;
        PrintWriter lWriter = null;
        try {
            lReader = new BufferedReader(new FileReader(aFilename));
            lWriter = new PrintWriter(new BufferedWriter(new FileWriter(lDstFilename)));
            boolean lFoundTestCase = false;
            for(String lLine = lReader.readLine(); lLine != null; lLine = lReader.readLine()) {
                if(lLine.indexOf(aTestCaseName) != -1)
                    lFoundTestCase = true;
                if(!lFoundTestCase && lLine.indexOf("return suite;") != -1)
                    lWriter.println(ind(2) + "suite.addTestSuite(" + aTestCaseName + ".class);");
                lWriter.println(lLine);
            }

            lReader.close();
            lWriter.close();
            File lSrcFile = new File(aFilename);
            File lDstFile = new File(lDstFilename);
            if(!lFoundTestCase) {
                if(lSrcFile.canWrite()) {
                    lSrcFile.delete();
                    if(lDstFile.renameTo(lSrcFile))
                        System.out.println("Updated TestSuite");
                    else
                        System.err.println("Could not update TestSuite");
                } else {
                    System.err.println("Could not update TestSuite");
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
}
