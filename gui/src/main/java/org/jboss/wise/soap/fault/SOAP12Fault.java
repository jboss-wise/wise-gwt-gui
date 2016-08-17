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
package org.jboss.wise.soap.fault;

import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;

/**
 * SOAP 1.2 Fault class that can be marshalled/unmarshalled by JAXB
 * <p/>
 * <pre>
 * Example:
 * &lt;env:Envelope xmlns:env="http://www.w3.org/2003/05/soap-envelope"
 *            xmlns:m="http://www.example.org/timeouts"
 *            xmlns:xml="http://www.w3.org/XML/1998/namespace">
 * &lt;env:Body>
 *     &lt;env:Fault>
 *         &lt;env:Code>
 *             &lt;env:Value>env:Sender* &lt;/env:Value>
 *             &lt;env:Subcode>
 *                 &lt;env:Value>m:MessageTimeout* &lt;/env:Value>
 *             &lt;/env:Subcode>
 *         &lt;/env:Code>
 *         &lt;env:Reason>
 *             &lt;env:Text xml:lang="en">Sender Timeout* &lt;/env:Text>
 *         &lt;/env:Reason>
 *         &lt;env:Detail>
 *             &lt;m:MaxTime>P5M* &lt;/m:MaxTime>
 *         &lt;/env:Detail>
 *     &lt;/env:Fault>
 * &lt;/env:Body>
 * &lt;/env:Envelope>
 * </pre>
 */
@XmlRootElement(name = "Fault", namespace = "http://www.w3.org/2003/05/soap-envelope") @XmlAccessorType(XmlAccessType.FIELD) @XmlType(name = "", propOrder = {
        "code", "reason", "node", "role", "detail" }) public class SOAP12Fault {
    @XmlTransient private static final String ns = "http://www.w3.org/2003/05/soap-envelope";

    @XmlElement(namespace = ns, name = "Code") private CodeType code;

    @XmlElement(namespace = ns, name = "Reason") private ReasonType reason;

    @XmlElement(namespace = ns, name = "Node") private String node;

    @XmlElement(namespace = ns, name = "Role") private String role;

    @XmlElement(namespace = ns, name = "Detail") private DetailType detail;

    public SOAP12Fault() {
    }

    public SOAP12Fault(CodeType code, ReasonType reason, String node, String role, DetailType detail) {
        this.code = code;
        this.reason = reason;
        this.node = node;
        this.role = role;
        this.detail = detail;
    }

    public SOAP12Fault(CodeType code, ReasonType reason, String node, String role, Element detailObject) {
        this.code = code;
        this.reason = reason;
        this.node = node;
        this.role = role;
        if (detailObject != null) {
            if (detailObject.getNamespaceURI().equals(ns) && detailObject.getLocalName().equals("Detail")) {
                detail = new DetailType();
                for (Element detailEntry : DOMUtil.getChildElements(detailObject)) {
                    detail.getDetails().add(detailEntry);
                }
            } else {
                detail = new DetailType(detailObject);
            }
        }
    }

    /**
     * public SOAP12Fault(SOAPFault fault) {
     * code = new CodeType(fault.getFaultCodeAsQName());
     * try {
     * fillFaultSubCodes(fault);
     * } catch (SOAPException e) {
     * throw new WebServiceException(e);
     * }
     * <p>
     * reason = new ReasonType(fault.getFaultString());
     * role = fault.getFaultRole();
     * node = fault.getFaultNode();
     * if (fault.getDetail() != null) {
     * detail = new DetailType();
     * Iterator iter = fault.getDetail().getDetailEntries();
     * while(iter.hasNext()){
     * Element fd = (Element)iter.next();
     * detail.getDetails().add(fd);
     * }
     * }
     * }
     **/

    public SOAP12Fault(QName code, String reason, Element detailObject) {
        this(new CodeType(code), new ReasonType(reason), null, null, detailObject);
    }

    public CodeType getCode() {
        return code;
    }

    public ReasonType getReason() {
        return reason;
    }

    public String getNode() {
        return node;
    }

    public String getRole() {
        return role;
    }

    public DetailType getDetail() {
        return detail;
    }

    void setDetail(DetailType detail) {
        this.detail = detail;
    }

    public String getFaultString() {
        return reason.texts().get(0).getText();
    }

    /**
     * Recursively populate the Subcodes
     */
    private void fillFaultSubCodes(SOAPFault fault, SubcodeType subcode) throws SOAPException {
        if (subcode != null) {
            fault.appendFaultSubcode(subcode.getValue());
            fillFaultSubCodes(fault, subcode.getSubcode());
        }
    }

}
