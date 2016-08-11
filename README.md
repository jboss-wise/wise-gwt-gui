
The wise-gwt-gui module provides a GWT based interface to the WISE project.  This
console was implemented using MVP design pattern.

Project Directories

    gui/src/main/java/org/jboss/wise/gwt            ; GWT display components
    gui/src/main/java/org/jboss/wise/gwt/shared     ; Data structs shared between the UI and backend
    gui/src/main/java/org/jboss/wise/gwt/client     ; MVC, GWT module components and directory
                                                ;   structure as required by GWT
    gui/src/main/java/org/jboss/wise/gui            ; Preexisting classes that interface with wise-core
    gui/src/main/java/org/jboss/wise/gui/treeElement
    gui/src/main/java/org/jboss/wise/shared     ; classes shared by gwt and gui classes

    testsuite                                   ; Arquillian based Selenium tests.  It uses the Firefox driver.



Server Configuration Prerequisites

There are 2 application server configuration prerequisites in order to run this Web application.
One, an application specific security-domain must be defined in the server's standard.xml.  
Two, a set of ManagementRealm credentials.

In declaring the security-domain, there are 2 options.  
    1.) edit ${JBOSS-HOME}/standalone/configuration/standalone.xml and insert the following xml 
 
                <security-domain name="wise-security-domain" cache-type="default">
                    <authentication>
                        <login-module code="RealmDirect" flag="required">
                            <module-option name="realm" value="ManagementRealm"/>
                        </login-module>
                    </authentication>
                </security-domain>

in this subsystem

        <subsystem xmlns="urn:jboss:domain:security:1.2">
            <security-domains>
            
               <!-- INSERT XML HERE -->
               
               <security-domain name="other" cache-type="default">
               :
               :
            </security-domains>
        </subsystem>
      
    2.) Run a CLI script that inserts the above xml.
        - start the server
        - run cmd
              ${JBOSS-HOME}/bin/jboss-cli.sh -c --file=./scripts/wise-security-domain-cli.txt 
    
 
If the ManagementRealm credentials are needed use these directions to create it.
https://docs.jboss.org/author/display/WFLY8/add-user+utility
      
    
Wise can be referenced by 2 means, one, its default start page URL, or two, the URL
with a query parameter.  The default start page URL is, http://<HOST>:8080/wise
The URL with query parameter i,s http://<HOST>:8080/wise/?wsdl=http://<HOST>:<PORT>/<APPLICATION>?wsdl
      e.g. http://localhost:8080/wise/?wsdl=http://localhost:8080/wise-test-datatypes?wsdl


Arquillian based Selenium tests are provided in the testsuite directory.
They can be run under 4 profiles, wildfly800, wildfly810, wildfly821, wildfly902,
wildfly1000 and with 2 phases, test and integration-test.  The test phase runs the 
tests with the start page URL.  The integration-test phase runs tests for both the 
start page URL and the URL with query parameter.  In addition the test phase URL 
test property can be overridden using the maven system property -Dsuite.url=


Prerequisites
1. JDK 1.8
2. Firefox 38 or higher except for versions 47.0.x [1]
3. gui/target/wise-gwt-gui-<version>.war 

1. Build the gui/target/wise-gwt-gui-<version>.war
    From the project root directory run 'mvn clean install'
2. cd into testsuite and run the tests with either
       mvn test -Pwildfly<version>    
   or
       mvn integration-test -Pwildfly<version>
       

[1] https://github.com/seleniumhq/selenium/issues/2110
 
 The user can download and install any specific Firefox version.  Add the
 path to that Firefox to the beginning of his PATH variable and run the testsuite.
