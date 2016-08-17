/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.wise.gwt.shared.tree.element;

import java.util.ArrayList;
import java.util.List;

/**
 * User: rsearls
 * Date: 3/21/15
 */
public class GroupTreeElement extends org.jboss.wise.gwt.shared.tree.element.TreeElement {

    private static final long serialVersionUID = 5299473227860294202L;
    private String rawType;
    private org.jboss.wise.gwt.shared.tree.element.TreeElement protoType;
    private List<org.jboss.wise.gwt.shared.tree.element.TreeElement> valueList = new ArrayList<org.jboss.wise.gwt.shared.tree.element.TreeElement>();

    public GroupTreeElement() {
        kind = org.jboss.wise.gwt.shared.tree.element.TreeElement.GROUP;
    }

    public List<org.jboss.wise.gwt.shared.tree.element.TreeElement> getValueList() {

        return valueList;
    }

    public void addValue(org.jboss.wise.gwt.shared.tree.element.TreeElement value) {

        valueList.add(value);
    }

    public String getRawType() {

        return rawType;
    }

    public void setRawType(String rawType) {

        this.rawType = rawType;
    }

    public org.jboss.wise.gwt.shared.tree.element.TreeElement getProtoType() {

        return protoType;
    }

    public void setProtoType(org.jboss.wise.gwt.shared.tree.element.TreeElement protoType) {

        this.protoType = protoType;
    }

    @Override public org.jboss.wise.gwt.shared.tree.element.TreeElement clone() {
        GroupTreeElement clone = new GroupTreeElement();
        clone.setKind(getKind());
        clone.setName(getName());
        clone.setClassType(getClassType());
        clone.setProtoType(protoType.clone());
        return clone;
    }
}
