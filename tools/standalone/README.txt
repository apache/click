Standalone Tools for the Click Framework
========================================

This project contains the following packages:

* 'deploy' (package org.apache.click.tools.deploy) - provides an Ant task to deploy Click 
  resources from a given source folder to a target folder. Example usage:

  <target name="deploy" description="Deploy Click static resources" depends="build">
    <taskdef name="deploy"
      classname="org.apache.click.tools.deploy.DeployTask"
      classpath="C:/dev/click-2.2.0/lib/click-dev-tasks-1.1.jar"/>

    <deploy dir="C:/dev/myapp/web/WEB-INF" todir="c:/dev/myapp/web/"/>
  </target>


  The 'deploy' task also supports nested filesets:

  <target name="deploy" description="Deploy Click static resources" depends="build">
    <taskdef name="deploy"
      classname="org.apache.click.tools.deploy.DeployTask"
      classpath="C:/dev/click-2.2.0/lib/click-dev-tasks-1.1.jar"/>

    <deploy todir="c:/dev/myapp/web/">
        <fileset dir="C:/dev/myapp/web/WEB-INF">
            <include name="**/classes"/>
            <include name="**/*.jar"/>
        </fileset>
        <fileset dir="C:/dev/myjars/lib">
            <include name="click-xxx.jar"/>
            <include name="click-extras-xxx.jar"/>
        </fileset>
    </deploy>
  </target>

* 'devtasks' (package org.apache.click.tools.devtasks) - a set of ANT tasks to help 
  in the development of Click Framework itself, Click based 3rd party controls and 
  Click based webapplications. Example usage:
  
  <target name="format-java" description="Format the sources before check-in">      
      <taskdef name="linetrim"
   	     classname="org.apache.click.tools.devtasks.LineTrimTask"
   	     classpath="C:/dev/click-2.2.0/lib/click-dev-tasks-1.1.jar"/>

      <taskdef name="replacetabs"
   	     classname="org.apache.click.tools.devtasks.ReplaceTabsTask"
      	 classpath="C:/dev/click-2.2.0/lib/click-dev-tasks-1.1.jar"/>

      <linetrim srcdir="src" includes="**/*.java"/>
      <replacetabs srcdir="src" includes="**/*.java"/>
  </target>

  