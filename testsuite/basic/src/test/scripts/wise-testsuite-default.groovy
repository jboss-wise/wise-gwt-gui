def root = new XmlParser().parse(project.properties['inputFile'])

/**
 * Add a security-domain block like this:
 *
 * <security-domain name="wise-security-domain" cache-type="default">
 *   <authentication>
 *      <login-module code="RealmDirect" flag="required">
 *           <module-option name="realm" value="ManagementRealm"/>
 *       </login-module>
 *  </authentication>
 * </security-domain>
 *
 */
def securityDomains = root.profile.subsystem.'security-domains'[0]
def securityDomain = securityDomains.appendNode('security-domain', ['name':'wise-security-domain','cache-type':'default'])
def authentication = securityDomain.appendNode('authentication')
def loginModule = authentication.appendNode('login-module', ['code':'RealmDirect','flag':'required'])
loginModule.appendNode('module-option', ['name':'realm','value':'ManagementRealm'])


/**
 * Settings for modules/testsuite/shared-tests/src/test/java/org/jboss/test/ws/jaxws/samples/securityDomain/SecurityDomainTestCase.java
 * Add a security-domain block like this:
 *
 * <security-domain name="JBossWSSecurityDomainTest" cache-type="default">
 *   <authentication>
 *     <login-module code="UsersRoles" flag="required">
 *       <module-option name="usersProperties" value="/jaxws/samples/securityDomain/jbossws-users.properties"/>
 *       <module-option name="rolesProperties" value="/jaxws/samples/securityDomain/jbossws-roles.properties"/>
 *     </login-module>
 *   </authentication>
 * </security-domain>
 */
def bSecurityDomainBasicAuth = securityDomains.appendNode('security-domain', ['name':'JBossWSSecurityDomainTest','cache-type':'default'])
def bAuthenticationBasicAuth = bSecurityDomainBasicAuth.appendNode('authentication')
def bLoginModuleBasicAuth = bAuthenticationBasicAuth.appendNode('login-module', ['code':'UsersRoles','flag':'required'])
bLoginModuleBasicAuth.appendNode('module-option', ['name':'usersProperties','value':project.properties['testResourcesDir'] + '/jaxws/samples/securityDomain/jbossws-users.properties'])
bLoginModuleBasicAuth.appendNode('module-option', ['name':'rolesProperties','value':project.properties['testResourcesDir'] + '/jaxws/samples/securityDomain/jbossws-roles.properties'])



/**
 * Save the configuration to a new file
 */
def writer = new StringWriter()
writer.println('<?xml version="1.0" encoding="UTF-8"?>')
new XmlNodePrinter(new PrintWriter(writer)).print(root)
def f = new File(project.properties['outputFile'])
f.write(writer.toString())