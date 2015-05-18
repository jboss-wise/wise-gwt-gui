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

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.jboss.wise.gui.model.TreeNode;
import org.jboss.wise.gui.model.TreeNodeImpl;

/**
 * The abstract tree element, extended by all the custom tree element. Provides
 * common TreeNode implementation.
 *
 * @author alessio.soldano@jboss.com
 */
public abstract class WiseTreeElement extends TreeNodeImpl implements TreeNode, Serializable, Cloneable {

   private static final long serialVersionUID = 5298756163814063425L;
   public static final String SIMPLE = "simple";
   public static final String BYTE_ARRAY = "byteArray";
   public static final String COMPLEX = "complex";
   public static final String DURATION = "Duration";
   public static final String ENUMERATION = "Enumeration";
   public static final String GROUP = "group";
   public static final String LAZY = "lazy";
   public static final String PARAMETERIZED = "Parameterized";
   public static final String QNAME = "qname";
   public static final String ROOT = "root";
   public static final String XML_GREGORIAN_CAL = "XMLGregorianCalendar";

   protected Object id;
   protected String name;
   protected String kind; // simple, complex, array, date, etc
   protected boolean expanded = false;

   protected boolean nil; //whether this elements has the attribute xsi:nil set to "true"
   protected boolean nillable = true; //for primitives and explicitly not nillable elements
   private boolean removable = false; // to be used on array elements

   private WiseTreeElement parent;

   protected Type classType;

   protected WiseTreeElement() {
      super();
   }

   protected WiseTreeElement(boolean isLeaf) {
      super(isLeaf);
   }

   public String getKind() {
      return this.kind;
   }

   public void setKind(String kind) {
      this.kind = kind;
   }

   public boolean isRemovable() {
      return removable;
   }

   public void setRemovable(boolean removable) {
      this.removable = removable;
   }

   public Type getClassType() {
      return classType;
   }

   public boolean isTypeBoolean() {
      return "Boolean".equalsIgnoreCase(this.getType());
   }

   public void setClassType(Type classType) {
      this.classType = classType;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public boolean isNil() {
      return nil;
   }

   public void setNil(boolean nil) {
      this.nil = nil;
   }

   public Object getId() {
      return id;
   }

   public boolean isNillable() {
      return nillable;
   }

   public void setNillable(boolean nillable) {
      this.nillable = nillable;
   }

   public boolean isNotNillable() {
      return !this.isNillable();
   }

   public boolean isNotNil() {
      return !this.isNil();
   }

   public void setNotNil(boolean notNil) {
      this.setNil(!notNil);
   }

   public boolean isExpanded() {
      return expanded;
   }

   public void setExpanded(boolean expanded) {
      this.expanded = expanded;
   }

   public String getType() {
      if (this.classType instanceof ParameterizedType) {
         return ((Class<?>) ((ParameterizedType) this.classType).getRawType()).getSimpleName();
      } else {
         return ((Class<?>) this.classType).getSimpleName();
      }
   }

   public WiseTreeElement getParent() {
      return parent;
   }

   public void setParent(WiseTreeElement parent) {
      this.parent = parent;
   }

   /**
    * * Abstract method ***
    */

   public void addChild(Object key, TreeNode child) {
      super.addChild(key, child);
      if (child instanceof WiseTreeElement) {
         ((WiseTreeElement) child).setParent(this);
      }
   }

   public void insertChild(int idx, Object key, TreeNode child) {
      super.insertChild(idx, key, child);
      if (child instanceof WiseTreeElement) {
         ((WiseTreeElement) child).setParent(this);
      }
   }

   public void removeChild(Object key) {
      TreeNode child = getChild(key);
      if (child instanceof WiseTreeElement) {
         ((WiseTreeElement) child).setParent(null);
      }
      super.removeChild(key);
   }

   /**
    * Every WiseTreeElement must be cloneable; this is required to handle
    * element's add and removal into/from arrays and collections.
    */
   @Override
   public abstract WiseTreeElement clone();

   /**
    * This is required to convert a tree element into the corresponding object
    * instance.
    *
    * @return The object corresponding to this element
    */
   public abstract Object toObject();

}
