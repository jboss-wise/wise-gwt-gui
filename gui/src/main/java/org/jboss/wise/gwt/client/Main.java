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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import org.jboss.wise.gwt.client.event.InputWsdlEvent;

// debugging
//import java.util.logging.Level;
//import java.util.logging.Logger;

/**
 * User: rsearls
 * Date: 3/8/15
 */
public class Main implements EntryPoint {
    //private static Logger rootLogger = Logger.getLogger("");

    public void onModuleLoad() {
        MainServiceAsync rpcService = GWT.create(MainService.class);
        HandlerManager eventBus = new HandlerManager(null);
        AppController appViewer = new AppController(rpcService, eventBus);

        //rootLogger.log(Level.INFO, "Main: href: " + Window.Location.getHref());
        //rootLogger.log(Level.INFO, "Main: queryString: " + Window.Location.getQueryString());

        String hRef = Window.Location.getHref();
        String qStr = Window.Location.getQueryString();
        if (qStr != null && qStr.length() > 0) {
            int indx = hRef.indexOf(qStr);
            if (indx > 0) {

                // extract and send the URL's query parameter to the start page
                String wsdlParam = Window.Location.getParameter("wsdl");
                if (wsdlParam != null) {
                    String decodedWsdl = URL.decodeQueryString(wsdlParam);
                    eventBus.fireEvent(new InputWsdlEvent(decodedWsdl));
                }
            }
        }

        appViewer.go(RootPanel.get());
    }
}
