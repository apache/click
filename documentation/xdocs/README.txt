Click Book
==========

The Click book is written in Docbook, and makes use of the Velocity
Docbook Framework (DBF) and Apache FOP (FOP) to generate output various formats
such as PDF, HTML (multiple pages) and HTML (single page).


Project Layout
==============

The project consists of the following:

build.xml           the documentation build script
project.properties  the documentation properties
src/css/html/       contains the HTML stylesheet and Code Syntax Highlighter
src/docbook/click/  contains the Click Docbook document
src/images/         contains all the documentation images
src/style/html/     contains the Docbook HTML transformation rules
src/style/pdf/      contains the Docbook PDF transformation rules


Dependencies
============

This project has the following dependencies:

# Velocity Docbook Framework (DBF) version 1.0: http://velocity.apache.org/docbook/
# Apache FOP (at least version 0.95): http://xmlgraphics.apache.org/fop/0.95/index.html
# Docbook XLS project version 1.75.0: https://sourceforge.net/project/showfiles.php?group_id=21935&package_id=16608&release_id=680922
# XSLTHL syntax highlighter version 2.0.1: http://sourceforge.net/projects/xslthl

A separate project, called 'Click Docbook', was created to distributes the
above dependencies in a zip file.


Building Click Book
===================

Click Book is built using the J2SE 1.4.2 and Ant 1.6.5.

The Ant build.xml and build.properties files are located in this directory.

The main Ant targets include:

    all                   build Click Book in PDF, HTML (multi) and HTML (single) format
    pdf                   build Click Book in PDF format
    html                  build Click Book in HTML multi page format
    htmlsingle build      build Click Book in HTML single page format

Before building the documentation, all third-party library dependencies must be
downloaded using the command:

    ant get-deps

The 'get-deps' command will download the dependency from the 'Click Docbook'
project. The dependency is a 9MB zip file containing all that is needed to
build the documentation.

To build the Click Book in all available formats run:

    ant all
