
Welcome to Click
================

Click is a J2EE web application framework featuring a Page
and Component oriented design. Click is an open source project 
licensed using the Apache license. 


Docmentation
============

Comprehensive HTML documentation is available online at:

    http://incubator.apache.org/click

This documentation is also available in the directory:

   documentation


Release Notes
=============

Detailed release notes are available here:

   documentation/docs/roadmap-changes.html


Distribution Jars
=================

Distributed Click JAR files include:

   dist/click-xx.jar        - Click runtime JAR including dependencies

   dist/click-nodeps-xx.jar - Click runtime JAR with no dependencies

   dist/click-extras-xx.jar - Click Extras JAR

   dist/click-mock-xx.jar   - Click Mock Utilities JAR


Examples
========

Example pre-built web appliation include:

   dist/click-examples.war


Build Information
=================

Click is built using the J2SE 1.4.2 and Ant 1.6.5. 

The Ant build.xml and build.properties files are located in 
the build directory. 

The main Ant targets include:

    build-all             build framework, extras, examples
    build-distribution    build distribution ZIP file
    build-examples        build click-examples WAR file
    build-extras          build click-extras JAR file
    build-framework       build click framework JAR file
    build-maven-bundles   build Maven repository upload bundles
    build-mock            build mock JAR file
    build-sources         build source ZIP files for use with IDEs
    checkstyle            run checkstyle report on Java soruce
    deploy-examples       copy example WAR files to app server
    get-deps              download JAR dependencies
    get-deps-proxy        download JAR dependencies via proxy
    help                  display the Help message
    javadoc               create Javadoc HTML files
    project-quick-start   build application template
    test-all              run all unit tests


Before building the framework, all third-party library dependencies must be
downloaded using the command:

    ant get-deps

To build a new distribution use run the command:

    ant build-distribution

To build the core library, click-x.x.x.jar, run the command:

    ant build-framework

To build the extras library, click-extras.x.x.x.jar, run the command:

    ant build-extras

Further information on building Click is available here:

   documentation/docs/developer-guide/building.html
