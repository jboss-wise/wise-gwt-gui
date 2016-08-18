/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.wise.shared;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.OperationBuilder;
import org.jboss.as.controller.client.helpers.ClientConstants;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.Property;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import org.jboss.logging.Logger;

/**
 * Retrieve all wsdl-urls deployed to the current server.
 * User: rsearls
 * Date: 2/18/15
 */
public class WsdlFinder implements Serializable {

    private static final long serialVersionUID = -6841057226758482815L;
    private static final Logger log = Logger.getLogger(WsdlFinder.class);

    public WsdlFinder() {
    }

    public List<String> getWsdlList() {
	List<String> wsdlList = new ArrayList<String>();
	try {
            List<ModelNode> dataSources = getDeployedApps();
            for (ModelNode dataSource : dataSources) {
                List<ModelNode> endpointList = getEndpoints(dataSource.asString());
                for (ModelNode endPt : endpointList) {
                    addWSDLURLs(endPt, wsdlList);
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
        return wsdlList;
    }

    private void addWSDLURLs(ModelNode mNode, List<String> wsdlList) {
	for (Property p : mNode.asPropertyList()) {
	    for (Property pp : p.getValue().asPropertyList()) {
		if (pp.getValue().has("wsdl-url")) {
		    String wsdlUrl = pp.getValue().get("wsdl-url").asString();
		    if (wsdlUrl != null) {
			wsdlList.add(wsdlUrl);
		    }
		}
	    }
	}
    }

    /**
     * @param appName
     * @return
     * @throws java.io.IOException
     */
    private List<ModelNode> getEndpoints(String appName) throws IOException {
        final ModelNode request = new ModelNode();
        request.get(ClientConstants.OP).set("read-resource");
        ModelNode address = request.get("address");
        address.add("deployment", appName);
        address.add("subsystem", "webservices");
        request.get("recursive").set(true);
        ModelControllerClient client = null;

        List<ModelNode> resultList = new ArrayList<ModelNode>();
        try {
            client = ModelControllerClient.Factory.create(InetAddress.getByName("127.0.0.1"), 9990);
            final ModelNode response = client.execute(new OperationBuilder(request).build());

            if ("success".equals(response.get(ClientConstants.OUTCOME).asString())) {
                resultList.addAll(response.get(ClientConstants.RESULT).asList());
            }
        } finally {
            safeClose(client);
        }

        return resultList;
    }

    /**
     * @return
     * @throws IOException
     */
    private List<ModelNode> getDeployedApps() throws IOException {
        final ModelNode request = new ModelNode();
        request.get(ClientConstants.OP).set("read-children-names");
        request.get("child-type").set("deployment");
        ModelControllerClient client = null;

        List<ModelNode> resultList = new ArrayList<ModelNode>();
        try {

            client = ModelControllerClient.Factory.create(InetAddress.getByName("127.0.0.1"), 9990);
            final ModelNode response = client.execute(new OperationBuilder(request).build());

            if ("success".equals(response.get(ClientConstants.OUTCOME).asString())) {
                resultList.addAll(response.get(ClientConstants.RESULT).asList());
            }
        } finally {
            safeClose(client);
        }
        return resultList;
    }

    private void safeClose(final Closeable closeable) {
        if (closeable != null)
            try {
                closeable.close();
            } catch (Exception e) {
                // no-op
            }
    }
}
