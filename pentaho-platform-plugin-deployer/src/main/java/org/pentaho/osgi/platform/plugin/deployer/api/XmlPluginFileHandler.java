/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package org.pentaho.osgi.platform.plugin.deployer.api;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bryan on 8/26/14.
 */
public abstract class XmlPluginFileHandler implements PluginFileHandler {
  private final String xpath;

  protected XmlPluginFileHandler( String xpath ) {
    this.xpath = xpath;
  }

  @Override public boolean handle( String relativePath, byte[] file, PluginMetadata pluginMetadata )
    throws PluginHandlingException {
    try ( ByteArrayInputStream fileInputStream = new ByteArrayInputStream( file ) ) {
      XPath xPath = XPathFactory.newInstance().newXPath();
      InputSource inputSource = new InputSource( fileInputStream );
      NodeList nodeList = (NodeList) xPath.evaluate( xpath, inputSource, XPathConstants.NODESET );
      List<Node> nodes = new ArrayList<Node>( nodeList.getLength() );
      for ( int i = 0; i < nodeList.getLength(); i++ ) {
        nodes.add( nodeList.item( i ) );
      }
      handle( relativePath, nodes, pluginMetadata );
    } catch ( Exception e ) {
      throw new PluginHandlingException( e );
    }
    return false;
  }

  protected abstract void handle( String relativePath, List<Node> nodes, PluginMetadata pluginMetadata )
    throws PluginHandlingException;

  protected Map<String, String> getAttributes( Node node ) {
    Map<String, String> result = new HashMap<String, String>();
    NamedNodeMap namedNodeMap = node.getAttributes();
    for ( int i = 0; i < namedNodeMap.getLength(); i++ ) {
      Attr attr = (Attr) namedNodeMap.item( i );
      result.put( attr.getName(), attr.getValue() );
    }
    return result;
  }

  protected String camelCaseJoin( String str ) {
    StringBuilder sb = new StringBuilder();
    for ( String elem : str.split( "[^A-Za-z]" ) ) {
      String trimmed = elem.trim();
      if ( trimmed.length() > 0 ) {
        if ( sb.length() > 0 ) {
          sb.append( trimmed.substring( 0, 1 ).toUpperCase() );
        } else {
          sb.append( trimmed.substring( 0, 1 ).toLowerCase() );
        }
        if ( trimmed.length() > 1 ) {
          sb.append( trimmed.substring( 1 ) );
        }
      }
    }
    return sb.toString();
  }

  protected void setAttribute( Document document, Node node, String attribute, String value ) {
    Attr attr = document.createAttribute( attribute );
    attr.setValue( value );
    node.getAttributes().setNamedItem( attr );
  }
}
