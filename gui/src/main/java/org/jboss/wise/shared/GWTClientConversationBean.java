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
package org.jboss.wise.shared;

import org.jboss.logging.Logger;
import org.jboss.wise.core.client.builder.BasicWSDynamicClientBuilder;
import org.jboss.wise.core.client.impl.reflection.builder.ReflectionBasedBasicWSDynamicClientBuilder;
import org.jboss.wise.core.exception.WiseAuthenticationException;
import org.jboss.wise.core.exception.WiseProcessingException;
import org.jboss.wise.core.exception.WiseURLException;
import org.jboss.wise.core.exception.WiseWebServiceException;
import org.jboss.wise.gui.ClientConversationBean;
import org.jboss.wise.gui.ClientHelper;
import org.jboss.wise.gui.model.TreeNode;
import org.jboss.wise.gui.model.TreeNodeImpl;
import org.jboss.wise.gui.treeElement.*;
import org.jboss.wise.gwt.shared.Service;
import org.jboss.wise.gwt.shared.tree.element.*;
import org.jboss.wise.soap.fault.CodeType;
import org.jboss.wise.soap.fault.DetailType;
import org.jboss.wise.soap.fault.SubcodeType;
import org.w3c.dom.Element;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import java.io.StringReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * User: rsearls
 * Date: 4/2/15
 */
public class GWTClientConversationBean extends ClientConversationBean {

    private static final long serialVersionUID = 4531727535065189366L;
    private static Logger log = Logger.getLogger(GWTClientConversationBean.class);
    private Map<String, WiseTreeElement> treeElementMap = new HashMap<String, WiseTreeElement>();
    private HashMap<String, WiseTreeElement> lazyLoadMap = new HashMap<String, WiseTreeElement>();
    private WsdlFinder wsdlFinder = null;

    public void readWsdl() throws WiseProcessingException {

        cleanup();
        try {

            BasicWSDynamicClientBuilder builder = new ReflectionBasedBasicWSDynamicClientBuilder().verbose(true)
                    .messageStream(ps).keepSource(true).excludeNonSOAPPorts(true).maxThreadPoolSize(1);

            String wsdlUser = getWsdlUser();
            String wsdlPwd = getWsdlPwd();

            builder.userName(wsdlUser);
            setInvocationUser(wsdlUser);
            builder.password(wsdlPwd);
            setInvocationPwd(wsdlPwd);
            client = builder.wsdlURL(getWsdlUrl()).build();
            addCleanupTask();
        } catch (Exception e) {
            setError("Could not read WSDL from specified URL. Please check credentials and see logs for further information.");
            logException(e);
            throw new WiseProcessingException("Could not read WSDL from specified URL.", e.getCause());
        }
        if (client != null) {
            try {
                List<Service> services = ClientHelper.convertServicesToGui(client.processServices());
                String currentOperation = ClientHelper.getFirstGuiOperation(services);

                setServices(services);
                setCurrentOperation(currentOperation);

            } catch (Exception e) {
                setError("Could not parse WSDL from specified URL. Please check logs for further information.");
                logException(e);
                throw new WiseProcessingException("Could not read WSDL from specified URL.", e.getCause());
            }
        }
    }

    public RequestResponse parseOperationParameters(String curOperation) {

        try {
            List<Service> services = getServices();
            String currentOperation = getCurrentOperation();

            String currentOperationFullName = ClientHelper.getOperationFullName(currentOperation, services);
            TreeNodeImpl inputTree = ClientHelper
                    .convertOperationParametersToGui(ClientHelper.getWSMethod(curOperation, client), client);

            setInputTree(inputTree);
            setCurrentOperationFullName(currentOperationFullName);

        } catch (Exception e) {
            log.error(e);
        }

        TreeElement treeElement = wiseDataPostProcess((TreeNodeImpl) getInputTree());

        RequestResponse invResult = new RequestResponse();
        invResult.setOperationFullName(getCurrentOperationFullName());
        invResult.setTreeElement(treeElement);

        return invResult;
    }

