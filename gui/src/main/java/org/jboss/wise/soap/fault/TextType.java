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
package org.jboss.wise.soap.fault;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import java.util.Locale;

/**
 * <pre>
 *    &lt;env:Text xml:lang="en">Sender Timeout</env:Text>
 * </pre>
 */
@XmlType(name = "TextType", namespace = "http://www.w3.org/2003/05/soap-envelope") public class TextType {
    private @XmlValue String text;

    @XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace", required = true) private String lang;

    public TextType() {
    }

    public TextType(String text) {
        this.text = text;
        this.lang = Locale.getDefault().getLanguage();
    }

    public String getText() {
        return text;
    }
}
