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
 * Date: 3/26/15
 */
public class RequestResponse implements Serializable {
    private static final long serialVersionUID = -5275278090812046815L;
    private String operationFullName;
    private TreeElement treeElement;
    private String responseMessage;
    private String errorMessage;

    public String getOperationFullName() {

        return operationFullName;
    }

    public void setOperationFullName(String operationFullName) {

        this.operationFullName = operationFullName;
    }

    public TreeElement getTreeElement() {

        return treeElement;
    }

    public void setTreeElement(TreeElement treeElement) {

        this.treeElement = treeElement;
    }

    public String getResponseMessage() {

        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {

        this.responseMessage = responseMessage;
    }

    public String getErrorMessage() {

        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {

        this.errorMessage = errorMessage;
    }
}
