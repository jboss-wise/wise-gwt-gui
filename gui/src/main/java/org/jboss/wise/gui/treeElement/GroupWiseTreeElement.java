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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * This tree element is for arrays and collections of elements
 *
 * @author Alessio Soldano, alessio.soldano@jboss.com
 */
public class GroupWiseTreeElement extends WiseTreeElement {

   private static final long serialVersionUID = 1L;

   private WiseTreeElement prototype;

   private GroupWiseTreeElement() {

      this.kind = GROUP;
      this.id = IDGenerator.nextVal();
      this.expanded = true;
   }

   public GroupWiseTreeElement(Type classType, String name, WiseTreeElement prototype) {

      this();
      this.classType = classType;
      this.nil = false;
      this.name = name;
      this.prototype = prototype;
   }

   public WiseTreeElement getPrototype() {

      return prototype;
   }

   public void setPrototype(WiseTreeElement prototype) {

      this.prototype = prototype;
   }

   public WiseTreeElement clone() {

      GroupWiseTreeElement element = new GroupWiseTreeElement();
      Iterator<Object> keyIt = this.getChildrenKeysIterator();
      while (keyIt.hasNext()) {
         WiseTreeElement child = (WiseTreeElement) this.getChild(keyIt.next());
         element.addChild(child.getId(), child.clone());
      }
      element.setName(this.name);
      element.setNil(this.nil);
      element.setClassType(this.classType);
      if (this.prototype != null) {
         element.setPrototype((WiseTreeElement) this.prototype.clone());
      }
      element.setRemovable(this.isRemovable());
      element.setNillable(this.isNillable());
      return element;
   }

   /**
    * To be used to add a brand new child to this component
    *
    * @return
    */
   public WiseTreeElement incrementChildren() {

      WiseTreeElement component = (WiseTreeElement) prototype.clone();
      component.setRemovable(true);
      addChild(component.getId(), component);
      return component;
   }

   public Object toObject() {

      if (isNil()) {
         return null;
      }
      LinkedList<Object> returnList = new LinkedList<Object>();
      Iterator<Object> keyIt = this.getChildrenKeysIterator();
      while (keyIt.hasNext()) {
         WiseTreeElement child = (WiseTreeElement) this.getChild(keyIt.next());
         returnList.add(child.toObject());
      }
      return returnList;
   }

   public String getType() {

      return ((Class<?>) ((ParameterizedType) this.classType).getActualTypeArguments()[0]).getSimpleName();
   }

   public int getSize() {

      Iterator<Object> keyIt = this.getChildrenKeysIterator();
      int i = 0;
      while (keyIt.hasNext()) {
         i++;
         keyIt.next();
      }
      return i;
   }
}
