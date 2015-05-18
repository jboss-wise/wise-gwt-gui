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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.jboss.wise.core.exception.WiseRuntimeException;

/**
 * A simple tree element to handle durations.
 * 
 * @author alessio.soldano@jboss.com
 * 
 */
public class DurationWiseTreeElement extends SimpleWiseTreeElement {

    private static final long serialVersionUID = 5492389675960954725L;

    public DurationWiseTreeElement() {
	this.kind = DURATION;
    }

    public DurationWiseTreeElement(Class<?> classType, String name, String value) {
	this.kind = DURATION;
	this.classType = classType;
	this.nil = value == null;
	this.name = name;
	this.value = value;
    }

    @Override
    public WiseTreeElement clone() {
	DurationWiseTreeElement element = new DurationWiseTreeElement();
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
	this.setValue(obj != null ? ((Duration) obj).toString() : null);
	this.nil = (obj == null && nillable);
    }
    
    @Override
    public void enforceNotNillable() {
	this.nillable = false;
	this.nil = false;
	this.value = "0";
    }

    @Override
    public Object toObject() {
	if (isNil()) return null;
	Object result = null;
	try {
	    result = DatatypeFactory.newInstance().newDuration(Long.parseLong(value));
	} catch (DatatypeConfigurationException e) {
	    throw new WiseRuntimeException("Error converting element to object, type format error?", e);
	}
	return result;
    }
}