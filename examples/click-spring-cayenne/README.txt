
Click Spring Cayenne Example Application
========================================

This example application provides a demonstration of the Click, Spring and 
Cayenne application stack.

   * Click provides the application web tier framework.

   * Spring provides an Inversion of Control (IoC) container.

   * Cayenne provides the Object Relational Mapping (ORM) framework. 

This application is based on the cayenne-spring demonstrator by Andrei 
Adamchick. Please see the URL below for the original resource:

   http://objectstyle.org/downloads/cayenne/demos/cayenne-spring.tar.gz

The cayenne-spring application was inturn based on the original Spring Framework
distribution samples/petclinic application by Ken Krebs, Juergen Hoeller and
Rob Harrop.

   http://www.springframework.org/
   
   
PLEASE NOTE: this application does not illustrate best practices in designing
a Click application. Rather it is an adaption of the existing request based
PetClinic application to the Click framework.


The structure of this web application is detailed below:

 +---[cayenne]              Spring Cayenne integration package
 |    |
 |    +---[src]             Spring Cayenne integration Java source directory
 |    |
 |    +---[test]            Spring Cayenne integration Java test directory
 |
 +---[petclinic]            Petclinic project directory
 |    |
 |    +---[db]              Database directory 
 |    |    |
 |    |    +---[hsqldb]     HSQL database
 |    |
 |    +---[src]             Web application Java source files directory
 |    | 
 |    +---[war]             Web application root directory
 |         |
 |         +---[META-INF]   Tomcat context.xml directory
 |         |
 |         +---[WEB-INF]    Protected Web Inf directory
 |         |
 |         +---[classes]    Compile classes output directory
 |         |
 |         +---[lib]        JAR libraries directory
 |         |
 |         +---[src]        Java source files directory
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

Step 2.
-------

Compile and build the application using the Ant command:

    ant compile build

This will create a WAR file:

    deploy/click-spring-cayenne.war

Step 3.
-------

Configure the location of you application server in the
build.properties file:  build.properties

Then deploy the application use the Ant command:

    ant deploy
    
Step 4.
-------

Start the database. Change to "db/hsqldb" directory and start HSQLDB by running 
"server.sh" or "server.bat" script.

Step 5.
-------

Access the application via the URL (tomcat example): 

    http://localhost:8080/click-spring-cayenne
