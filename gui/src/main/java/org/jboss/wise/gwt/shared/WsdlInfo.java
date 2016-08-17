/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.wise.gwt.shared;

import java.io.Serializable;

/**
 * User: rsearls
 * Date: 3/5/15
 */
public class WsdlInfo implements Serializable {
    private static final long serialVersionUID = -7090099493396706144L;
    public String wsdl;
    public String user;
    public String password;

    public WsdlInfo() {

    }

    public WsdlInfo(String wsdl, String user, String password) {

        this.wsdl = wsdl;
        this.user = user;
        this.password = password;
    }

    public String getWsdl() {

        return wsdl;
    }

    public void setWsdl(String wsdl) {

        this.wsdl = wsdl;
    }

    public String getUser() {

        return user;
    }

    public void setUser(String user) {

        this.user = user;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }
}