    public String generateRequestPreview(TreeElement rootTreeElement) {
        userDataPostProcess(rootTreeElement);
        generateRequestPreview();
        return getRequestPreview();
    }

    public RequestResponse performInvocation(TreeElement root)
            throws WiseWebServiceException, WiseProcessingException, WiseAuthenticationException {

        userDataPostProcess(root);

        RequestResponse invResult = new RequestResponse();
        invResult.setOperationFullName(getCurrentOperationFullName());

        performInvocation();

        TreeElement treeE = null;
        TreeNodeImpl outputTree = getOutputTree();
        if (outputTree != null) {
            treeE = wiseOutputPostProcess(outputTree);
        }

        invResult.setResponseMessage(getResponseMessage());
        invResult.setTreeElement(treeE);
        invResult.setErrorMessage(getError());

        if (getError() != null) {
            TreeElement faultE = getSoapFault(getResponseMessage());
            invResult.setTreeElement(faultE);
        }

        return invResult;
    }

    /**
     * @param responseMessage
     * @return
     */
    private TreeElement getSoapFault(String responseMessage) {

        if (responseMessage != null) {
            if (responseMessage.contains("http://schemas.xmlsoap.org/soap/envelope")) {
                return unmarshalSOAP11Fault(getResponseMessage());

            } else if (responseMessage.contains("http://www.w3.org/2003/05/soap-envelope")) {
                return unmarshalSOAP12Fault(getResponseMessage());
            }
        }
        return null;
    }

    /**
     * @param responseMessage
     * @return
     */
    private TreeElement unmarshalSOAP12Fault(String responseMessage) {

        SimpleTreeElement rootTreeElement = new SimpleTreeElement();

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(org.jboss.wise.soap.fault.SOAP12Fault.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            StringReader reader = new StringReader(responseMessage);
            org.jboss.wise.soap.fault.SOAP12Fault soap12Fault = (org.jboss.wise.soap.fault.SOAP12Fault) unmarshaller
                    .unmarshal(reader);

            ComplexTreeElement errorRoot = new ComplexTreeElement();
            errorRoot.setName("SOAP 1.2 Fault");
            rootTreeElement.addChild(errorRoot);

            if (soap12Fault.getCode() != null) {
                ComplexTreeElement codeTreeElement = new ComplexTreeElement();
                codeTreeElement.setName("Code");
                errorRoot.addChild(codeTreeElement);

                CodeType codeType = soap12Fault.getCode();
                if (codeType.getValue() != null) {
                    SimpleTreeElement value = new SimpleTreeElement();
                    value.setName("value");
                    value.setValue(processQName(codeType.getValue()));
                    codeTreeElement.addChild(value);
                }

                TreeElement subcodeTreeElement = processSubCode(codeType.getSubcode());
                if (subcodeTreeElement != null) {
                    errorRoot.addChild(subcodeTreeElement);
                }
            }

            if (soap12Fault.getReason() != null) {
                ComplexTreeElement reason = new ComplexTreeElement();
                reason.setName("Reason");

                for (String s : soap12Fault.getReason().getTextList()) {
                    SimpleTreeElement text = new SimpleTreeElement();
                    text.setName("Text");
                    text.setValue(s);
                    reason.addChild(text);
                }

                errorRoot.addChild(reason);
            }

            if (soap12Fault.getRole() != null) {
                SimpleTreeElement role = new SimpleTreeElement();
                role.setName("Role");
                role.setValue(soap12Fault.getRole());
                errorRoot.addChild(role);
            }

            TreeElement detailTreeElement = processDetailType(soap12Fault.getDetail());
            if (detailTreeElement != null) {
                errorRoot.addChild(detailTreeElement);
            }

        } catch (javax.xml.bind.JAXBException e) {
            log.error(e);
        } catch (Exception ex) {
            log.error(ex);
        }

        return rootTreeElement;
    }

