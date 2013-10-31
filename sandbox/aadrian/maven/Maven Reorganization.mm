<map version="freeplane 1.2.0">
<!--To view this file, download free mind mapping software Freeplane from http://freeplane.sourceforge.net -->
<node FOLDED="false" ID="ID_1723255651" CREATED="1283093380553" MODIFIED="1383260148246"><richcontent TYPE="NODE">

<html>
  <head>
    
  </head>
  <body>
    <p style="text-align: center">
      Maven Reorganization
    </p>
    <p style="text-align: center">
      <font size="3">(for Apache Click 2.x)</font>
    </p>
  </body>
</html>

</richcontent>
<hook NAME="MapStyle">
    <properties show_note_icons="true"/>

<map_styles>
<stylenode LOCALIZED_TEXT="styles.root_node">
<stylenode LOCALIZED_TEXT="styles.predefined" POSITION="right">
<stylenode LOCALIZED_TEXT="default" MAX_WIDTH="600" COLOR="#000000" STYLE="as_parent">
<font NAME="SansSerif" SIZE="10" BOLD="false" ITALIC="false"/>
</stylenode>
<stylenode LOCALIZED_TEXT="defaultstyle.details"/>
<stylenode LOCALIZED_TEXT="defaultstyle.note"/>
<stylenode LOCALIZED_TEXT="defaultstyle.floating">
<edge STYLE="hide_edge"/>
<cloud COLOR="#f0f0f0" SHAPE="ROUND_RECT"/>
</stylenode>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.user-defined" POSITION="right">
<stylenode LOCALIZED_TEXT="styles.topic" COLOR="#18898b" STYLE="fork">
<font NAME="Liberation Sans" SIZE="10" BOLD="true"/>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.subtopic" COLOR="#cc3300" STYLE="fork">
<font NAME="Liberation Sans" SIZE="10" BOLD="true"/>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.subsubtopic" COLOR="#669900">
<font NAME="Liberation Sans" SIZE="10" BOLD="true"/>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.important">
<icon BUILTIN="yes"/>
</stylenode>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.AutomaticLayout" POSITION="right">
<stylenode LOCALIZED_TEXT="AutomaticLayout.level.root" COLOR="#000000">
<font SIZE="18"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,1" COLOR="#0033ff">
<font SIZE="16"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,2" COLOR="#00b439">
<font SIZE="14"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,3" COLOR="#990000">
<font SIZE="12"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,4" COLOR="#111111">
<font SIZE="10"/>
</stylenode>
</stylenode>
</stylenode>
</map_styles>
</hook>
<hook NAME="AutomaticEdgeColor" COUNTER="5"/>
<font SIZE="16" BOLD="true"/>
<richcontent TYPE="NOTE">

<html>
  <head>
    
  </head>
  <body>
    <p>
      Trial to reorganize Apache Click 2.x to make use of Maven.
    </p>
  </body>
</html>

