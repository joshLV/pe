/*
 * @(#)XmlUtil.java	1.0 2008-5-8
 *
 * Copyright 2008 Client Server International, Inc. All rights reserved.
 * CSII PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.csii.pp.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.csii.pe.core.PeException;
import com.csii.pp.dict.Constants;
import com.csii.pp.dict.ErrorConstants;

public class XmlUtil {

	public static Log log = LogFactory.getLog(XmlUtil.class);

	public static Map toMap(String response) throws PeException {

		String response2 = response.replaceAll(">\\s*<", "><");
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder;
		Document doc;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(new InputSource(new StringReader(response2)));
		} catch (Exception e) {
			throw new PeException(e);
		}
		Node messageNode = doc.getElementsByTagName(Constants.MESSAGE_TAG).item(0);
		NodeList nodeList = messageNode.getChildNodes();
		log.info("Nodes Num :" + nodeList.getLength());
		log.info("Nodes List :" + nodeList);
		HashMap dataMap = new HashMap();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getFirstChild() == null) {
				dataMap.put(node.getNodeName(), "");
			} else {
				if (node.getFirstChild().hasChildNodes()) {
					NodeList subNodeList = node.getChildNodes();
					List merchantList = new ArrayList();
					for (int j = 0; j < subNodeList.getLength(); j++) {
						Node subNode = subNodeList.item(j);
						Map merTransDetail = new HashMap();
						if (subNode.hasChildNodes()) {
							NodeList mapList = subNode.getChildNodes();
							for (int k = 0; k < mapList.getLength(); k++) {
								Node mapnode = mapList.item(k);
								if (mapnode.getFirstChild() == null) {
									log.info("Node Name :[" + mapnode.getNodeName() + "],Node Value :[" + null + "]");
									merTransDetail.put(mapnode.getNodeName(), "");
								} else {
									log.info("Node Name :[" + mapnode.getNodeName() + "],Node Value :[" + mapnode.getFirstChild().getNodeValue() + "].");
									merTransDetail.put(mapnode.getNodeName(), mapnode.getFirstChild().getNodeValue());
								}

							}
						}
						merchantList.add(merTransDetail);
					}
					dataMap.put("MerTransList", merchantList);
				} else {
					log.info("Node Name :[" + node.getNodeName() + "],Node Value :[" + node.getFirstChild().getNodeValue() + "].");
					dataMap.put(node.getNodeName(), node.getFirstChild().getNodeValue());
				}
			}

		}
		return dataMap;
	}

	public static Map toMap(String response, String tagName) throws PeException {

		if (MiscUtil.isNullOrEmpty(response)) {
			log.error("报文为空，不能解析.");
			return Collections.EMPTY_MAP;
		}

		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder;
		Document doc;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(new InputSource(new StringReader(response)));
		} catch (Exception e) {
			throw new PeException(e);
		}
		Node messageNode = doc.getElementsByTagName(tagName).item(0);
		NodeList nodeList = messageNode.getChildNodes();
		HashMap dataMap = new HashMap();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getFirstChild() == null) {
				dataMap.put(node.getNodeName(), "");
			} else {
				if (node.getFirstChild().hasChildNodes()) {
					NodeList subNodeList = node.getChildNodes();
					List merchantList = new ArrayList();
					for (int j = 0; j < subNodeList.getLength(); j++) {
						Node subNode = subNodeList.item(j);
						Map merTransDetail = new HashMap();
						if (subNode.hasChildNodes()) {
							NodeList mapList = subNode.getChildNodes();
							for (int k = 0; k < mapList.getLength(); k++) {
								Node mapnode = mapList.item(k);
								if (mapnode.getFirstChild() == null) {
									merTransDetail.put(mapnode.getNodeName(), "");
								} else {
									merTransDetail.put(mapnode.getNodeName(), mapnode.getFirstChild().getNodeValue());
								}

							}
						}
						merchantList.add(merTransDetail);
					}
					dataMap.put("MerTransList", merchantList);
				} else {
					dataMap.put(node.getNodeName(), node.getFirstChild().getNodeValue());
				}
			}
		}
		return dataMap;
	}

	public static List strToList(String response) throws PeException {

		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder;
		Document doc;
		StringReader strReader = null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			strReader = new StringReader(response);
			doc = docBuilder.parse(new InputSource(strReader));
		} catch (Exception e) {
			throw new PeException(e);
		} finally {
			if (strReader != null) {
				strReader.close();
			}
		}
		NodeList nodeList = doc.getElementsByTagName("MerTransList");
		List merchantList = new ArrayList();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getFirstChild() == null) {
				throw new PeException(ErrorConstants.INVALID_PACKET_DATA);
			} else {
				if (node.getFirstChild().hasChildNodes()) {
					NodeList subNodeList = node.getChildNodes();
					for (int j = 0; j < subNodeList.getLength(); j++) {
						Node subNode = subNodeList.item(j);
						Map merTransDetail = new HashMap();
						if (subNode.hasChildNodes()) {
							NodeList mapList = subNode.getChildNodes();
							for (int k = 0; k < mapList.getLength(); k++) {
								Node mapnode = mapList.item(k);
								if (mapnode.getFirstChild() == null) {
									log.info("Node Name :[" + mapnode.getNodeName() + "],Node Value :[" + null + "]");
									merTransDetail.put(mapnode.getNodeName(), "");
								} else {
									log.info("Node Name :[" + mapnode.getNodeName() + "],Node Value :[" + mapnode.getFirstChild().getNodeValue() + "].");
									merTransDetail.put(mapnode.getNodeName(), mapnode.getFirstChild().getNodeValue());
								}
							}
						}
						merchantList.add(merTransDetail);
					}
				} else {
					log.info("Node Name :[" + node.getNodeName() + "],Node Value :[" + node.getFirstChild().getNodeValue() + "].");
					throw new PeException(ErrorConstants.INVALID_PACKET_DATA);
				}
			}
		}
		return merchantList;
	}
}
