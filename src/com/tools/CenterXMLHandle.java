package com.tools;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CenterXMLHandle {
	private String CenterNumber;
	private String Centercontent;

	public String LoadCenterNumber() {
		File file = new File("/sdcard/SmartBus/system/center.xml");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document doc = null;
		try {
			doc = db.parse(file);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element root = doc.getDocumentElement();
		NodeList stations = root.getElementsByTagName("center");
		for (int i = 0; i < stations.getLength(); i++) {
			Element station = (Element) stations.item(i);
			Element number = (Element) station.getElementsByTagName("number")
					.item(0);
			CenterNumber = number.getFirstChild().getNodeValue();
		}
		return CenterNumber;
	}

	public String LoadText() {
		File file = new File("/sdcard/SmartBus/system/center.xml");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document doc = null;
		try {
			doc = db.parse(file);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element root = doc.getDocumentElement();
		NodeList stations = root.getElementsByTagName("center");
		for (int i = 0; i < stations.getLength(); i++) {
			Element station = (Element) stations.item(i);
			Element content = (Element) station.getElementsByTagName("content")
					.item(0);
			Centercontent = content.getFirstChild().getNodeValue();
		}
		return Centercontent;
	}

}
