#Setup automatic class reloading

1) For automatic class reloading to work you must replace ClickApp and ClickServlet that 
    is included in the click-core with the same named files in this package. An easy way to do
    this is to place these two files in the classpath before the regular click-core classes.
    

2) DynamicClassLoader currently excludes all classes by default. To enable dynamic
    replacement of classes you can programmatically call
    'DynamicClassLoader.addPackageToInclude(String package)'.

    If you use the ReloadClassFilter, you can specify a comma seperated list of 
    packages to be loaded at filter initialization time. By specifying the 
    initialization parameter 'included-packages', you can provide a list of packages 
    that will be added to the DynamicClassLoader, for example:
    
     <filter>
        <filter-name>reload-filter</filter-name>
        <filter-class>net.sf.click.extras.devel.ReloadClassFilter</filter-class>
        <init-param>
            <param-name> 
                included-packages
            </param-name>
            <param-value>
                net.sf.click.tests.reload, com.mycorp.test
            </param-value>
        </init-param>
    </filter>

    The snippet above will add the packages 'net.sf.click.tests.reload' and
    'com.mycorp.test' to the DynamicClassLoader's list of packages to load.


3) Certain servlet containers have the ability to track changes to classes 
    and jars, and reload the entire web application when changes occur. You should 
    probably disable this feature in your container if you want automatic class 
    reloading to work. Otherwise instead of only reloading the changed class, the 
    servlet container will restart the entire application.

    For Tomcat you can disable this feature by adding the attribute 
    'reloadable="false"' to your Context.xml file for example:

    <Context
        path="/click-test" 
        reloadable="false"
        antiJARLocking="true">
    
    
    Jetty (at least 6.1) does not seem to support this feature.

    Some IDE's like Netbeans will also reload the entire web application when you 
    click the "Run" button. So instead of hitting "Run" just refresh the browser
    to reload classes.