</richcontent>
<node TEXT="New Structure" POSITION="right" ID="ID_142930139" CREATED="1383260201605" MODIFIED="1383261290649">
<edge COLOR="#ff0000"/>
<font BOLD="true"/>
<node TEXT="/click" ID="ID_44566269" CREATED="1383261301372" MODIFIED="1383261342044">
<node TEXT="/click-core" ID="ID_230109486" CREATED="1383260211945" MODIFIED="1383261345567"/>
<node TEXT="/click-mock" ID="ID_681908043" CREATED="1383261010839" MODIFIED="1383261351732"/>
<node TEXT="/click-extras" ID="ID_1861903404" CREATED="1383260967128" MODIFIED="1383261354694"/>
<node TEXT="/click-extras-cayenne" ID="ID_1490261438" CREATED="1383261019912" MODIFIED="1383261364247"/>
<node TEXT="/click-extras-hibernate" ID="ID_1213618301" CREATED="1383261073628" MODIFIED="1383261367067"/>
<node TEXT="/click-extras-spring" ID="ID_1891818409" CREATED="1383261079716" MODIFIED="1383261369755"/>
<node TEXT="/click-extras-prototypejs" ID="ID_1089229183" CREATED="1383261087048" MODIFIED="1383261372241"/>
<node TEXT="/click-extras-jquery" ID="ID_878070330" CREATED="1383261097388" MODIFIED="1383261374771"/>
</node>
</node>
<node TEXT="Artifacts" POSITION="left" ID="ID_1407785884" CREATED="1383260917771" MODIFIED="1383261288112">
<edge COLOR="#0000ff"/>
<font BOLD="true"/>
<node TEXT="click.jar" ID="ID_1827983182" CREATED="1383260931251" MODIFIED="1383261238288">
<node TEXT="remains unchanged" ID="ID_862392453" CREATED="1383261240959" MODIFIED="1383261721537"/>
<node TEXT="includes dependency classes" ID="ID_1369945848" CREATED="1383261722399" MODIFIED="1383261729495"/>
<node TEXT="includes sources" ID="ID_1939639358" CREATED="1383261737292" MODIFIED="1383261741804"/>
</node>
<node TEXT="click-test.jar" ID="ID_1668592688" CREATED="1383261263312" MODIFIED="1383261281366">
<node TEXT="was click-mock.jar" ID="ID_1145044958" CREATED="1383261272149" MODIFIED="1383261284147"/>
</node>
<node TEXT="click-core.jar" ID="ID_1801361983" CREATED="1383260946011" MODIFIED="1383260949936">
<node TEXT="was click-nodeps.jar" ID="ID_1232772408" CREATED="1383261202243" MODIFIED="1383261253394"/>
<node TEXT="should be the preffered JAR to include" ID="ID_275717975" CREATED="1383261759730" MODIFIED="1383261767009"/>
</node>
<node TEXT="click-extras.jar" ID="ID_1875196219" CREATED="1383260951033" MODIFIED="1383260957173"/>
<node TEXT="click-extras-cayenne.jar" ID="ID_228015699" CREATED="1383261019912" MODIFIED="1383261778265">
<font BOLD="false"/>
</node>
<node TEXT="click-extras-hibernate.jar" ID="ID_786102209" CREATED="1383261073628" MODIFIED="1383261478757"/>
<node TEXT="click-extras-spring.jar" ID="ID_1501063370" CREATED="1383261079716" MODIFIED="1383261482136"/>
<node TEXT="click-extras-prototypejs.jar" ID="ID_1918696202" CREATED="1383261087048" MODIFIED="1383261486571"/>
<node TEXT="click-extras-jquery.jar" ID="ID_1292941605" CREATED="1383261097388" MODIFIED="1383261490886"/>
</node>
<node TEXT="Naming" POSITION="right" ID="ID_660953991" CREATED="1383261140130" MODIFIED="1383261296523">
<edge COLOR="#00ff00"/>
<font BOLD="true"/>
<node TEXT="&quot;click-extras&quot;" ID="ID_408684649" CREATED="1383261144386" MODIFIED="1383261537339">
<node TEXT="should prefix all officially suported extensions" ID="ID_1089175494" CREATED="1383261393180" MODIFIED="1383261667339"/>
</node>
</node>
<node TEXT="Archetypes" POSITION="left" ID="ID_406890385" CREATED="1383261618427" MODIFIED="1383261649410">
<edge COLOR="#00ffff"/>
<font BOLD="true"/>
<node TEXT="see" ID="ID_453931904" CREATED="1383261632901" MODIFIED="1383261634140">
<node TEXT="https://code.google.com/p/construtor/wiki/MavenArchetypeClickSetup" ID="ID_994743713" CREATED="1383261634756" MODIFIED="1383261641681" LINK="https://code.google.com/p/construtor/wiki/MavenArchetypeClickSetup"/>
</node>
</node>
</node>
</map>
