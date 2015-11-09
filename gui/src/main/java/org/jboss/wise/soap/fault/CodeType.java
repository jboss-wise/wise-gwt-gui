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

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.namespace.QName;

/**
 * <pre>
 *  &lt;env:Code>
 *       &lt;env:Value>env:Sender&lt;/env:Value>
 *       &lt;env:Subcode>
 *           &lt;env:Value>m:MessageTimeout1&lt;/env:Value>
 *           &lt;env:Subcode>
 *               &lt;env:Value>m:MessageTimeout2&lt;/env:Value>
 *           &lt;/env:Subcode>
 *       &lt;/env:Subcode>
 *  &lt;/env:Code>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CodeType", namespace = "http://www.w3.org/2003/05/soap-envelope", propOrder = {
   "Value",
   "Subcode"
})
public class CodeType {
   @XmlTransient
   private static final String ns="http://www.w3.org/2003/05/soap-envelope";

   @XmlElement(namespace = ns)
   private QName Value;

   @XmlElement(namespace = ns)
   private SubcodeType Subcode;

   public CodeType(QName value) {
      Value = value;
   }

   public CodeType() {
   }

   public QName getValue(){
      return Value;
   }

   public SubcodeType getSubcode(){
      return Subcode;
   }

   void setSubcode(SubcodeType subcode) {
      Subcode = subcode;
   }
}
