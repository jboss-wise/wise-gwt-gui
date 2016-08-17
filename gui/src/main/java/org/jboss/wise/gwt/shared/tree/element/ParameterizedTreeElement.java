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

/**
 * User: rsearls
 * Date: 4/27/15
 */
public class ParameterizedTreeElement extends SimpleTreeElement {

    private static final long serialVersionUID = -4606263589486412824L;

    public ParameterizedTreeElement() {
        kind = TreeElement.PARAMETERIZED;
    }

    @Override public TreeElement clone() {

        ParameterizedTreeElement clone = new ParameterizedTreeElement();
        clone.setKind(getKind());
        clone.setName(getName());
        clone.setClassType(getClassType());

        for (TreeElement child : getChildren()) {
            clone.addChild(child.clone());
        }

        return clone;

    }
}