    /**
     * @param responseMessage
     * @return
     */
    private TreeElement unmarshalSOAP11Fault(String responseMessage) {

        SimpleTreeElement rootTreeElement = new SimpleTreeElement();

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(org.jboss.wise.soap.fault.SOAP11Fault.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            StringReader reader = new StringReader(responseMessage);
            org.jboss.wise.soap.fault.SOAP11Fault soap11Fault = (org.jboss.wise.soap.fault.SOAP11Fault) unmarshaller
                    .unmarshal(reader);

            ComplexTreeElement errorRoot = new ComplexTreeElement();
            errorRoot.setName("SOAP 1.1 Fault");
            rootTreeElement.addChild(errorRoot);

            if (soap11Fault.getFaultcode() != null) {
                SimpleTreeElement codeTreeElement = new SimpleTreeElement();
                codeTreeElement.setName("faultcode");
                codeTreeElement.setValue(processQName(soap11Fault.getFaultcode()));
                errorRoot.addChild(codeTreeElement);
            }

            if (soap11Fault.getFaultString() != null) {
                SimpleTreeElement faultstringTreeElement = new SimpleTreeElement();
                faultstringTreeElement.setName("faultstring");
                faultstringTreeElement.setValue(soap11Fault.getFaultString());
                errorRoot.addChild(faultstringTreeElement);
            }

            if (soap11Fault.getFaultactor() != null) {
                SimpleTreeElement faultactorTreeElement = new SimpleTreeElement();
                faultactorTreeElement.setName("faultactor");
                faultactorTreeElement.setValue(soap11Fault.getFaultactor());
                errorRoot.addChild(faultactorTreeElement);
            }

            TreeElement detailTreeElement = processDetailType(soap11Fault.getDetail());
            if (detailTreeElement != null) {
                errorRoot.addChild(detailTreeElement);
            }

        } catch (javax.xml.bind.JAXBException e) {
            log.error(e);
        } catch (Exception ex) {
            log.error(ex);
        }

        return rootTreeElement;
    }

    private ComplexTreeElement processSubCode(SubcodeType subcodeType) {

        if (subcodeType == null) {
            return null;
        }

        ComplexTreeElement parentTreeElement = new ComplexTreeElement();
        parentTreeElement.setName("Subcode");

        if (subcodeType.getValue() != null) {
            SimpleTreeElement simpleTreeElement = new SimpleTreeElement();
            simpleTreeElement.setValue(processQName(subcodeType.getValue()));
            simpleTreeElement.setName("Value");
            parentTreeElement.addChild(simpleTreeElement);
        }

        ComplexTreeElement subcodeTreeElement = processSubCode(subcodeType.getSubcode());
        if (subcodeTreeElement != null) {
            parentTreeElement.addChild(subcodeTreeElement);
        }
        return parentTreeElement;
    }

    /**
     * @param detailType
     * @return
     */
    private TreeElement processDetailType(DetailType detailType) {
        if (detailType != null) {

            ComplexTreeElement detailTreeElement = new ComplexTreeElement();
            detailTreeElement.setName("Detail");

            for (Element element : detailType.getDetails()) {
                SimpleTreeElement detail = new SimpleTreeElement();
                detail.setName(element.getTagName());
                detail.setValue(element.getTextContent());
                detailTreeElement.addChild(detail);
            }
            return detailTreeElement;
        }
        return null;
    }

    /**
     * @param qname
     * @return
     */
    private String processQName(QName qname) {

        String prefix = (qname.getPrefix() == null) ? "" : qname.getPrefix();
        String localPart = (qname.getLocalPart() == null) ? "" : qname.getLocalPart();
        String colon = "";
        if (!prefix.isEmpty() && !localPart.isEmpty()) {
            colon = ":";
        }

        return prefix + colon + localPart;
    }

    /**
     * Generate GWT objects that correspond to WISE objects
     *
     * @param tNode
     * @return
     */
    private TreeElement wiseDataPostProcess(TreeNodeImpl tNode) {

        lazyLoadMap.clear();

        SimpleTreeElement treeElement = new SimpleTreeElement();
        List<TreeElement> children = treeElement.getChildren();

        Iterator<Object> keyIt = tNode.getChildrenKeysIterator();
        while (keyIt.hasNext()) {
            WiseTreeElement child = (WiseTreeElement) tNode.getChild(keyIt.next());
            TreeElement te = wiseDataTransfer(child);
            children.add(te);
        }

        return treeElement;
    }

