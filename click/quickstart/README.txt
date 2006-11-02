
Click Quick Start Application
=============================

The structure of this web application is detailed below:

 +---[src]                  Java source files directory
 |
 +---[webapp]               Web application root directory
 |    |
 |    +---[images]          Web Images directory 
 |    |    
 |    +---[META-INF]        Tomcat context.xml directory
 |    |
 |    +---[WEB-INF]         Protected Web Inf directory
 |         |
 |         +---[classes]    Compile classes output directory
 |
 +---build.xml


Use the provided Ant build.xml file and follow the steps below
to build and deploy this application.

Step 1.
-------

Compile and build the application using the Ant command:

    ant compile build

This will create a WAR file:

    ../deploy/click-quickstart.war

Step 2.
-------

Configure the location of you application server in the
build.properties file:  build.properties

Then deploy the application use the Ant command:

    ant deploy

Step 3.
-------

Access the application via the URL (Tomcat example): 

    http://localhost:8080/click-quickstart
