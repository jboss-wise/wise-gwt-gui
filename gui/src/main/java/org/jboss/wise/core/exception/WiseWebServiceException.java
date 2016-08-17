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
package org.jboss.wise.core.exception;

import java.io.Serializable;

/**
 * The GWT module and wise-core contain duplicate copies of this class
 * because this exception is passed from wise-core to the GWT code.
 * GWT requires a copy in order to generate the corresponding javascript.
 * wise-core requires a copy because it is the source of the exception.
 * <p>
 * User: rsearls
 * Date: 6/23/15
 */
public class WiseWebServiceException extends Exception implements Serializable {

    private static final long serialVersionUID = 3803266852951478259L;
    private String message = "";

    public WiseWebServiceException() {

    }

    public WiseWebServiceException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
