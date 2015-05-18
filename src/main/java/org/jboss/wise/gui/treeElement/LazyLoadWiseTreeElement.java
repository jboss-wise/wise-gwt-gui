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

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

/**
 * This tree element is for storing lazy load references to other elements;
 * required because in some uncommon scenario, the parameter trees might
 * actually be graphs... (the actually passed data won't be a graph, but
 * the type model can have cycles in it)
 * 
 * @author Alessio Soldano, alessio.soldano@jboss.com
 */
public class LazyLoadWiseTreeElement extends WiseTreeElement {

    private static final long serialVersionUID = 1L;
    
    private boolean resolved = false;
    protected Map<Type, WiseTreeElement> treeTypesMap;

    private LazyLoadWiseTreeElement() {
	this.kind = LAZY;
	this.id = IDGenerator.nextVal();
    }

    public LazyLoadWiseTreeElement(Type classType, String name, Map<Type, WiseTreeElement> treeTypesMap) {
	this.kind = LAZY;
	this.id = IDGenerator.nextVal();
	this.classType = classType;
	this.nil = false;
	this.name = name;
	this.treeTypesMap = treeTypesMap;
    }

    public WiseTreeElement clone() {
	LazyLoadWiseTreeElement element = new LazyLoadWiseTreeElement();
	element.setName(this.name);
	element.setNil(this.nil);
	element.setClassType(this.classType);
	element.setRemovable(this.isRemovable());
	element.setNillable(this.isNillable());
	element.setResolved(false); //copy into an unresolved element and do not copy child
	element.setTreeTypesMap(this.treeTypesMap);
	return element;
    }

    public void resolveReference() {
	if (!isResolved()) {
	    WiseTreeElement ref = treeTypesMap.get(this.classType);
            WiseTreeElement component = (WiseTreeElement) ref.clone();
            component.setName(this.getName());
            addChild(component.getId(), component);
            setResolved(true);
	}
    }

    public Object toObject() {
	Iterator<Object> keyIt = this.getChildrenKeysIterator();
	return keyIt.hasNext() ? ((WiseTreeElement)this.getChild(keyIt.next())).toObject() : null;
    }
    
    public boolean isResolved() {
	return resolved;
    }
    
    public void setResolved(boolean resolved) {
	this.resolved = resolved;
    }
    
    public Map<Type, WiseTreeElement> getTreeTypesMap() {
        return treeTypesMap;
    }

    public void setTreeTypesMap(Map<Type, WiseTreeElement> treeTypesMap) {
        this.treeTypesMap = treeTypesMap;
    }
}
