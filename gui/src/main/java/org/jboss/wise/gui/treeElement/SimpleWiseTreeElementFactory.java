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

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

/**
 * @author alessio.soldano@jboss.com
 */
public class SimpleWiseTreeElementFactory {

   public static SimpleWiseTreeElement create(Class<?> classType, String name, Object obj) {

      SimpleWiseTreeElement element;
      if (classType.isEnum()) {
         element = new EnumerationWiseTreeElement(classType, name, null);
      } else if (QName.class.isAssignableFrom(classType)) {
         element = new QNameWiseTreeElement(classType, name, null, null);
      } else if (XMLGregorianCalendar.class.isAssignableFrom(classType)) {
         element = new XMLGregorianWiseTreeElement(classType, name, null);
      } else if (Duration.class.isAssignableFrom(classType)) {
         element = new DurationWiseTreeElement(classType, name, null);
      } else {
         element = new SimpleWiseTreeElement(classType, name, null);
      }
      if (obj != null) {
         element.parseObject(obj);
      }
      return element;
   }
}
