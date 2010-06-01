
Welcome to ClickIDE
===================

ClickIDE is an Eclipse plug-in for the developing Click web applications.

ClickIDE is a sub project of Apache Click ant it is an open source project 
licensed using the Apache license. 


Installation
============

ClickIDE 2.1.0.x requires Eclipse 3.5.x and WTP 3.1.x.

The easiest way to use ClickIDE is installing Eclipse IDE for Java EE Developers 
and get a copy of ClickIDE from the downloads page. Unzip clickide-x.x.x.zip and 
put 2 folders (plugins/ and features/) into your ECLIPSE_HOME. 


Docmentation
============

Comprehensive HTML documentation is available online at:

    http://click.apache.org/docs/click-ide.html


Release Notes
=============

Detailed release notes are available here:

    http://click.apache.org/docs/click-ide-roadmap-changes.html


Build Information
=================

ClickIDE is built using the J2SE 1.5.0, Eclipse 3.5.x and Ant.

First, checkout the source code from SVN onto a folder on your local machine:

  svn co http://svn.apache.org/repos/asf/click/trunk/tools/eclipse/

Next, open Eclipse and import the following plugins into a Plug-in Project:

 - org.apache.click.eclipse
 - org.apache.click.eclipse.cayenne
 - org.apache.click.eclipse.feature

The Ant build script is located at : org.apache.click.eclipse.feature/clickide-build.xml.

Note: Ant tasks have to be run in the same JRE as the workspace. To do this right click
on clickide-build.xml -> Run As -> Ant Build ... -> JRE -> select the "Run in the same
JRE as the workspace" radio button. 

From the Eclipse IDE run org.apache.click.eclipse.feature/clickide-build.xml as follows:

 1) Run feature_export task
 
   This task exports plugins to the dest directory using PDE and requires
   an Eclipse environment.
   
   Note: Plugin exporting is executed as asynchronous job, so we have to wait for 
   the completion of exporting before running the next task.
 
 2) Run append_files task
 
   This task copies LICENSE.txt, NOTICE.txt and README.txt to
   the exported directory.
   
 3) Run create_zip task
 
   This task makes a zip file to release.

Then a plugin would be exported to org.apache.click.eclipse.feature/dest directory 
as clickide-x.x.x.zip.

