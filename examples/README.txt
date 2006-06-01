
Click Example Application
=========================

The structure of this web application is detailed below:

 +---[src]                  Java source files directory
 |
 +---[webapp]               Web application root directory
 |    |
 |    +---[images]          Web Images directory 
 |    | 
 |    +---[javadoc]         Web Javadoc output directory
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

Download JAR dependencies using Ant command:

    ant get-deps

If you are behind a firewall configure your proxy settings in
the build.properties file: build.properties

Then use the Ant command:

    ant get-deps-proxy
    
Ensure you have also downloaded the JAR dependencies using
the main Click build file: ../build/build/build.xml

    ant get-deps

Step 2.
-------

Compile and build the application using the Ant command:

    ant compile build

This will create a WAR file:

    ../deploy/click-examples.war

Step 3.
-------

Configure the location of you application server in the
build.properties file:  build.properties

Then deploy the application use the Ant command:

    ant deploy

Step 4.
-------

Access the application via the URL (tomcat example): 

    http://localhost:8080/click-examples
