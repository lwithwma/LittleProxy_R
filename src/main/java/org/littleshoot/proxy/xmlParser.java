package org.littleshoot.proxy;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;


import java.util.List;
import java.util.HashMap;

import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.PrintStream;

import java.net.NetworkInterface;
import java.util.Enumeration;
 
import java.net.InetAddress;
import java.net.Inet4Address;

import java.net.UnknownHostException;
//import java.io.*;
import java.net.*;
import java.util.*;
/*import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import org.dom4j.DocumentHelper;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.XPath;*/

public class xmlParser {

	public static void xmlprocess(String mpd){
		System.out.println("Inside xmlprocess");


		if (mpd == null){
			System.out.println("Inside xmlprocess: No MPD file");

			return ;
		}
		try{
			Vector<Vector<String>> vec = new Vector<Vector<String>>();
			//Get Document Builder
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();



			//Build Document
			File inputFile = new File(MyUtils.mpdDir + "/" + mpd); //create mpd file
			//SAXReader reader = new SAXReader();
			//Document document = reader.read(inputFile); //read mpd file

			System.out.println("Inside xmlprocess-->");
			Document document = builder.parse(new File(MyUtils.mpdDir + "/" + mpd));
			System.out.println("Inside xmlprocess<--");
			//Normalize the XML Structure; It's just too important !!
			document.getDocumentElement().normalize();

			//Here comes the root node
			Element root = document.getDocumentElement();
			System.out.println(root.getNodeName());

			//Get all segment
			NodeList nList = document.getElementsByTagName("SegmentURL");
			System.out.println("============================");

			int index=0;

			for (int temp = 0; temp < nList.getLength(); temp++)
			{
			 Node node = nList.item(temp);
			 System.out.println("inside for loop");    //Just a separator

			 vec.add(new Vector<String>());

				 if (node.getNodeType() == Node.ELEMENT_NODE)
				 {
				    //Print each employee's detail
				 	Element eElement = (Element) node;

					vec.get(index).add(eElement.getAttribute("media"));
					
				 	//System.out.println("Representation id : "    + eElement.getAttribute("id"));
				 	System.out.println("SegmentList : "  + eElement.getAttribute("media"));//.item(0).getTextContent());
				 	// System.out.println("Last Name : "   + eElement.getElementsByTagName("lastName").item(0).getTextContent());
				 	// System.out.println("Location : "    + eElement.getElementsByTagName("location").item(0).getTextContent());

				 }
			 
			}
              //index++;//unused for now

			MyUtils.mpdtosegurls.put(mpd,vec);

       }catch (Exception e) {
			//e.printStackTrace();
			System.out.println("ERROR Inside xmlprocess ");
		}
	}
}