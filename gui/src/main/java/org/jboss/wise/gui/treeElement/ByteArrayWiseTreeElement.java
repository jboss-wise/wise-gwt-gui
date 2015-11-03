/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
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

import javax.xml.bind.DatatypeConverter;

/**
 * A simple tree element to handle byte[]
 *
 * @author alessio.soldano@jboss.com
 */
public class ByteArrayWiseTreeElement extends SimpleWiseTreeElement {

   private static final long serialVersionUID = 1L;

   public ByteArrayWiseTreeElement() {
      this.kind = BYTE_ARRAY;
   }

   public ByteArrayWiseTreeElement(Class<?> classType, String name, String value) {
      this.kind = BYTE_ARRAY;
      this.classType = classType;
      this.nil = value == null;
      this.name = name;
      this.value = value;
   }

   @Override
   public WiseTreeElement clone() {
      ByteArrayWiseTreeElement element = new ByteArrayWiseTreeElement();
      element.setName(this.name);
      element.setNil(this.nil);
      element.setClassType(this.classType);
      element.setValue(this.value);
      element.setRemovable(this.isRemovable());
      element.setNillable(this.isNillable());
      return element;
   }

   @Override
   public void parseObject(Object obj) {
      if (obj != null) {
         this.setValue(DatatypeConverter.printBase64Binary((byte[]) obj));
      } else {
         this.setValue(null);
      }
      this.nil = (obj == null && nillable);
   }

   @Override
   public Object toObject() {
      if (isNil())
         return null;
      byte[] result = null;
      if (value != null) {
         return DatatypeConverter.parseBase64Binary(value);
      }
      return result;
   }
}