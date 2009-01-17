BEST PRACTICE
=============

# Soft page break: Add soft breaks before sections and programlisting descriptions
e.g.

  <?dbfo-need height="2in" ?>
  <section id="section-hello-world">

This will ensure that the <section> will be forced to the next page if less
than 2 inches available on the current page. This alleviates the problem where
the section title is displayed at the bottom of the page but the actual content
only starts on the next page, which looks awkward.

Another example:

  <?dbfo-need height="2in" ?>
  <para>Next we would have a page template:</para>
  <programlisting>
  ...
  </programlisting>

The snippet above will ensure that the paragraph (together with the programlisting)
will be forced to the next page if less than 2 inches is available. This removes
the problem where the program listing description is at the bottom of one page
and the program listing only starts on the next page by itself.

CHANGELOG
=========

--------------------------------------------------------------------------------

Added a Code Syntax Highlighter to HTML generator.

Changes made to docs/src/styles/html/titlepage.xml
 See the section <!-- CLICK HIGHLIGHTER CHANGES START --> in that file

--------------------------------------------------------------------------------

JAI

install JAI for pdf image rendering from Sun:

https://jai.dev.java.net/binary-builds.html#Stable_builds_1.1.3

--------------------------------------------------------------------------------

Reduced PDF font-size for examples.

Changes made to

docs/src/styles/pdf/custom.xls
  See the section <!-- CLICK FONT CHANGES START --> in that file

--------------------------------------------------------------------------------

# Updated to FOP 0.95. Changes needed to be made to

build-docbook.xml
 See the section <!-- CLICK FOP 0.95 CHANGES START --> in that file
docs/src/styles/pdf/custom.xls
  See the section <!-- CLICK FOP 0.95 CHANGES START --> in that file

added new libs:
  batik-all-1.7.jar
  fop.jar
  commons-io.jar
  commons-logging.jar
  xmlgraphics-commons-1.3.1.jar

--------------------------------------------------------------------------------

The generated PDF has a common problem in that there is no control over page
breaks. For example say you have a description and code listing below, the PDF
generator don't care if the description is at the end of the page and the code listing
on the start of the next. However it will be better if the description and code listing
goes together on the same page. Thus the description should be moved to the top of
the next together with the code listing. One solution is to add a soft break. Here
one can add some markup which will force a section to break to the next page. A soft
page break provides a "hint" to the PDF generator to only break to the next page
upon a certain condition e.g. only break if less than 2inches are left on the page.

Soft breaks provide some protection against further editing of the document at a later
stage.

For pagebreaking use: <?dbfo-need height="2in" ?>

The above basically states that there should be at least 2 inches left
at the bottom of the page for the content to be added. If more than 2 inches is left,
render the content. If less than 2 inches is available, the page must break and the
content should be moved to the next page.

e.g:
  <para>Some text in a paragraph</para>

  <!-- We would like to have the description and code listing on the same page,
       so we add a soft page break (2 inches) as shown below -->
  <?dbfo-need height="2in" ?>

  <para>The following code snippet illustrates the technique.</para>

  <programlisting># Some sample code</programlisting>

For further info see:
  http://www.sagehill.net/docbookxsl/PageBreaking.html

--------------------------------------------------------------------------------