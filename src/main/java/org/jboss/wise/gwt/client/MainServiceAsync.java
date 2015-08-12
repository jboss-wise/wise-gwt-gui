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
package org.jboss.wise.gwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import java.util.List;
import org.jboss.wise.gwt.shared.Service;
import org.jboss.wise.gwt.shared.tree.element.RequestResponse;
import org.jboss.wise.gwt.shared.tree.element.TreeElement;
import org.jboss.wise.gwt.shared.WsdlAddress;
import org.jboss.wise.gwt.shared.WsdlInfo;

/**
 * User: rsearls
 * Date: 3/8/15
 */
public interface MainServiceAsync {
   public void getAddressDetails(AsyncCallback<ArrayList<WsdlAddress>> callback);
   public void getAddress(String id, AsyncCallback<WsdlAddress> callback);
   public void getEndpoints(WsdlInfo wsdlInfo, AsyncCallback<List<Service>> callback);
   public void getEndpointReflection(String id, AsyncCallback<RequestResponse> callback);
   public void getRequestPreview(TreeElement rootTreeElement, AsyncCallback<String> callback);
   public void getPerformInvocationOutputTree(TreeElement rootTreeElement, WsdlInfo wsdlInfo, AsyncCallback<RequestResponse> callback);
   public void isValidURL(String url, AsyncCallback<Boolean> callback);
}
