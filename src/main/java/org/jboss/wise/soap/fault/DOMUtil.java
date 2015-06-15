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


import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class DOMUtil {

   private static DocumentBuilder db;

   /**
    * Creates a new DOM document.
    */
   public static Document createDom() {
      synchronized (DOMUtil.class) {
         if (db == null) {
            try {
               DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
               dbf.setNamespaceAware(true);
               db = dbf.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
               throw new FactoryConfigurationError(e);
            }
         }
         return db.newDocument();
      }
   }

   public static Node createDOMNode(InputStream inputStream) {

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      dbf.setValidating(false);
      try {
         DocumentBuilder builder = dbf.newDocumentBuilder();
         try {
            return builder.parse(inputStream);
         } catch (SAXException e) {
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
         }
      } catch (ParserConfigurationException pce) {
         IllegalArgumentException iae = new IllegalArgumentException(pce.getMessage());
         iae.initCause(pce);
         throw iae;
      }
      return null;
   }

   /**
    * Traverses a DOM node and writes out on a streaming writer.
    *
    * @param node
    * @param writer
    */
   public static void serializeNode(Element node, XMLStreamWriter writer) throws XMLStreamException {
      writeTagWithAttributes(node, writer);

      if (node.hasChildNodes()) {
         NodeList children = node.getChildNodes();
         for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            switch (child.getNodeType()) {
               case Node.PROCESSING_INSTRUCTION_NODE:
                  writer.writeProcessingInstruction(child.getNodeValue());
               case Node.DOCUMENT_TYPE_NODE:
                  break;
               case Node.CDATA_SECTION_NODE:
                  writer.writeCData(child.getNodeValue());
                  break;
               case Node.COMMENT_NODE:
                  writer.writeComment(child.getNodeValue());
                  break;
               case Node.TEXT_NODE:
                  writer.writeCharacters(child.getNodeValue());
                  break;
               case Node.ELEMENT_NODE:
                  serializeNode((Element) child, writer);
                  break;
            }
         }
      }
      writer.writeEndElement();
   }

   public static void writeTagWithAttributes(Element node, XMLStreamWriter writer) throws XMLStreamException {
      String nodePrefix = fixNull(node.getPrefix());
      String nodeNS = fixNull(node.getNamespaceURI());

      String nodeLocalName = node.getLocalName()== null?node.getNodeName():node.getLocalName();

      boolean prefixDecl = isPrefixDeclared(writer, nodeNS, nodePrefix);
      writer.writeStartElement(nodePrefix, nodeLocalName, nodeNS);

      if (node.hasAttributes()) {
         NamedNodeMap attrs = node.getAttributes();
         int numOfAttributes = attrs.getLength();

         for (int i = 0; i < numOfAttributes; i++) {
            Node attr = attrs.item(i);
            String nsUri = fixNull(attr.getNamespaceURI());
            if (nsUri.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
               // handle default ns declarations
               String local = attr.getLocalName().equals(XMLConstants.XMLNS_ATTRIBUTE) ? "" : attr.getLocalName();
               if (local.equals(nodePrefix) && attr.getNodeValue().equals(nodeNS)) {
                  prefixDecl = true;
               }
               if (local.equals("")) {
                  writer.writeDefaultNamespace(attr.getNodeValue());
               } else {
                  writer.setPrefix(attr.getLocalName(), attr.getNodeValue());
                  writer.writeNamespace(attr.getLocalName(), attr.getNodeValue());
               }
            }
         }
      }

      if (!prefixDecl) {
         writer.writeNamespace(nodePrefix, nodeNS);
      }

      if (node.hasAttributes()) {
         NamedNodeMap attrs = node.getAttributes();
         int numOfAttributes = attrs.getLength();

         for (int i = 0; i < numOfAttributes; i++) {
            Node attr = attrs.item(i);
            String attrPrefix = fixNull(attr.getPrefix());
            String attrNS = fixNull(attr.getNamespaceURI());
            if (!attrNS.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
               String localName = attr.getLocalName();
               if (localName == null) {
                  localName = attr.getNodeName();
               }
               boolean attrPrefixDecl = isPrefixDeclared(writer, attrNS, attrPrefix);
               if (!attrPrefix.equals("") && !attrPrefixDecl) {
                  writer.setPrefix(attr.getLocalName(), attr.getNodeValue());
                  writer.writeNamespace(attrPrefix, attrNS);
               }
               writer.writeAttribute(attrPrefix, attrNS, localName, attr.getNodeValue());
            }
         }
      }
   }

   private static boolean isPrefixDeclared(XMLStreamWriter writer, String nsUri, String prefix) {
      boolean prefixDecl = false;
      NamespaceContext nscontext = writer.getNamespaceContext();
      Iterator prefixItr = nscontext.getPrefixes(nsUri);
      while (prefixItr.hasNext()) {
         if (prefix.equals(prefixItr.next())) {
            prefixDecl = true;
            break;
         }
      }
      return prefixDecl;
   }

   /**
    * Gets the first child of the given name, or null.
    */
   public static Element getFirstChild(Element e, String nsUri, String local) {
      for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
         if (n.getNodeType() == Node.ELEMENT_NODE) {
            Element c = (Element) n;
            if (c.getLocalName().equals(local) && c.getNamespaceURI().equals(nsUri))
               return c;
         }
      }
      return null;
   }

   private static String fixNull(String s) {
      if (s == null) return "";
      else return s;
   }

   /**
    * Gets the first element child.
    */
   public static Element getFirstElementChild(Node parent) {
      for (Node n = parent.getFirstChild(); n != null; n = n.getNextSibling()) {
         if (n.getNodeType() == Node.ELEMENT_NODE) {
            return (Element) n;
         }
      }
      return null;
   }

   public static List<Element> getChildElements(Node parent){
      List<Element> elements = new ArrayList<Element>();
      for (Node n = parent.getFirstChild(); n != null; n = n.getNextSibling()) {
         if (n.getNodeType() == Node.ELEMENT_NODE) {
            elements.add((Element)n);
         }
      }
      return elements;
   }
}
