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

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.jboss.wise.core.exception.WiseRuntimeException;

/**
 * A simple tree element to handle calendars.
 * 
 * @author alessio.soldano@jboss.com
 */
public class XMLGregorianWiseTreeElement extends SimpleWiseTreeElement {

    private static final long serialVersionUID = 5492389675960954725L;

    private Date valueDate;

    public XMLGregorianWiseTreeElement() {
	this.kind = XML_GREGORIAN_CAL;
    }

    public XMLGregorianWiseTreeElement(Class<?> classType, String name, String value) {
	this.kind = XML_GREGORIAN_CAL;
	this.classType = classType;
	this.nil = value == null;
	this.name = name;
	this.value = value;
    }

    @Override
    public WiseTreeElement clone() {
	XMLGregorianWiseTreeElement element = new XMLGregorianWiseTreeElement();
	element.setName(this.name);
	element.setNil(this.nil);
	element.setClassType(this.classType);
	element.setValue(this.value);
	element.setRemovable(this.isRemovable());
	element.setNillable(this.isNillable());
	element.setValueDate(this.valueDate);
	return element;
    }

    @Override
    public void parseObject(Object obj) {
	if (obj != null) {
	    this.setValueDate(((XMLGregorianCalendar) obj).toGregorianCalendar().getTime());
	    this.setValue(((XMLGregorianCalendar) obj).toString());
	} else {
	    this.setValueDate(null);
	    this.setValue(null);
	}
	this.nil = obj == null;
    }
    
    @Override
    public void enforceNotNillable() {
	this.nillable = false;
	this.nil = false;
	this.value = new Date().toString();
    }

    @Override
    public Object toObject() {
	Object result = null;

	if (!(this.isNil() || this.getValueDate() == null)) {
	    // TODO!! consider creating a validator to prevent empty string
	    // for dates,
	    // as empy string shouldn't be converted to null, we should
	    // throw exception
	    // instead...
	    try {
		GregorianCalendar gregCal = new GregorianCalendar();
		gregCal.setTime(this.getValueDate());
		result = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCal);
	    } catch (DatatypeConfigurationException e) {
		throw new WiseRuntimeException("Type format error", e);
	    }
	}
	return result;
    }

    public Date getValueDate() {
	return valueDate;
    }

    public void setValueDate(Date valueDate) {
	this.valueDate = valueDate;
    }
}
