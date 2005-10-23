
Welcome to the Click
====================

Click is a J2EE web application framework featuring a Page
and Component oriented design. Click is an open source project 
licensed using the Apache license. 

Comprehensive HTML documentation is available online at:

    http://click.sourceforge.net

This documentation is also available in the click-documentation.war file.
	
Distributed Click JAR files include:

   dist/click-xx.jar        - Click runtime JAR including dependencies

   dist/click-nodeps-xx.jar - Click runtime JAR with no dependencies

   dist/click-extras-xx.jar - Click Extras JAR
	

Example pre-built web appliations include:

   dist/click-blank.war

   dist/click-examples.war

   deploy/click-cayenne.war


Click is built using the J2SE 1.4.2 and Ant 1.6.5.
The main Ant targets include:

    build-all             build framework, extras, webapps
    build-distribution    build distribution ZIP file
    build-extras          build click extras JAR file
    build-framework       build click framework JAR file
    build-webapps         build web application WAR files
    deploy-webapps        copy WAR files to application server
    get-deps              download JAR dependencies
    get-deps-proxy        download JAR dependencies via proxy
    help                  display the Help message
    javadoc               create Javadoc HTML files

