Standalone Tools for the Click Framework
========================================

This project contains the following packages:

* deploy - provides an Ant task to deploy Click resources from a given source
  folder to a target folder. Example usage:

  <target name="deploy" description="Deploy Click static resources" depends="build">
    <taskdef name="deploy"
      classname="org.apache.click.tools.deploy.DeployTask"
      classpath="C:/dev/click-2.1.0/lib/click-dev-tasks-1.1.jar"/>

    <deploy dir="C:/dev/myapp/web/WEB-INF" todir="c:/dev/myapp/web/"/>
  </target>

* devtasks - a set of ANT tasks to help in the development of Click Framework
  itself, Click based 3rd party controls and Click based webapplications.
