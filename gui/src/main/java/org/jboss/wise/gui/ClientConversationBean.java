/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.wise.gui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.logging.Logger;
import org.jboss.wise.core.client.BasicWSDynamicClient;
import org.jboss.wise.core.client.InvocationResult;
import org.jboss.wise.core.client.WSEndpoint;
import org.jboss.wise.core.client.WSMethod;
import org.jboss.wise.core.exception.InvocationException;
import org.jboss.wise.core.exception.WiseAuthenticationException;
import org.jboss.wise.core.exception.WiseProcessingException;
import org.jboss.wise.core.exception.WiseWebServiceException;
import org.jboss.wise.core.utils.JBossLoggingOutputStream;
import org.jboss.wise.gui.model.TreeNodeImpl;
import org.jboss.wise.gwt.shared.Service;

public class ClientConversationBean implements Serializable {

    private static final long serialVersionUID = -3778997821476776895L;

    private static final int CONVERSATION_TIMEOUT = 15 * 60 * 1000; //15 mins instead of default 30 mins
    private static CleanupTask<BasicWSDynamicClient> cleanupTask = new CleanupTask<BasicWSDynamicClient>(true);
    private static Logger logger = Logger.getLogger(ClientConversationBean.class);
    protected static PrintStream ps = new PrintStream(new JBossLoggingOutputStream(logger, Logger.Level.DEBUG), true);
    protected BasicWSDynamicClient client;
    private String wsdlUrl;
    private String wsdlUser;
    private String wsdlPwd;
    private String invocationUrl;
    private String invocationUser;
    private String invocationPwd;
    private List<Service> services;
    private String currentOperation;
    private String currentOperationFullName;
    private TreeNodeImpl inputTree;
    private TreeNodeImpl outputTree;
    private String error;
    private String requestPreview;
    private String responseMessage;
    private String requestActiveTab;

    /*private*/
    protected static void logException(Exception e) {

        logger.error("", e);
    }

    protected void addCleanupTask() {
	cleanupTask.addRef(client, System.currentTimeMillis() + CONVERSATION_TIMEOUT, new CleanupTask.CleanupCallback<BasicWSDynamicClient>() {
	    @Override
	    public void cleanup(BasicWSDynamicClient data) {
		data.close();
	    }
	});
    }

    public void performInvocation() throws WiseWebServiceException, WiseProcessingException, WiseAuthenticationException {

        outputTree = null;
        error = null;
        responseMessage = null;
        ByteArrayOutputStream os = null;
        try {
            WSMethod wsMethod = ClientHelper.getWSMethod(currentOperation, client);
            InvocationResult result = null;
            os = new ByteArrayOutputStream();

            Map<String, Object> params = ClientHelper.processGUIParameters(inputTree);
            ClientHelper.addOUTParameters(params, wsMethod, client);
            final WSEndpoint endpoint = wsMethod.getEndpoint();
            endpoint.setTargetUrl(invocationUrl);
            endpoint.setPassword(invocationPwd);
            endpoint.setUsername(invocationUser);
            endpoint.addHandler(new ResponseLogHandler(os));
            result = wsMethod.invoke(params);

            if (result != null) {
                outputTree = ClientHelper.convertOperationResultToGui(result, client);
                error = null;
            }

        } catch (InvocationException ie) {
            logException(ie);
            error = "Unexpected fault / error received from target endpoint";
            throw new WiseProcessingException(ClientHelper.toErrorMessage(ie), ie);
	} catch (WiseWebServiceException wwse) {
	    if (wwse.getMessage().contains("Authentication exception")) {
		throw new WiseAuthenticationException();
	    } else {
		Throwable c = wwse.getCause();
		// let UI display issue

		// special check for connection/timeout/404 exceptions
		Throwable ce = getConnectionException(c, new HashSet<Throwable>());
		if (ce != null) {
		    error = ce.getMessage();
		} else {
		    // other exceptions / soap faults
		    error = c.toString();
		}
	    }
        } catch (Exception e) {
            error = ClientHelper.toErrorMessage(e);
            logException(e);
            throw new WiseProcessingException(ClientHelper.toErrorMessage(e), e.getCause());
        } finally {
            try {
                responseMessage = os.toString("UTF-8");
            } catch (UnsupportedEncodingException uee) {

            }
            if (responseMessage.trim().length() == 0) {
                responseMessage = null;
            }
        }
    }
    
