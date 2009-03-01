Click Docs
==========

Click documentation is written in Docbook format, and makes use of the Velocity
Docbook Framework (DBF) and Apache FOP (FOP) to generate output formats:PDF,
HTML and HTML (single).

Click docs makes use of Ant to build the documentation.


Project Layout
==============

The project consists of the folowing:

build.xml           the project build script
project.properties  the project properties
src/css/html/       contains the HTML stylesheet and Code Syntax Highlighter
src/docbook/click/  contains the Click Docbook document
src/images/         contains all the documentation images
src/style/html/     contains the Docbook HTML transformation rules
src/style/pdf/      contains the Docbook PDF transformation rules


HowTo Setup the Project
=======================

This project has the following dependencies:

# Ant version 1.7.1: http://ant.apache.org/
# Velocity Docbook Framework (DBF) version 1.0: http://velocity.apache.org/docbook/
# Apache FOP (at least version 0.95): http://xmlgraphics.apache.org/fop/0.95/index.html
# Docbook XLS project version 1.75.0: http://docbook.sourceforge.net/snapshots/

* Note DBF also ships with Apache FOP, but its an old version which does not
  support 'soft pagebreaks' (see below).

* As of this writing, Docbook-XLS 1.75.0 was not yet available. Instead download
  the latest snapshot release from: http://docbook.sourceforge.net/snapshots/.
  The reason for using 1.75.0 is it includes a syntax highlighter package.

Download and unzip both the Docbook Framework (DBF) 1.0, Apache FOP 0.95 and
Docbook XLS 1.75 (or latest snapshot) somewhere on your computer. Once done set
the following properties in the file project.properties:

  dbf.basedir   = directory-where-DBF-is-installed
  fop.basedir   = directory-where-FOP-is-installed
  click.basedir = directory-where-Click-is-located

The following changes must be made to DBF:

# Replace the existing docbook-xsl version of DBF with 1.75.0. You can do that
  by copying docbook-xsl-1.75.0.zip to the DBF folder '<dbf-install>/src/zip/'.
  You can leave docbook-xsl-1.70.0.zip in the folder or remove it if you wish.

# Now we need to update DBF to the new Docbook XSL version. To do this open the
  file '<dbf-install>/docbook.properties' and search for the property
  "dbf.xsl.version". Change its value from '1.70.0' to '1.75.0'. If you are using
  a snapshot release change the value from '1.70.0' to 'snapshot':

  dbf.xsl.version = snapshot

That's it for setting up the project.

Build Click Docs
================

Once the project is setup you can build the Click docs by running the build script:

  ant all

This command will generate a 'target' folder consisting of a number of subfolders.
The PDF, HTML and HTML (single) documentation is available at the following locations:

  target/click/pdf
  target/click/html
  target/click/htmlsingle

NOTE: All documentation artifacts will also be copied to the Click folder specified
by the property 'click.basedir'. The artifacts will be copied to the folder:
'<click-distribution>/documentation/docs/user-guide/'

Linking to resources
====================

The Click documentation output files (PDF and HTML), will be placed in the
folder 'user-guide' of the Click distribution:

  <click-distribution>/documentation/docs/user-guide/pdf
  <click-distribution>/documentation/docs/user-guide/html
  <click-distribution>/documentation/docs/user-guide/htmlsingle

When linking to other Click documentation such as 'click-api' and other images,
use the relative path and '../../'.

For example:

  <para>This framework uses a single servlet, called
  <ulink url="../../click-api/org/apache/click/ClickServlet.html">ClickServlet</ulink>

Notice we link to ClickServlet using the relative path '../../' since that is
where the click-api will be located relative to the user-guide.


Soft pagebreaks
===============

The generated PDF has a common problem in that there is no control over page
breaks. For example say you have a description and code listing below, the PDF
generator don't care if the description is at the end of the page and the code listing
on the start of the next. However it will be better if the description and code listing
goes together on the same page. Thus the description should be moved to the top of
the next together with the code listing. One solution is to add a soft break.

A soft pagebreak provides a hint to the PDF generator to only break to the next page
upon a certain condition e.g. only break if less than two inches are left on the page.

To add a soft pagebreak we use the following markup:

  <?dbfo-need height="2in" ?>

The above basically states that there should be at least 2 inches left
at the bottom of the page for the content to be added. If more than 2 inches is left,
render the content. If less than 2 inches is available, the page must break and the
content should be moved to the next page.

For example:

  <section id="section-hello-world">

  <?dbfo-need height="2in" ?>
  <title>Hello World</title>

This will ensure that the section <title> will be forced to the next page if less
than 2 inches are available on the current page. This alleviates the problem where
the section title is displayed at the bottom of the page but the actual content
only starts on the next page, which looks awkward.

Another example:

  <para>Some text</para>

  <!-- Below we would like to have the description and code listing on the same page,
       so we add a soft page break of 0.8 inches as shown below -->
  <?dbfo-need height="0.8in" ?>

  <para>For example:</para>
  <programlisting>public class MyPage extends Page {
  ...
} </programlisting>

Here there must be at least an 8th of an inch available at the bottom of the page,
otherwise the paragraph and program listing will be forced to the next page.

For more info see:
  http://www.sagehill.net/docbookxsl/PageBreaking.html


CHANGELOG
=========

--------------------------------------------------------------------------------

Added custom syntax highlighting for both HTML and PDF:

Changes made to src/styles/html/custom.xml
Changes made to src/styles/pdf/custom.xml

--------------------------------------------------------------------------------

Replaced JavaScript Syntax Highlighter with XSLTHL[1] which handles both HTML
and PDF.

[1]: http://sourceforge.net/projects/xslthl

--------------------------------------------------------------------------------

Added a Code Syntax Highlighter for the HTML generator.

Changes made to src/styles/html/titlepage.xml
See the section <!-- CLICK HIGHLIGHTER CHANGES START -->

--------------------------------------------------------------------------------

Reduced PDF font-size for examples.

Changes made to src/styles/pdf/custom.xls
See the section <!-- CLICK FONT CHANGES START -->

--------------------------------------------------------------------------------

Upgraded to FOP 0.95.

Changes made to src/styles/pdf/custom.xls
  See the section <!-- CLICK FOP 0.95 CHANGES START -->

--------------------------------------------------------------------------------
