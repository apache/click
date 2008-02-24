
Click Quick Start Application
=============================

The structure of this web application is detailed below:

 +---[lib]                  Build time JAR libs directory
 |
 +---[src]                  Java source files directory
 |
 +---[webapp]               Web application root directory
 |    |
 |    +---[admin]           Admin role pages directory 
 |    |
 |    +---[assets]          Web static assets directory 
 |    |    
 |    +---[META-INF]        Tomcat context.xml directory
 |    |
 |    +---[user]            User role pages directory 
 |    |
 |    +---[WEB-INF]         Protected Web Inf directory
 |         |
 |         +---[classes]    Compile classes output directory
 |         |
 |         +---click.xml    Click configuration file
 |         |
 |         +---menu.xml     Menu configuration file
 |         |
 |         +---web.xml      Web configuration file
 |
 +---build.xml              Ant build script file
 |
 +---README.txt             Read Me description file


To build the application WAR file using the Ant command:

    ant build
