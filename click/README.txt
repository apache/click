
Welcome to the Click
====================

Click is a J2EE web application framework featuring a Page
and Component oriented design. Click is an open source project 
licensed using the Apache license. 

Comprehensive HTML documentation is available online at:

    http://click.sourceforge.net/

This documentation is also available in the click-documentation.war file.
	
Distributed Click JAR files include:

   deploy/click-xx.jar        - Click runtime JAR including dependencies

   deploy/click-xx-nodeps.jar - Click runtime JAR with no dependencies

   deploy/click-extras-xx.jar - Click Extras JAR with no dependencies
	

Example pre-built web appliations include:

   deploy/click-blank.war

   deploy/click-examples.war


Click is built using the J2SE 1.4.2 and Ant 1.6.5.
The main Ant targets include:

    build-all             build framework, extras, webapps
    build-extras          build extras JAR file
    build-framework       build framework JAR file
    build-release         build release ZIP file
    build-webapps         build web application WAR files
    deploy-webapps        copy WAR files to application server
    help                  display the Help message
    javadoc               create Javadoc HTML files


Please note to build-extras you will need to download the following jars:

    cayenne-xx.jar  from  http://sourceforge.net/projects/cayenne

    spring-xxx.jar  from  http://sourceforge.net/projects/springframework

Then you will need to configure the build.properties entries:

    jar.cayenne= location of cayenne-xx.jar

    jar.spring= location of spring-xx.jar

