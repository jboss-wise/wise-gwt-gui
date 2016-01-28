
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


Wise can be reference by 2 means, by its default start page URL, or by the URL
with a query parameter.  The default start page URL is, http://<HOST>:8080/wise
The URL with query parameter i,s http://<HOST>:8080/wise/?wsdl=http://<HOST>:<PORT>/<APPLICATION>?wsdl
      e.g. http://localhost:8080/wise/?wsdl=http://localhost:8080/wise-test-datatypes?wsdl

Arquillian based Selenium tests are provided in the testsuite directory.
They can be run under 4 profiles, wildfly800, wildfly810, wildfly820, wildfly900
and with 2 phases, test and integration-test.  The test phase runs the tests with
the start page URL.  The integration-test phase runs tests for both the start page URL
and the URL with query parameter.  In addition the test phase URL test property can be
overridden using the maven system property -Dsuite.url=


Prerequisites
1. JDK 1.8
2. Firefox 38 or higher
3. gui/target/wise-gwt-gui-<version>.war 

1. Build the gui/target/wise-gwt-gui-<version>.war
    From the project root directory run 'mvn clean install'
2. cd into testsuite and run the tests with either
       mvn test -Pwildfly<version>    
   or
       mvn integration-test -Pwildfly<version>
