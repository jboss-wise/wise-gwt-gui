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
import javax.xml.soap.SOAPFault;
import java.util.Iterator;

/**
 * This class represents SOAP1.1 Fault. This class will be used to marshall/unmarshall a soap fault using JAXB.
 * <p/>
 * <pre>
 * Example:
 * <p/>
 *     &lt;soap:Fault xmlns:soap='http://schemas.xmlsoap.org/soap/envelope/'>
 *         &lt;faultcode>soap:Client&lt;/faultcode>
 *         &lt;faultstring>Invalid message format&lt;/faultstring>
 *         &lt;faultactor>http://example.org/someactor&lt;/faultactor>
 *         &lt;detail>
 *             &lt;m:msg xmlns:m='http://example.org/faults/exceptions'>
 *                 Test message
 *             &lt;/m:msg>
 *         &lt;/detail>
 *     &lt;/soap:Fault>
 * <p/>
 * Above, m:msg, if a known fault (described in the WSDL), IOW, if m:msg is known by JAXBContext it should be unmarshalled into a
 * Java object otherwise it should be deserialized as {@link javax.xml.soap.Detail}
 * </pre>
 * <p/>
 */

@XmlAccessorType(XmlAccessType.FIELD) @XmlType(name = "", propOrder = { "faultcode", "faultstring", "faultactor",
        "detail" }) @XmlRootElement(name = "Fault", namespace = "http://schemas.xmlsoap.org/soap/envelope/") public class SOAP11Fault {
    @XmlElement(namespace = "") private QName faultcode;

    @XmlElement(namespace = "") private String faultstring;

    @XmlElement(namespace = "") private String faultactor;

    @XmlElement(namespace = "") private DetailType detail;

    public SOAP11Fault() {
    }

    /**
     * @param code
     * @param reason
     * @param actor
     * @param detailObject
     */
    public SOAP11Fault(QName code, String reason, String actor, Element detailObject) {
        this.faultcode = code;
        this.faultstring = reason;
        this.faultactor = actor;
        if (detailObject != null) {
            if (detailObject.getNamespaceURI().equals("") && detailObject.getLocalName().equals("detail")) {
                detail = new DetailType();
                for (Element detailEntry : DOMUtil.getChildElements(detailObject)) {
                    detail.getDetails().add(detailEntry);
                }
            } else {
                detail = new DetailType(detailObject);
            }
        }
    }

    public SOAP11Fault(SOAPFault fault) {
        this.faultcode = fault.getFaultCodeAsQName();
        this.faultstring = fault.getFaultString();
        this.faultactor = fault.getFaultActor();
        if (fault.getDetail() != null) {
            detail = new DetailType();
            Iterator iter = fault.getDetail().getDetailEntries();
            while (iter.hasNext()) {
                Element fd = (Element) iter.next();
                detail.getDetails().add(fd);
            }
        }
    }

    public QName getFaultcode() {
        return faultcode;
    }

    void setFaultcode(QName faultcode) {
        this.faultcode = faultcode;
    }

    public String getFaultString() {
        return faultstring;
    }

    void setFaultstring(String faultstring) {
        this.faultstring = faultstring;
    }

    public String getFaultactor() {
        return faultactor;
    }

    void setFaultactor(String faultactor) {
        this.faultactor = faultactor;
    }

    public DetailType getDetail() {
        return detail;
    }

    void setDetail(DetailType detail) {
        this.detail = detail;
    }

}