    private TreeElement wiseDataTransfer(WiseTreeElement wte) {

        TreeElement treeElement = TreeElementFactory.create(wte.getKind());

        if (treeElement instanceof GroupTreeElement) {
            GroupTreeElement gTreeElement = (GroupTreeElement) treeElement;
            WiseTreeElement protoType = ((GroupWiseTreeElement) wte).getPrototype();

            TreeElement tElement = wiseDataTransfer(protoType);
            gTreeElement.setProtoType(tElement);

            String rType = gTreeElement.getCleanClassName(((ParameterizedType) wte.getClassType()).getRawType().toString());
            gTreeElement.setClassType(rType);

        } else if (wte instanceof ComplexWiseTreeElement) {
            ComplexWiseTreeElement cNode = (ComplexWiseTreeElement) wte;
            Iterator<Object> keyIt = cNode.getChildrenKeysIterator();
            while (keyIt.hasNext()) {
                WiseTreeElement child = (WiseTreeElement) cNode.getChild(keyIt.next());
                TreeElement te = wiseDataTransfer(child);
                te.setId(child.getId().toString());
                treeElement.addChild(te);

            }

            lazyLoadMap.put(cNode.getClassType().toString(), cNode);

        } else if (wte instanceof ParameterizedWiseTreeElement) {
            ParameterizedWiseTreeElement cNode = (ParameterizedWiseTreeElement) wte;
            Iterator<Object> keyIt = cNode.getChildrenKeysIterator();
            while (keyIt.hasNext()) {
                WiseTreeElement child = (WiseTreeElement) cNode.getChild(keyIt.next());
                TreeElement te = wiseDataTransfer(child);
                te.setId(child.getId().toString());
                treeElement.addChild(te);

            }

        } else if (treeElement instanceof EnumerationTreeElement) {
            EnumerationTreeElement eTreeElement = (EnumerationTreeElement) treeElement;
            Map<String, String> eValuesMap = ((EnumerationWiseTreeElement) wte).getValidValue();
            if (eValuesMap != null) {
                eTreeElement.getEnumValues().addAll(eValuesMap.keySet());
            }
            eTreeElement.setValue(((SimpleWiseTreeElement) wte).getValue());

        } else {
            if (wte instanceof SimpleWiseTreeElement) {
                ((SimpleTreeElement) treeElement).setValue(((SimpleWiseTreeElement) wte).getValue());
            }

        }

        // classType check facilitates automated testing
        if (!(treeElement instanceof GroupTreeElement) && (wte.getClassType() != null)) {
            treeElement.setClassType(treeElement.getCleanClassName(wte.getClassType().toString()));
        }

        treeElement.setName(wte.getName());
        treeElement.setKind(wte.getKind());
        treeElement.setId(Integer.toString(((Object) wte).hashCode()));
        treeElement.setNil(wte.isNil());
        treeElement.setNillable(wte.isNillable());
        treeElementMap.put(treeElement.getId(), wte);

        return treeElement;
    }

    /**
     * Generate GWT objects from WISE response objects
     *
     * @param tNode
     * @return
     */
    private TreeElement wiseOutputPostProcess(TreeNodeImpl tNode) {

        SimpleTreeElement treeElement = new SimpleTreeElement();

        if (tNode == null) {
            log.error("wiseOutputPostProcess tNode is NULL");

        } else {
            List<TreeElement> children = treeElement.getChildren();

            Iterator<Object> keyIt = tNode.getChildrenKeysIterator();
            while (keyIt.hasNext()) {
                WiseTreeElement child = (WiseTreeElement) tNode.getChild(keyIt.next());
                TreeElement te = wiseOutputTransfer(child);
                children.add(te);
            }
        }
        return treeElement;
    }

