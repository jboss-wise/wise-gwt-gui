
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


Arquillian based Selenium tests are provided in the testsuite directory.
They can be run under 4 profiles, wildfly800, wildfly810, wildfly820, wildfly900.

Prerequisites
1. JDK 1.8
2. Firefox 38 or higher
3. gui/target/wise-gwt-gui-<version>.war 

1. Build the gui/target/wise-gwt-gui-<version>.war
    From the project root directory run 'mvn clean install'
2. cd into testsuite
       mvn test -Pwildfly<version>    

