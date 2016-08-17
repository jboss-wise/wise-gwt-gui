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
import java.util.LinkedList;
import java.util.List;

/**
 * User: rsearls
 * Date: 3/19/15
 */
public abstract class TreeElement implements Serializable {

    public static final String SIMPLE = "simple";
    public static final String BYTE_ARRAY = "byteArray";
    public static final String COMPLEX = "complex";
    public static final String DURATION = "Duration";
    public static final String ENUMERATION = "Enumeration";
    public static final String GROUP = "group";
    public static final String LAZY = "lazy";
    public static final String PARAMETERIZED = "Parameterized";
    public static final String QNAME = "qname";
    public static final String ROOT = "root";
    public static final String XML_GREGORIAN_CAL = "XMLGregorianCalendar";
    private static final long serialVersionUID = -6123163243263043337L;
    protected String id; // hashcode of *WiseTreeElement mapped to
    protected String name;
    protected String kind;
    protected String classType;
    protected List<TreeElement> children = new LinkedList<TreeElement>();
    protected boolean nil = false; //whether this element has the attribute xsi:nil set to "true"
    protected boolean nillable = false;  //whether this element can have the xsi:nill attribute set

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getKind() {

        return this.kind;
    }

    public void setKind(String kind) {

        this.kind = kind;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getClassType() {

        return classType;
    }

    public void setClassType(String classType) {

        this.classType = classType;
    }

    public void addChild(TreeElement child) {

        children.add(child);
    }

    public List<TreeElement> getChildren() {

        return children;
    }

    public boolean isNil() {
        return nil;
    }

    public void setNil(boolean nil) {
        this.nil = nil;
    }

    public boolean isNillable() {
        return nillable;
    }

    public void setNillable(boolean nillable) {
        this.nillable = nillable;
    }

    public String getCleanClassName(String src) {
        String tmpStr = src;
        int index = src.trim().lastIndexOf(" ");
        if (index > -1) {
            tmpStr = src.substring(index + 1);
        }
        return tmpStr;
    }

    public abstract TreeElement clone();
}

