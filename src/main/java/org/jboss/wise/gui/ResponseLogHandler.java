/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.wise.gui;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.LogicalMessageContext;
import javax.xml.ws.handler.MessageContext;

/**
 * Logs the client inbound response message
 * 
 * @author alessio.soldano@jboss.com
 */
public class ResponseLogHandler implements LogicalHandler<LogicalMessageContext> {

    private final OutputStream outputStream;

    public ResponseLogHandler(OutputStream outStream) {
	this.outputStream = outStream;
    }

    public Set<QName> getHeaders() {
	return new HashSet<QName>(); // empty set
    }

    public boolean handleMessage(LogicalMessageContext smc) {
	doLog(smc);
	return true;
    }

    public boolean handleFault(LogicalMessageContext smc) {
	doLog(smc);
	return true;
    }

    // nothing to clean up
    public void close(MessageContext messageContext) {
    }

    private void doLog(LogicalMessageContext smc) {
	if (!(Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)) {
	    try {
		TransformerFactory tff = TransformerFactory.newInstance();
		Transformer tf = tff.newTransformer();
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		
		Source sc = smc.getMessage().getPayload();
		
		StreamResult result = new StreamResult(outputStream);
		tf.transform(sc, result);
		
	    } catch (Exception e) {
		PrintWriter ps = new PrintWriter(outputStream);
		ps.println("Exception getting response message log: ");
		e.printStackTrace(ps);
	    }
	}
    }
}
