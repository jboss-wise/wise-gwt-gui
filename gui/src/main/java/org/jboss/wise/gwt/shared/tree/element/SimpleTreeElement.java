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

import java.io.Serializable;

/**
 * User: rsearls
 * Date: 3/27/15
 */
public class SimpleTreeElement extends TreeElement implements Serializable {
    private static final long serialVersionUID = -4549616769681548027L;
    protected String value;

    public SimpleTreeElement() {
        kind = TreeElement.SIMPLE;
    }

    public String getValue() {

        return value;
    }

    public void setValue(String value) {

        if (value == null || value.isEmpty()) {
            setNil(true);
        } else {
            setNil(false);
        }
        this.value = value;
    }

    @Override public TreeElement clone() {
        SimpleTreeElement clone = new SimpleTreeElement();
        clone.setKind(getKind());
        clone.setName(getName());
        clone.setClassType(getClassType());
        clone.setValue(getValue());
        clone.setNil(isNil());
        clone.setNillable(isNillable());

        for (TreeElement child : getChildren()) {
            clone.addChild(child.clone());
        }

        return clone;
    }
}