    private static Throwable getConnectionException(Throwable e, Set<Throwable> stack) {
        if (e == null || stack.contains(e)) {
            return null;
        } else if ((e instanceof ConnectException) || (e instanceof SocketTimeoutException)
                || (e.getMessage() != null && e.getMessage().contains("404: Not Found"))) {
	    return e;
	} else {
            stack.add(e);
            return getConnectionException(e.getCause(), stack);
        }
    }

    public void generateRequestPreview() {

        requestPreview = null;
        try {
            WSMethod wsMethod = ClientHelper.getWSMethod(currentOperation, client);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            wsMethod.getEndpoint().setTargetUrl(null);
            wsMethod.writeRequestPreview(ClientHelper.processGUIParameters(inputTree), os);
            requestPreview = os.toString("UTF-8");
        } catch (Exception e) {
            requestPreview = ClientHelper.toErrorMessage(e);
            logException(e);
        }
    }

    protected void cleanup() {

        if (client != null) {
            cleanupTask.removeRef(client);
            client.close();
            client = null;
        }
        services = null;
        currentOperation = null;
        currentOperationFullName = null;
        inputTree = null;
        outputTree = null;
        inputTree = null;
        error = null;
        responseMessage = null;
        invocationUrl = null;
        ///treeElementMap.clear();
    }

    public String getWsdlUrl() {

        return wsdlUrl;
    }

    public void setWsdlUrl(String wsdlUrl) {

        this.wsdlUrl = wsdlUrl;
    }

    public String getWsdlUser() {

        return wsdlUser;
    }

    public void setWsdlUser(String wsdlUser) {

        if (wsdlUser != null && wsdlUser.length() == 0) {
            this.wsdlUser = null;
        } else {
            this.wsdlUser = wsdlUser;
        }
    }

    public String getWsdlPwd() {

        return wsdlPwd;
    }

    public void setWsdlPwd(String wsdlPwd) {

        if (wsdlPwd != null && wsdlPwd.length() == 0) {
            this.wsdlPwd = null;
        } else {
            this.wsdlPwd = wsdlPwd;
        }
    }

    public String getInvocationUrl() {

        return invocationUrl;
    }

    public void setInvocationUrl(String invocationUrl) {

        if (invocationUrl != null && invocationUrl.length() == 0) {
            this.invocationUrl = null;
        } else {
            this.invocationUrl = invocationUrl;
        }
    }

    public String getInvocationUser() {

        return invocationUser;
    }

    public void setInvocationUser(String invocationUser) {

        if (invocationUser != null && invocationUser.length() == 0) {
            this.invocationUser = null;
        } else {
            this.invocationUser = invocationUser;
        }
    }

    public String getInvocationPwd() {

        return invocationPwd;
    }

    public void setInvocationPwd(String invocationPwd) {

        if (invocationPwd != null && invocationPwd.length() == 0) {
            this.invocationPwd = null;
        } else {
            this.invocationPwd = invocationPwd;
        }
    }

    public List<Service> getServices() {

        return services;
    }

    public void setServices(List<Service> services) {

        this.services = services;
    }

    public String getCurrentOperation() {

        return currentOperation;
    }

    public void setCurrentOperation(String currentOperation) {

        this.currentOperation = currentOperation;
    }

    public String getCurrentOperationFullName() {

        return currentOperationFullName;
    }

    public void setCurrentOperationFullName(String currentOperationFullName) {

        this.currentOperationFullName = currentOperationFullName;
    }

    public TreeNodeImpl getInputTree() {

        return inputTree;
    }

    public void setInputTree(TreeNodeImpl inputTree) {

        this.inputTree = inputTree;
    }

    public TreeNodeImpl getOutputTree() {

        return outputTree;
    }

    public void setOutputTree(TreeNodeImpl outputTree) {

        this.outputTree = outputTree;
    }

    public String getError() {

        return error;
    }

    public void setError(String error) {

        this.error = error;
    }

    public String getRequestPreview() {

        return requestPreview;
    }

    public void setRequestPreview(String requestPreview) {

        this.requestPreview = requestPreview;
    }

    public String getRequestActiveTab() {

        return requestActiveTab;
    }

    public void setRequestActiveTab(String requestActiveTab) {

        this.requestActiveTab = requestActiveTab;
    }

    public String getResponseMessage() {

        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {

        this.responseMessage = responseMessage;
    }
}