    private TreeElement wiseOutputTransfer(WiseTreeElement wte) {

        TreeElement treeElement = TreeElementFactory.create(wte.getKind());

        if (treeElement instanceof GroupTreeElement) {
            GroupTreeElement gTreeElement = (GroupTreeElement) treeElement;
            WiseTreeElement protoType = ((GroupWiseTreeElement) wte).getPrototype();

            // test for characteristic of parameterizedType
            if (protoType != null) {
                TreeElement pElement = wiseOutputTransfer(protoType);
                gTreeElement.setProtoType(pElement);
            }

            Type[] typeArr = ((ParameterizedType) wte.getClassType()).getActualTypeArguments();
            if (typeArr != null && typeArr.length > 0) {
                String actualType = typeArr[0].toString();
                gTreeElement.setRawType(actualType);
            } else {
                log.error("ERROR parameterizedType actualTypeArguments not found for " + wte.getName());
            }

            String rType = gTreeElement.getCleanClassName(((ParameterizedType) wte.getClassType()).getRawType().toString());
            gTreeElement.setClassType(rType);

            GroupWiseTreeElement gChild = (GroupWiseTreeElement) wte;
            Iterator<Object> childKeyIt = gChild.getChildrenKeysIterator();
            while (childKeyIt.hasNext()) {
                WiseTreeElement c = (WiseTreeElement) gChild.getChild(childKeyIt.next());
                TreeElement te = wiseOutputTransfer(c);
                gTreeElement.addValue(te);
            }

        } else if (wte instanceof ComplexWiseTreeElement) {
            ComplexWiseTreeElement cNode = (ComplexWiseTreeElement) wte;
            Iterator<Object> keyIt = cNode.getChildrenKeysIterator();
            while (keyIt.hasNext()) {
                WiseTreeElement child = (WiseTreeElement) cNode.getChild(keyIt.next());
                TreeElement te = wiseOutputTransfer(child);
                treeElement.addChild(te);
            }

            treeElement.setClassType(cNode.getClassType().toString());

        } else if (wte instanceof ParameterizedWiseTreeElement) {
            ParameterizedWiseTreeElement cNode = (ParameterizedWiseTreeElement) wte;
            Iterator<Object> keyIt = cNode.getChildrenKeysIterator();
            while (keyIt.hasNext()) {
                WiseTreeElement child = (WiseTreeElement) cNode.getChild(keyIt.next());
                TreeElement te = wiseOutputTransfer(child);
                treeElement.addChild(te);
            }
            treeElement.setClassType(((ParameterizedTypeImpl) cNode.getClassType()).toString());

        } else if (treeElement instanceof EnumerationTreeElement) {
            EnumerationTreeElement eTreeElement = (EnumerationTreeElement) treeElement;
            Map<String, String> eValuesMap = ((EnumerationWiseTreeElement) wte).getValidValue();
            if (eValuesMap != null) {
                eTreeElement.getEnumValues().addAll(eValuesMap.keySet());
            }
            eTreeElement.setValue(((SimpleWiseTreeElement) wte).getValue());

            // classType check facilitates automated testing
            if (wte.getClassType() != null) {
                eTreeElement.setClassType(treeElement.getCleanClassName(wte.getClassType().toString()));
            }

        } else {
            if (wte instanceof SimpleWiseTreeElement) {
                if (wte.isNil()) {
                    ((SimpleTreeElement) treeElement).setValue("***NIL***");
                } else {
                    ((SimpleTreeElement) treeElement).setValue(((SimpleWiseTreeElement) wte).getValue());
                }

            }

            // classType check facilitates automated testing
            if (wte.getClassType() != null) {
                treeElement.setClassType(treeElement.getCleanClassName(wte.getClassType().toString()));
            }

        }

        treeElement.setName(wte.getName());
        treeElement.setKind(wte.getKind());
        treeElement.setId(Integer.toString(((Object) wte).hashCode()));
        treeElementMap.put(treeElement.getId(), wte);

        return treeElement;
    }

    /**
     * Transfer user input data from GWT objects to WISE objects
     *
     * @param root
     */
    private void userDataPostProcess(TreeElement root) {
        if (root != null) {
            for (TreeElement te : root.getChildren()) {
                WiseTreeElement wte = treeElementMap.get(te.getId());
                if (wte == null) {
                    // This should never happen
                    log.error("ERROR: not WiseTreeElement for TreeElement");

                } else {
                    userDataTransfer(te, wte);
                }
            }
        }
    }

