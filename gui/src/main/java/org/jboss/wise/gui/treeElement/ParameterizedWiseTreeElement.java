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
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.ws.Holder;

import org.jboss.wise.core.client.WSDynamicClient;
import org.jboss.wise.core.exception.WiseRuntimeException;

/**
 * A tree element to handle JAXBElement<T>.
 *
 * @author alessio.soldano@jboss.com
 */
public class ParameterizedWiseTreeElement extends WiseTreeElement {

   private static final long serialVersionUID = 5492389675960954725L;

   private WSDynamicClient client;

   private Class<?> scope;

   private String namespace;

   private Class<?> parameterizedClass;

   public ParameterizedWiseTreeElement() {
      this.kind = PARAMETERIZED;
      this.id = IDGenerator.nextVal();
      this.expanded = true;
   }

   public ParameterizedWiseTreeElement(ParameterizedType classType, Class<?> parameterizedClass, String name, WSDynamicClient client, Class<?> scope, String namespace) {
      this();
      this.classType = classType;
      this.nil = false;
      this.name = name;
      this.client = client;
      this.scope = scope;
      this.namespace = namespace;
      this.parameterizedClass = parameterizedClass;
   }

   @Override
   public WiseTreeElement clone() {
      ParameterizedWiseTreeElement element = new ParameterizedWiseTreeElement();
      element.setName(this.name);
      element.setNil(this.nil);
      element.setClassType(this.classType);
      element.setRemovable(this.isRemovable());
      element.setNillable(this.isNillable());
      element.setClient(this.client);
      element.setScope(this.scope);
      element.setNamespace(this.namespace);
      element.setParameterizedClass(this.parameterizedClass);
      Iterator<Object> keyIt = this.getChildrenKeysIterator();
      while (keyIt.hasNext()) { // actually 1 child only
         WiseTreeElement child = (WiseTreeElement) this.getChild(keyIt.next());
         element.addChild(child.getId(), (WiseTreeElement) child.clone());
      }
      return element;
   }

   @Override
   public Object toObject() throws WiseRuntimeException {
      if (isLeaf()) {
         return null;
      }
      Object child = ((WiseTreeElement) this.getChild(this.getChildrenKeysIterator().next())).toObject();
      if (parameterizedClass.isAssignableFrom(JAXBElement.class)) {
         return instanceXmlElementDecl(this.name, this.scope, this.namespace, child);
      } else if (parameterizedClass.isAssignableFrom(Holder.class)) {
         return instanceHolder(child);
      } else {
         throw new WiseRuntimeException("Unsupported parameterized class: " + parameterizedClass);
      }
   }

   @SuppressWarnings({"rawtypes", "unchecked"})
   private Object instanceHolder(Object obj) {
      return new Holder(obj);
   }

   private Object instanceXmlElementDecl(String name, Class<?> scope, String namespace, Object value) {
      try {
         Class<?> objectFactoryClass = null;
         Method methodToUse = null;
         boolean done = false;
         List<Class<?>> objectFactories = client.getObjectFactories();
         if (objectFactories != null) {
            for (Iterator<Class<?>> it = objectFactories.iterator(); it.hasNext() && !done; ) {
               objectFactoryClass = it.next();
               Method[] methods = objectFactoryClass.getMethods();
               for (int i = 0; i < methods.length; i++) {
                  XmlElementDecl annotation = methods[i].getAnnotation(XmlElementDecl.class);
                  if (annotation != null && name.equals(annotation.name())
                      && (annotation.namespace() == null || annotation.namespace().equals(namespace))
                      && (annotation.scope() == null || annotation.scope().equals(scope))) {
                     methodToUse = methods[i];
                     break;
                  }
               }
               if (methodToUse != null) {
                  done = true;
               }
            }
         }
         if (methodToUse != null) {
            Object obj = objectFactoryClass.newInstance();
            return methodToUse.invoke(obj, new Object[]{value});
         } else {
            return null;
         }
      } catch (Exception e) {
         throw new WiseRuntimeException(e);
      }
   }

   public void setClient(WSDynamicClient client) {
      this.client = client;
   }

   public void setScope(Class<?> scope) {
      this.scope = scope;
   }

   public void setNamespace(String namespace) {
      this.namespace = namespace;
   }

   public String getNamespace() {
      return namespace;
   }

   public Class<?> getParameterizedClass() {
      return parameterizedClass;
   }

   public void setParameterizedClass(Class<?> parameterizedClass) {
      this.parameterizedClass = parameterizedClass;
   }
}