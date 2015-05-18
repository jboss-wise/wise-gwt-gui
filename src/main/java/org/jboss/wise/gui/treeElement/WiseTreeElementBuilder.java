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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.ws.Holder;

import org.jboss.logging.Logger;
import org.jboss.wise.core.client.WSDynamicClient;
import org.jboss.wise.core.exception.WiseRuntimeException;
import org.jboss.wise.core.utils.ReflectionUtils;

/**
 * Builds WiseTreeElements given a type or class
 * <p/>
 * (Type/Class) + [Object] ===> Tree
 *
 * @author alessio.soldano@jboss.com
 */
public class WiseTreeElementBuilder {

   private static Logger logger = Logger.getLogger(WiseTreeElementBuilder.class);

   private final WSDynamicClient client;
   private final boolean request;
   private final boolean trace;

   public WiseTreeElementBuilder(WSDynamicClient client, boolean request) {
      this.client = client;
      this.request = request;
      this.trace = logger.isTraceEnabled();
   }

   public WiseTreeElement buildTreeFromType(Type type, String name, Object value, boolean nillable) {
      return buildTreeFromType(type, name, value, nillable, null, null, new HashMap<Type, WiseTreeElement>(), new HashSet<Type>());
   }

   private WiseTreeElement buildTreeFromType(Type type,
                                             String name,
                                             Object obj,
                                             boolean nillable,
                                             Class<?> scope,
                                             String namespace,
                                             Map<Type, WiseTreeElement> typeMap,
                                             Set<Type> stack) {
      //System.out.println("AAA buildTreeFromType: type: "
      //   + ((type == null)? "NULL" : type.toString()) + "  name: "
      //   + ((name == null)? "NULL" : name)
      //   + "  obj: " + ((obj== null) ? "NULL" : obj.toString()));
      if (trace)
         logger.trace("=> Converting parameter '" + name + "', type '" + type + "'");
      if (type instanceof ParameterizedType) {
         ParameterizedType pt = (ParameterizedType) type;
         return this.buildParameterizedType(pt, name, obj, scope, namespace, typeMap, stack);
      } else {
         return this.buildFromClass((Class<?>) type, name, obj, nillable, typeMap, stack);

      }
   }

   @SuppressWarnings("rawtypes")
   private WiseTreeElement buildParameterizedType(ParameterizedType pt,
                                                  String name,
                                                  Object obj,
                                                  Class<?> scope,
                                                  String namespace,
                                                  Map<Type, WiseTreeElement> typeMap,
                                                  Set<Type> stack) {
      //System.out.println("AAA buildParameterizedType: type: "
      //   + ((pt == null)? "NULL" : pt.getActualTypeArguments()[0].toString())
      //   + "  name: " + ((name == null)? "NULL" : name)
      //   + "  obj: " + ((obj == null) ? "NULL" : obj.toString())
      //   + "  rawType: " + ((pt == null)? "NULL" : pt.getRawType().toString()));
      Type firstTypeArg = pt.getActualTypeArguments()[0];
      if (Collection.class.isAssignableFrom((Class<?>) pt.getRawType())) {
         GroupWiseTreeElement group;
         if (obj != null || request) {
            WiseTreeElement prototype = this.buildTreeFromType(firstTypeArg, name, null, true, null, null, typeMap, stack);
            group = new GroupWiseTreeElement(pt, name, prototype);
            //System.out.println("AAA buildParameterizedType: group created");
            if (obj != null) {
               for (Object o : (Collection) obj) {
                  //System.out.println("AAA buildParameterizedType: group child: " + o.toString());
                  group.addChild(IDGenerator.nextVal(), this.buildTreeFromType(firstTypeArg, name, o, true, null, null, typeMap, stack));
               }
            }
         } else {
            //System.out.println("AAA buildParameterizedType: group not children");
            group = new GroupWiseTreeElement(pt, name, null);
         }
         return group;
      } else {
         if (obj != null && obj instanceof JAXBElement) {
            obj = ((JAXBElement) obj).getValue();
         } else if (obj != null && obj instanceof Holder) {
            obj = ((Holder) obj).value;
         }
         //System.out.println("AAA buildParameterizedType: create parameterized obj");
         WiseTreeElement element = this.buildTreeFromType(firstTypeArg, name, obj, true, null, null, typeMap, stack);
         ParameterizedWiseTreeElement parameterized = new ParameterizedWiseTreeElement(pt, (Class<?>) pt.getRawType(), name, client, scope, namespace);
         parameterized.addChild(element.getId(), element);
         return parameterized;
      }
   }

