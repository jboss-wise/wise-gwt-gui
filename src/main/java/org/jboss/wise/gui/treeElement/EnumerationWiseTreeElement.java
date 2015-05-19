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
import java.util.HashMap;
import java.util.Map;

import org.jboss.wise.core.exception.WiseRuntimeException;

/**
 * A simple tree element to handle enumerations.
 * 
 * @author alessio.soldano@jboss.com
 */
public class EnumerationWiseTreeElement extends SimpleWiseTreeElement {

    private static final long serialVersionUID = 5492389675960954725L;

    public EnumerationWiseTreeElement() {
	this.kind = ENUMERATION;
    }

    public EnumerationWiseTreeElement(Class<?> classType, String name, String value) {
	this.kind = ENUMERATION;
	this.classType = classType;
	this.nil = value == null;
	this.name = name;
	this.value = value;
    }

    @Override
    public WiseTreeElement clone() {
	EnumerationWiseTreeElement element = new EnumerationWiseTreeElement();
	element.setName(this.name);
	element.setNil(this.nil);
	element.setClassType(this.classType);
	element.setValue(this.value);
	element.setRemovable(this.isRemovable());
	element.setNillable(this.isNillable());
	return element;
    }
    
    @Override
    public void enforceNotNillable() {
	this.nillable = false;
	this.nil = false;
	this.value = getValidValue().keySet().iterator().next();
    }

    @Override
    public void parseObject(Object obj) {
	if (obj != null) {
	    try {
		Method method = obj.getClass().getMethod("value");
		this.value = (String) method.invoke(obj);
		this.nil = value == null;
	    } catch (Exception e) {
		throw new WiseRuntimeException("Type format error", e);
	    }
	} else {
	    this.setValue(null);
	}
    }

    /**
     * Gets the valid values of the enumeration corresponding to this tree
     * element.
     * 
     * @return
     */
    public Map<String, String> getValidValue() {
	HashMap<String, String> returnMap = new HashMap<String, String>();
	for (Object obj : ((Class<?>) classType).getEnumConstants()) {
	    String valueOfEnum;
	    try {
		Method method = obj.getClass().getMethod("value");
		valueOfEnum = (String) method.invoke(obj);
		returnMap.put(valueOfEnum, valueOfEnum);
	    } catch (Exception e) {
		throw new WiseRuntimeException("Type format error", e);
	    }
	}
	return returnMap;
    }

    @Override
    public Object toObject() {
	Class<?> cl = (Class<?>) classType;
	try {
	    if (this.isNil()) {
		return null;
	    }
	    Method method = cl.getMethod("fromValue", String.class);
	    Object obj = method.invoke(null, value);
	    return obj;
	} catch (Exception e) {
	    throw new WiseRuntimeException("Type format error", e);
	}
    }
}