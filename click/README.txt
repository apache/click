
Welcome to Click
================

Click is a J2EE web application framework featuring a Page
and Component oriented design. Click is an open source project 
licensed using the Apache license. 

Comprehensive HTML documentation is available online at:

    http://click.sourceforge.net

This documentation is also available in the WAR file:

   dist/click-documentation.war
	
Distributed Click JAR files include:

   dist/click-xx.jar        - Click runtime JAR including dependencies

   dist/click-nodeps-xx.jar - Click runtime JAR with no dependencies

   dist/click-extras-xx.jar - Click Extras JAR	

Example pre-built web appliation include:

   dist/click-examples.war

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
    build-sources         build source ZIP files for use with IDEs
    deploy-examples       copy click-examples WAR file to app server
    get-deps              download JAR dependencies
    get-deps-proxy        download JAR dependencies via proxy
    help                  display the Help message
    javadoc               create Javadoc HTML files