    public void userDataTransfer(TreeElement treeElement, WiseTreeElement wte) {

        if (TreeElement.SIMPLE.equals(treeElement.getKind())) {

            if (wte instanceof SimpleWiseTreeElement) {

                if (!treeElement.isNil()) {
                    ((SimpleWiseTreeElement) wte).setValue((((SimpleTreeElement) treeElement).getValue() == null) ?
                            "" :
                            ((SimpleTreeElement) treeElement).getValue());
                }
                wte.setNil(treeElement.isNil());

            } else {
                log.error("ERROR: incompatible types. TreeElement: " + treeElement.getKind() + "  WiseTreeElement: " + wte
                        .getClass().getName());
            }

        } else if (treeElement instanceof ComplexTreeElement) {

            if (wte instanceof ComplexWiseTreeElement) {
                ComplexTreeElement cte = (ComplexTreeElement) treeElement;
                ComplexWiseTreeElement cWise = (ComplexWiseTreeElement) wte;

                if (!treeElement.isNil()) {

                    Iterator<Object> childKeyIt = cWise.getChildrenKeysIterator();
                    // create structure for node lookup by variable name
                    HashMap<String, TreeNode> wiseChildren = new HashMap<String, TreeNode>();
                    while (childKeyIt.hasNext()) {
                        TreeNode tNode = cWise.getChild(childKeyIt.next());
                        wiseChildren.put(((WiseTreeElement) tNode).getName(), tNode);
                    }

                    int cnt = cte.getChildren().size();
                    if (cnt == wiseChildren.size()) {
                        for (int i = 0; i < cnt; i++) {
                            TreeNode tNode = wiseChildren.get(cte.getChildren().get(i).getName());

                            if (tNode != null) {
                                userDataTransfer(cte.getChildren().get(i), (WiseTreeElement) tNode);
                            } else {
                                log.error("ERROR: No Wise treeNode found for name: " + cte.getChildren().get(i).getName());
                            }
                        }
                    } else {
                        log.error("ERROR: incompatable child count: ComplexTreeElement cnt: " + cte.getChildren().size()
                                + "  ComplexWiseTreeElement cnt: " + wiseChildren.size());
                    }
                }
                cWise.setNil(treeElement.isNil());

            } else {
                log.error("ERROR: incompatible types. TreeElement: " + treeElement.getKind() + "  WiseTreeElement: " + wte
                        .getClass().getName());
            }

        } else if (treeElement instanceof ParameterizedTreeElement) {

            if (wte instanceof ParameterizedWiseTreeElement) {
                ParameterizedTreeElement cte = (ParameterizedTreeElement) treeElement;
                ParameterizedWiseTreeElement cWise = (ParameterizedWiseTreeElement) wte;

                if (!treeElement.isNil()) {

                    Iterator<Object> childKeyIt = cWise.getChildrenKeysIterator();
                    // create structure for node lookup by variable name
                    HashMap<String, TreeNode> wiseChildren = new HashMap<String, TreeNode>();
                    while (childKeyIt.hasNext()) {
                        TreeNode tNode = cWise.getChild(childKeyIt.next());
                        wiseChildren.put(((WiseTreeElement) tNode).getName(), tNode);
                    }

                    int cnt = cte.getChildren().size();
                    if (cnt == wiseChildren.size()) {
                        for (int i = 0; i < cnt; i++) {
                            TreeNode tNode = wiseChildren.get(cte.getChildren().get(i).getName());

                            if (tNode != null) {
                                userDataTransfer(cte.getChildren().get(i), (WiseTreeElement) tNode);
                            } else {
                                log.error("ERROR: No Wise treeNode found for name: " + cte.getChildren().get(i).getName());
                            }
                        }
                    } else {
                        log.error("ERROR: incompatable child count: ParameterizedTreeElement cnt: " + cte.getChildren().size()
                                + "  ParameterizedWiseTreeElement cnt: " + wiseChildren.size());
                    }
                }
                cWise.setNil(treeElement.isNil());

            } else {
                log.error("ERROR: incompatible types. TreeElement: " + treeElement.getKind() + "  WiseTreeElement: " + wte
                        .getClass().getName());
            }

        } else if (treeElement instanceof GroupTreeElement) {

            if (wte instanceof GroupWiseTreeElement) {
                GroupTreeElement cte = (GroupTreeElement) treeElement;
                GroupWiseTreeElement cWise = (GroupWiseTreeElement) wte;

                if (!treeElement.isNil()) {

                    // Must use separate key list to void ConcurrentModificationException
                    Iterator<Object> keyIt = cWise.getChildrenKeysIterator();
                    List<Object> keyList = new ArrayList<Object>();
                    while (keyIt.hasNext()) {
                        keyList.add(keyIt.next());
                    }
                    // remove pre-existing protoType instances.
                    for (Object key : keyList) {
                        cWise.removeChild(key);
                    }

                    // replace deferred class with actual class.
                    if (cWise.getPrototype() instanceof LazyLoadWiseTreeElement) {
                        WiseTreeElement protoChildWte = lazyLoadMap.get(cWise.getPrototype().getClassType().toString());
                        WiseTreeElement clone = protoChildWte.clone();
                        cWise.setPrototype(clone);
                    }

                    for (TreeElement child : cte.getValueList()) {
                        WiseTreeElement protoChildWte = cWise.incrementChildren();
                        userDataTransfer(child, protoChildWte);
                    }
                }
                cWise.setNil(treeElement.isNil());

            } else {
                log.error("ERROR: incompatible types. TreeElement: " + treeElement.getKind() + "  WiseTreeElement: " + wte
                        .getClass().getName());
            }

        } else if (treeElement instanceof EnumerationTreeElement) {
            if (wte instanceof EnumerationWiseTreeElement) {
                if (treeElement.isNil()) {
                    ((EnumerationWiseTreeElement) wte).setValue(((EnumerationTreeElement) treeElement).getValue());
                    wte.setNil(treeElement.isNil());
                }
                wte.setNil(treeElement.isNil());
                ((EnumerationWiseTreeElement) wte).setValue(((EnumerationTreeElement) treeElement).getValue());
            } else {
                log.error("ERROR: incompatible types. TreeElement: " + treeElement.getKind() + "  WiseTreeElement: " + wte
                        .getClass().getName());
            }

        }
    }

