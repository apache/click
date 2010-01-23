
Welcome to ClickIDE
================

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

    http://incubator.apache.org/click/docs/click-ide.html


Release Notes
=============

Detailed release notes are available here:

    http://incubator.apache.org/click/docs/click-ide-roadmap-changes.html


Build Information
=================

ClickIDE is built using the J2SE 1.5.0 and Eclipse 3.5.x.
At first, import following plugins as plugin project:

 - org.apache.click.eclipse
 - org.apache.click.eclipse.cayenne
 - org.apache.click.eclipse.feature

Then run org.apache.click.eclipse.feature/clickide-build.xml by following sequence:

 1) Run feature_export task
 
   This task exports plugins as dest directory using PDE.
   So this task requires Eclipse environment.
   
   Note: Plugin exporting is executed as asynchronous job, so we have to wait for 
   the completion of exporting before running the next task.
 
 2) Run append_files task
 
   This task copies LICENSE.txt, NOTICE.txt and README.txt to
   the exported directory.
   
 3) Run create_zip task
 
   This task makes a zip file to release.

Then a plugin would be exported to org.apache.click.eclipse.feature/dest directory 
as clickide-x.x.x.zip.

