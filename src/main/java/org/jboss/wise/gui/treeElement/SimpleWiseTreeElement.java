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

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;

import org.jboss.wise.core.exception.WiseRuntimeException;
import org.jboss.wise.core.utils.JavaUtils;

import sun.misc.BASE64Encoder;

/**
 * A WiseTreeElement for simple types, like primitives and their corresponding
 * classes. This is to be used for types having a simple value.
 *
 * @author Alessio Soldano, alessio.soldano@jboss.com
 */
public class SimpleWiseTreeElement extends WiseTreeElement {

   private static final long serialVersionUID = 1L;

   protected String value;

   public SimpleWiseTreeElement() {
      super(true);
      this.kind = SIMPLE;
      this.id = IDGenerator.nextVal();
   }

   public SimpleWiseTreeElement(Class<?> classType, String name, String value) {
      super(true);
      this.kind = SIMPLE;
      this.id = IDGenerator.nextVal();
      this.classType = classType;
      this.name = name;
      init(classType, value);
   }

   /**
    * Returns the String value of the instance corresponding to this element.
    *
    * @return The value
    */
   public String getValue() {
      return value;
   }

   /**
    * This is the same as getValue except only the first 60 characters are
    * considered. This is needed to preview invocation results.
    *
    * @return The substring(0,60) of the value
    */
   public String getShortValue() {
      if (getValue() == null) {
         return null;
      }
      if (getValue().length() <= 60) {
         return getValue();
      }
      return getValue().substring(0, 60) + "...";
   }

   /**
    * This is the same as getValue except it double encode (BASE64 + URL) the
    * value.
    *
    * @return The encoded value
    */
   public String getLongValue() {
      if (getValue() == null) {
         return null;
      }
      BASE64Encoder enc = new BASE64Encoder();
      try {
         return URLEncoder.encode(enc.encode(getValue().getBytes()), "UTF-8");
      } catch (UnsupportedEncodingException e) {
         throw new WiseRuntimeException(e);
      }
   }

   public void setValue(String value) {
      this.value = value;
   }

   public WiseTreeElement clone() {
      SimpleWiseTreeElement element = new SimpleWiseTreeElement();
      element.setName(this.name);
      element.setNil(this.nil);
      element.setClassType(this.classType);
      element.setRemovable(this.isRemovable());
      element.setNillable(this.isNillable());
      element.init((Class<?>) this.classType, null);
      return element;
   }

   /**
    * Gets the value for this element parsing a given object instance. This is
    * to be used to set the element value after the service invocation.
    *
    * @param obj
    */
   public void parseObject(Object obj) {
      this.setValue(obj == null ? null : obj.toString());
      this.nil = (obj == null && nillable);
   }

   /**
    * Make sure this element can't be nill and set the default value
    * (this is to be used e.g. for main RPC/Lit parameters)
    */
   public void enforceNotNillable() {
      this.nillable = false;
      this.nil = false;
      this.value = getDefaultValue((Class<?>) classType);
   }

   private void init(Class<?> classType, String value) {
      // primitive are not nillable, thus they can't be nil or have a null value
      this.value = (value == null && classType.isPrimitive()) ? getDefaultValue(classType) : value;
      this.nillable = !classType.isPrimitive();
      this.nil = (value == null && nillable);
   }

   private static String getDefaultValue(Class<?> cl) {
      if (cl.isPrimitive()) {
         cl = JavaUtils.getWrapperType(cl);
      }
      if ("java.lang.String".equalsIgnoreCase(cl.getName())) {
         return "";
      } else if ("java.lang.Boolean".equalsIgnoreCase(cl.getName())) {
         return "false";
      } else if ("java.lang.Byte".equalsIgnoreCase(cl.getName())) {
         return "0";
      } else if ("java.lang.Character".equalsIgnoreCase(cl.getName())) {
         return "";
      } else if ("java.lang.Double".equalsIgnoreCase(cl.getName())) {
         return "0.0";
      } else if ("java.lang.Float".equalsIgnoreCase(cl.getName())) {
         return "0.0";
      } else if ("java.lang.Integer".equalsIgnoreCase(cl.getName())) {
         return "0";
      } else if ("java.lang.Long".equalsIgnoreCase(cl.getName())) {
         return "0";
      } else if ("java.lang.Short".equalsIgnoreCase(cl.getName())) {
         return "0";
      } else if ("java.math.BigDecimal".equalsIgnoreCase(cl.getName())) {
         return "0.0";
      } else if ("java.math.BigInteger".equalsIgnoreCase(cl.getName())) {
         return "0";
      } else if ("java.lang.Object".equalsIgnoreCase(cl.getName())) {
         return "";
      } else {
         throw new WiseRuntimeException("Class type not supported: " + cl);
      }
   }

   public Object toObject() {
      if (value == null) {
         return null;
      }

      if (this.isNil()) {
         return null;
      }

      Class<?> cl = (Class<?>) classType;
      if (cl.isPrimitive()) {
         cl = JavaUtils.getWrapperType(cl);
      }
      if ("java.lang.String".equalsIgnoreCase(cl.getName())) {
         return new String(value);
      } else if ("java.lang.Boolean".equalsIgnoreCase(cl.getName())) {
         return new Boolean(value);
      } else if ("java.lang.Byte".equalsIgnoreCase(cl.getName())) {
         return new Byte(value);
      } else if ("java.lang.Character".equalsIgnoreCase(cl.getName())) {
         return new Character(value.charAt(0));
      } else if ("java.lang.Double".equalsIgnoreCase(cl.getName())) {
         return new Double(value);
      } else if ("java.lang.Float".equalsIgnoreCase(cl.getName())) {
         return new Float(value);
      } else if ("java.lang.Integer".equalsIgnoreCase(cl.getName())) {
         return new Integer(value);
      } else if ("java.lang.Long".equalsIgnoreCase(cl.getName())) {
         return new Long(value);
      } else if ("java.lang.Short".equalsIgnoreCase(cl.getName())) {
         return new Short(value);
      } else if ("java.math.BigDecimal".equalsIgnoreCase(cl.getName())) {
         return BigDecimal.valueOf(Double.parseDouble(value));
      } else if ("java.math.BigInteger".equalsIgnoreCase(cl.getName())) {
         return BigInteger.valueOf(Long.parseLong(value));
      } else if ("java.lang.Object".equalsIgnoreCase(cl.getName())) {
         return (Object) value;
      } else {
         throw new WiseRuntimeException("Class type not supported: " + cl);
      }
   }
}
