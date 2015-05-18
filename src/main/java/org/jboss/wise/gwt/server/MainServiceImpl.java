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
package org.jboss.wise.gwt.server;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jboss.wise.gwt.client.MainService;
import org.jboss.wise.shared.GWTClientConversationBean;
import org.jboss.wise.gwt.shared.Service;
import org.jboss.wise.gwt.shared.tree.element.GroupTreeElement;
import org.jboss.wise.gwt.shared.tree.element.RequestResponse;
import org.jboss.wise.gwt.shared.tree.element.SimpleTreeElement;
import org.jboss.wise.gwt.shared.tree.element.TreeElement;
import org.jboss.wise.gwt.shared.WsdlAddress;
import org.jboss.wise.gwt.shared.WsdlInfo;

/**
 * User: rsearls
 * Date: 3/8/15
 */
@SuppressWarnings("serial")
public class MainServiceImpl extends RemoteServiceServlet implements
   MainService {

   private final HashMap<String, WsdlAddress> address = new HashMap<String, WsdlAddress>();
   private ArrayList<WsdlAddress> wsdlAddress = new ArrayList<WsdlAddress>();
   private GWTClientConversationBean gwtClientConversationBean;

   public MainServiceImpl() {

      gwtClientConversationBean = new GWTClientConversationBean();
      initAddress();
   }

   private void initAddress() {
      List<String> wsdlList = gwtClientConversationBean.getWsdlList();
      for (int i = 0; i < wsdlList.size(); ++i) {
         WsdlAddress detail = new WsdlAddress(String.valueOf(i), wsdlList.get(i));
         wsdlAddress.add(detail);
         address.put(detail.getId(), detail);
      }
   }


   public ArrayList<WsdlAddress> getAddressDetails() {

      return wsdlAddress;
   }

   public WsdlAddress getAddress(String id) {

      return address.get(id);
   }

   public ArrayList<Service> getEndpoints(WsdlInfo wsdlInfo) {

      ArrayList<Service> endpointList = new ArrayList<Service>();
      if (wsdlInfo != null) {
         gwtClientConversationBean.setWsdlUser(wsdlInfo.getUser());
         gwtClientConversationBean.setWsdlPwd(wsdlInfo.getPassword());
         gwtClientConversationBean.setWsdlUrl(wsdlInfo.getWsdl());
         gwtClientConversationBean.readWsdl();
         List<Service> serviceList = gwtClientConversationBean.getServices();
         endpointList.addAll(serviceList);
      } else {
         Window.alert("URL information not specified");
      }
      return endpointList;
   }

   public RequestResponse getEndpointReflection(String id) {

      if (id != null) {
         gwtClientConversationBean.setCurrentOperation(id);
         return gwtClientConversationBean.parseOperationParameters(id);
      }
      return null;
   }

   public String getRequestPreview(TreeElement rootTreeElement) {

      return gwtClientConversationBean.generateRequestPreview(rootTreeElement);
   }

   public RequestResponse getPerformInvocationOutputTree(TreeElement rootTreeElement, WsdlInfo wsdlInfo) {

      gwtClientConversationBean.setInvocationUrl(wsdlInfo.getWsdl());
      gwtClientConversationBean.setInvocationUser(wsdlInfo.getUser());
      gwtClientConversationBean.setInvocationPwd(wsdlInfo.getPassword());

      return gwtClientConversationBean.performInvocation(rootTreeElement);
   }

   private String dumpTree(TreeElement root) {

      StringBuilder sb = new StringBuilder();

      if (root == null) {
         sb.append("root is NULL \n");
      } else {
         sb.append("--- \n");
         sb.append("root: arg name: " + root.getName()
            + "  classType: " + root.getClassType()
            + "  kind: " + root.getKind() + "\n");

         for (TreeElement te : root.getChildren()) {

            if (TreeElement.SIMPLE.equals(te.getKind())) {
               /**
                sb.append("child: arg name: " + te.getName()
                + "  classType: " + te.getClassType()
                + "  kind: " + te.getKind() + "\n");
                **/
               sb.append(te.getClassType() + " : " + te.getName() + " = "
                  + ((SimpleTreeElement)te).getValue() + "\n");

            } else if (TreeElement.GROUP.equals(te.getKind())) {
               sb.append("child: arg name: " + te.getName()
                  + "  classType: " + te.getClassType()
                  + "  rawType: " + ((GroupTreeElement) te).getProtoType().getClassType()
                  + "  kind: " + te.getKind() + "\n");

            }
            /***
             else if (TreeElement.ENUMERATION.equals(te.getKind())) {
             sb.append("Enum child: arg name: " + te.getName()
             + "  classType: " + te.getClassType()
             + "  kind: " + te.getKind() + "\n");

             for(String v : ((EnumerationTreeElement)te).getEnumValues()) {
             sb.append("Enum child: value" + v + "\n");
             }

             } else {
             sb.append("UNKNOW Kind: child: arg name: " + te.getName()
             + "  classTypeAsString: " + te.getClassType()
             + "  kind: " + te.getKind() + "\n");
             }
             ***/
         }
      }
      return sb.toString();
   }
}
