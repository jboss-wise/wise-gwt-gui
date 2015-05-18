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

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.jboss.wise.core.exception.WiseRuntimeException;
import org.jboss.wise.core.utils.ReflectionUtils;

/**
 * A WiseTreeElement for non primitive/simple types.
 *
 * @author Alessio Soldano, alessio.soldano@jboss.com
 */
public class ComplexWiseTreeElement extends WiseTreeElement {

   private static final long serialVersionUID = 1L;

   private ComplexWiseTreeElement() {
      this.kind = COMPLEX;
      this.id = IDGenerator.nextVal();
      this.expanded = true;
   }

   public ComplexWiseTreeElement(Type classType, String name) {
      this();
      this.classType = classType;
      this.nil = false;
      this.name = name;
   }

   public WiseTreeElement clone() {
      ComplexWiseTreeElement element = new ComplexWiseTreeElement();
      Iterator<Object> keyIt = this.getChildrenKeysIterator();
      while (keyIt.hasNext()) {
         WiseTreeElement child = (WiseTreeElement) this.getChild(keyIt.next());
         element.addChild(child.getId(), child.clone());
      }
      element.setName(this.name);
      element.setNil(this.nil);
      element.setClassType(this.classType);
      element.setRemovable(this.isRemovable());
      element.setNillable(this.isNillable());
      return element;
   }

   @SuppressWarnings("unchecked")
   public Object toObject() {
      if (isNil())
         return null;
      Object obj = null;
      try {
         Class<?> cl = (Class<?>) classType;
         obj = cl.newInstance();

         Iterator<Object> keyIt = this.getChildrenKeysIterator();
         while (keyIt.hasNext()) {
            WiseTreeElement child = (WiseTreeElement) this.getChild(keyIt.next());
            String setter = ReflectionUtils.setterMethodName(child.getName(), child.isTypeBoolean());
            String getter = ReflectionUtils.getterMethodName(child.getName(), child.isTypeBoolean());

            Method method;
            if (child instanceof GroupWiseTreeElement) {
               method = cl.getMethod(getter, (Class[]) null);
               Collection<?> col = (Collection<?>) method.invoke(obj, (Object[]) null);
               col.addAll((List) child.toObject());
            } else {
               Object childObject = child.toObject();
               if (child instanceof ParameterizedWiseTreeElement) {
                  method = cl.getMethod(setter, (Class<?>) ((ParameterizedType) child.getClassType()).getRawType());
               } else {
                  if (childObject == null) {
                     continue;
                  }
                  Class<?> fieldClass = (Class<?>) child.getClassType();
                  if (Duration.class.isAssignableFrom(fieldClass)) {
                     method = cl.getMethod(setter, Duration.class);
                  } else if (XMLGregorianCalendar.class.isAssignableFrom(fieldClass)) {
                     method = cl.getMethod(setter, XMLGregorianCalendar.class);
                  } else {
                     method = cl.getMethod(setter, fieldClass);
                  }

               }
               method.invoke(obj, childObject);
            }
         }
      } catch (Exception e) {
         throw new WiseRuntimeException("Error converting element to object", e);
      }
      return obj;
   }

}