    /**
     * check of deployed wsdls on the server.
     *
     * @return
     */
    public List<String> getWsdlList() {
        if (wsdlFinder == null) {
            wsdlFinder = new WsdlFinder();
        }
        return wsdlFinder.getWsdlList();
    }

    @Override protected void cleanup() {
        super.cleanup();
        treeElementMap.clear();
    }

    /**
     * Method faciliates testing
     *
     * @param tNode
     * @return
     */
    public TreeElement testItWiseOutputPostProcess(TreeNodeImpl tNode) {
        return wiseOutputPostProcess(tNode);
    }

    /**
     * Method faciliates testing
     *
     * @param tNode
     * @return
     */
    public TreeElement testItWiseDataPostProcess(TreeNodeImpl tNode) {
        return wiseDataPostProcess(tNode);
    }

    /**
     * Method faciliates testing
     *
     * @param treeElement
     * @param wte
     */
    public void testItUserDataTransfer(TreeElement treeElement, WiseTreeElement wte) {
        userDataTransfer(treeElement, wte);
    }

    /**
     * Confirm valid URL/URI.
     * <p>
     * Wrap JDK URL exceptions in local exception. GWT does not support the
     * JDK exceptions wrapped.
     *
     * @param url
     * @return
     * @throws WiseURLException - issue with URL format
     */
    public boolean isValidURL(String url) throws WiseURLException {

        URL u = null;
        try {
            u = new URL(url);
            u.toURI();
        } catch (MalformedURLException em) {
            // unknown protocol is specified.
            throw new WiseURLException(em.getMessage(), em);
        } catch (URISyntaxException eu) {
            // URL is not formatted strictly according to to RFC2396 and cannot be converted to a URI.
            throw new WiseURLException(eu.getMessage(), eu);
        }

        return true;
    }
}
