package com.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.model.Station;

public class XMLHandle {

	private List<Station> StationList = new ArrayList<Station>();
	private List<String> LineList;
	Station s = null;

	public List<Station> LoadXML(String FileName) {
		File file = new File(FileName);
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
		NodeList stations = root.getElementsByTagName("station");
		for (int i = 0; i < stations.getLength(); i++) {
			Element station = (Element) stations.item(i);
			Element name = (Element) station.getElementsByTagName("name").item(
					0);
			Element longitude = (Element) station.getElementsByTagName(
					"longitude").item(0);
			Element latitude = (Element) station.getElementsByTagName(
					"latitude").item(0);
			s = new Station();
			s.setName(name.getFirstChild().getNodeValue());
			s.setLongitude(longitude.getFirstChild().getNodeValue());
			s.setLatitude(latitude.getFirstChild().getNodeValue());
			StationList.add(s);
		}
		return StationList;
	}

	public String LoadLine(String FileName) {
		LineList = new ArrayList<String>();
		File file = new File(FileName);
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
		NodeList stations = root.getElementsByTagName("station");
		for (int i = 0; i < stations.getLength(); i++) {
			Element station = (Element) stations.item(i);
			Element name = (Element) station.getElementsByTagName("name").item(
					0);
			LineList.add(name.getFirstChild().getNodeValue());
		}
		return LineList.get(0) + " ¡ú " + LineList.get(LineList.size() - 1);
	}
}
