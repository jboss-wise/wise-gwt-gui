
The wise-gwt-gui module provides a GWT based interface to the WISE project.  This
console was implemented using MVP design pattern.

Project Directories

    src/main/java/org/jboss/wise/gwt            ; GWT display components
    src/main/java/org/jboss/wise/gwt/shared     ; Data structs shared between the UI and backend
    src/main/java/org/jboss/wise/gwt/client     ; MVC, GWT module components and directory
                                                ;   structure as required by GWT
    src/main/java/org/jboss/wise/gui            ; Preexisting classes that interface with wise-core
    src/main/java/org/jboss/wise/gui/treeElement
    src/main/java/org/jboss/wise/shared     ; classes shared by gwt and gui classes


wise-core
The user must build the wise-core project before building this project.


Deploy/Undeploy
    A maven plugin is provided to assist with deploy, undeploy, and redeploy of this app.
        WildFly maven goals
            mvn wildfly:deploy
            mvn wildfly:undeploy
            mvn wildfly:redeploy

        JBoss AS maven goals
            mvn jboss-as:deploy
            mvn jboss-as:undeploy
            mvn jboss-as:redeploy