   private WiseTreeElement buildFromClass(Class<?> cl,
                                          String name,
                                          Object obj,
                                          boolean nillable,
                                          Map<Type, WiseTreeElement> typeMap,
                                          Set<Type> stack) {

      //System.out.println("AAA buildFromClass: class: "
      //   + ((cl == null)? "NULL" : cl.toString()) + "  name: "
      //   + ((name == null)? "NULL" : name)
      //   + "  obj: " + ((obj == null) ? "NULL" : obj.toString()));
      if (cl.isArray()) {
         if (trace)
            logger.trace("* array, component type: " + cl.getComponentType());
         if (byte.class.equals(cl.getComponentType())) {
            ByteArrayWiseTreeElement element = new ByteArrayWiseTreeElement(cl, name, null);
            if (obj != null) {
               element.parseObject(obj);
            }
            //System.out.println("AAA buildFromClass: ByteArray created" );
            return element;
         }
         throw new WiseRuntimeException("Converter doesn't support this Object[] yet.");
      }

      if (isSimpleType(cl, client)) {
         if (trace)
            logger.trace("* simple");
         SimpleWiseTreeElement element = SimpleWiseTreeElementFactory.create(cl, name, obj);
         if (!nillable) {
            element.enforceNotNillable();
         }
         //System.out.println("AAA buildFromClass: SimpleType created" );
         return element;
      } else { // complex
         if (request && stack.contains(cl)) {
            if (trace)
               logger.trace("* lazy");
            //System.out.println("AAA buildFromClass: LazyLoad created" );
            return new LazyLoadWiseTreeElement(cl, name, typeMap);
         }

         if (trace)
            logger.trace("* complex");
         //System.out.println("AAA buildFromClass: ComplexWise created" );
         ComplexWiseTreeElement complex = new ComplexWiseTreeElement(cl, name);
         if (request) {
            stack.add(cl);
         }
         for (Field field : ReflectionUtils.getAllFields(cl)) {
            XmlElement elemAnnotation = field.getAnnotation(XmlElement.class);
            XmlElementRef refAnnotation = field.getAnnotation(XmlElementRef.class);
            String fieldName = null;
            String namespace = null;
            if (elemAnnotation != null && !elemAnnotation.name().startsWith("#")) {
               fieldName = elemAnnotation.name();
            }
            if (refAnnotation != null) {
               fieldName = refAnnotation.name();
               namespace = refAnnotation.namespace();
            }
            final String xmlName = fieldName;
            if (fieldName == null) {
               fieldName = field.getName();
            }
            //String fieldName = (annotation != null && !annotation.name().startsWith("#")) ? annotation.name() : field.getName();
            Object fieldValue = null;
            if (obj != null) {
               try {
                  Method getter = cl.getMethod(ReflectionUtils.getGetter(field, xmlName), (Class[]) null);
                  fieldValue = getter.invoke(obj, (Object[]) null);
               } catch (Exception e) {
                  throw new WiseRuntimeException("Error calling getter method for field " + field, e);
               }
            }
            //System.out.println("AAA buildFromClass: Element created" );
            WiseTreeElement element = this.buildTreeFromType(field.getGenericType(), fieldName, fieldValue, true, cl, namespace, typeMap, stack);
            complex.addChild(element.getId(), element);
         }
         if (request) {
            stack.remove(cl);
            typeMap.put(cl, complex.clone());
         }
         if (!nillable) {
            complex.setNillable(false);
         }
         return complex;
      }
   }

   private static boolean isSimpleType(Class<?> cl, WSDynamicClient client) {
      return cl.isEnum() || cl.isPrimitive() || client.getClassLoader() != cl.getClassLoader();
   }
}
