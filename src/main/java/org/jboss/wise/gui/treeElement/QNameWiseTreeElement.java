/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.wise.gui.treeElement;

import javax.xml.namespace.QName;

/**
 * A simple tree element to handle QName instances.
 *
 * @author Alessio Soldano, alessio.soldano@jboss.com
 */
public class QNameWiseTreeElement extends SimpleWiseTreeElement {

   private static final long serialVersionUID = 1L;

   private String localPart;

   private String nameSpace;

   private QNameWiseTreeElement() {
      this.kind = QNAME;
   }

   public QNameWiseTreeElement(Class<?> classType, String name, String localPart, String nameSpace) {
      super();
      this.kind = QNAME;
      this.classType = classType;
      this.name = name;
      this.localPart = localPart;
      this.nameSpace = nameSpace;
   }

   @Override
   public String getValue() {
      return "{" + nameSpace + "}" + localPart;
   }

   public WiseTreeElement clone() {
      QNameWiseTreeElement element = new QNameWiseTreeElement();
      element.setName(this.name);
      element.setNil(this.nil);
      element.setClassType(this.classType);
      element.setLocalPart(localPart);
      element.setNameSpace(nameSpace);
      element.setRemovable(this.isRemovable());
      element.setNillable(this.isNillable());
      return element;
   }

   public String getNameSpace() {
      return nameSpace;
   }

   public void setNameSpace(String nameSpace) {
      this.nameSpace = nameSpace;
   }

   public String getLocalPart() {
      return localPart;
   }

   public void setLocalPart(String localPart) {
      this.localPart = localPart;
   }

   @Override
   public void enforceNotNillable() {
      this.nillable = false;
      this.nil = false;
      this.value = "";
   }

   @Override
   public void parseObject(Object obj) {
      if (obj != null) {
         this.localPart = (((QName) obj).getLocalPart());
         this.nameSpace = (((QName) obj).getNamespaceURI());
      } else {
         this.localPart = null;
         this.nameSpace = null;
      }
   }

   @Override
   public Object toObject() {
      return isNil() ? null : new QName(nameSpace, localPart);
   }
}
