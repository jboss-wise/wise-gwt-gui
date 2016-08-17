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
package org.jboss.wise.gwt.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import org.jboss.wise.gwt.client.MainServiceAsync;
import org.jboss.wise.gwt.client.event.BackEvent;
import org.jboss.wise.gwt.client.event.EndpointConfigEvent;
import org.jboss.wise.gwt.client.event.PopupOpenEvent;
import org.jboss.wise.gwt.client.event.ProcessingExceptionEvent;
import org.jboss.wise.gwt.shared.Service;
import org.jboss.wise.gwt.shared.WsdlInfo;

import java.util.List;

/**
 * User: rsearls
 * Date: 3/6/15
 */
public class EndpointsPresenter implements Presenter {
    private final HandlerManager eventBus;
    private final Display display;
    private HandlerRegistration backButtonHandlerRegistration;
    private HandlerRegistration nextButtonHandlerRegistration;
    private HandlerRegistration treeButtonHandlerRegistration;
    public EndpointsPresenter(MainServiceAsync rpcService, HandlerManager eventBus, Display display) {
        this.eventBus = eventBus;
        this.display = display;
        bind();
    }

    public EndpointsPresenter(MainServiceAsync rpcService, final HandlerManager eventBus, Display display, WsdlInfo wsdlInfo) {

        this.eventBus = eventBus;
        this.display = display;

        bind();

        rpcService.getEndpoints(wsdlInfo, new AsyncCallback<List<Service>>() {
            public void onSuccess(List<Service> result) {

                EndpointsPresenter.this.display.setData(result);
            }

            public void onFailure(Throwable caught) {
                EndpointsPresenter.this.eventBus.fireEvent(new ProcessingExceptionEvent(caught.getMessage()));
                EndpointsPresenter.this.eventBus.fireEvent(new BackEvent());
            }
        });
    }

    private void bind() {

        backButtonHandlerRegistration = this.display.getBackButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                display.getNextButton().setEnabled(false);
                unbind();
                eventBus.fireEvent(new BackEvent());
            }
        });

        nextButtonHandlerRegistration = this.display.getNextButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {

                TreeItem tItem = display.getData().getSelectedItem();
                String id = display.getId(tItem);
                if (id != null) {
                    eventBus.fireEvent(new PopupOpenEvent());
                    eventBus.fireEvent(new EndpointConfigEvent(id));
                }
            }
        });

        treeButtonHandlerRegistration = this.display.getData().addSelectionHandler(new SelectionHandler<TreeItem>() {

            TreeItem currentTreeItem = null;

            public void onSelection(SelectionEvent<TreeItem> event) {

                TreeItem tItem = event.getSelectedItem();
                String id = display.getId(tItem);
                if (id != null) {
                    if (tItem != currentTreeItem) {
                        if (currentTreeItem != null) {
                            currentTreeItem.removeStyleName("endpoint-selected");
                        }
                        tItem.addStyleName("endpoint-selected");
                        currentTreeItem = tItem;
                    }

                    if (!display.getNextButton().isEnabled()) {
                        display.getNextButton().setEnabled(true);
                    }
                }
            }
        });
    }

    private void unbind() {
        // avoid duplicate events, unbind
        treeButtonHandlerRegistration.removeHandler();
        nextButtonHandlerRegistration.removeHandler();
        backButtonHandlerRegistration.removeHandler();

    }

    public void go(final HasWidgets container) {

        container.clear();
        container.add(display.asWidget());
    }

    public interface Display {
        HasClickHandlers getBackButton();

        Button getNextButton();

        Tree getData();

        void setData(List<Service> data);

        String getId(TreeItem tItem);

        Widget asWidget();
    }

}
