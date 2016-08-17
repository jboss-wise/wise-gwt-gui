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
 * Date: 3/23/15
 */
public class EnumerationTreeElement extends SimpleTreeElement {

    private static final long serialVersionUID = 4969362839287678329L;
    private List<String> enumValues = new ArrayList<String>();

    public EnumerationTreeElement() {
        kind = TreeElement.ENUMERATION;
    }

    public List<String> getEnumValues() {

        return enumValues;
    }

    public void addEnumValue(String value) {

        enumValues.add(value);
    }

    @Override public TreeElement clone() {
        EnumerationTreeElement clone = new EnumerationTreeElement();
        clone.setKind(getKind());
        clone.setName(getName());
        clone.setClassType(getClassType());
        clone.setValue(getValue());
        clone.getEnumValues().addAll(enumValues);
        return clone;
    }
}
