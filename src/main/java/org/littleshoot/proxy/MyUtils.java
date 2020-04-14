package org.littleshoot.proxy;

import de.uniba.wiai.lspi.util.console.ConsoleException;
import de.uniba.wiai.lspi.chord.com.local.Registry;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;
import java.util.List;
import java.util.HashMap;

import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.PrintStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import org.dom4j.DocumentHelper;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.XPath;

import java.net.NetworkInterface;
import java.util.Enumeration;
 
import java.net.InetAddress;
import java.net.Inet4Address;

import java.net.UnknownHostException;
import java.io.*;
import java.net.*;
import java.util.*;

public class MyUtils {
	public static ChordImpl chord = new ChordImpl();
	public static String systemIP = null;
	public static HashMap<String, String> reftoMpd = new HashMap<String, String>();   //referer url to mpd file
	public static String repoDir; //repository directory
	public static String mpdDir; // mpd directory
	public static HashMap<String, Vector<Vector<String>> >mpdtosegurls = new HashMap<String, Vector<Vector<String>> >(); //mpd to segment url
	
	public static String getSystemIP() throws Exception {
		Scanner scanner = new Scanner( System.in );
		String[] iparray = new String[20];
		int i = 1;
        	Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        	for (NetworkInterface netint : Collections.list(nets)){
		   System.out.println("Interface number" + i);
         	   System.out.println("Display name: " + netint.getDisplayName());
        	   System.out.println("Name: " + netint.getName());
        	   Enumeration<InetAddress> inetAddress = netint.getInetAddresses();
        	   InetAddress currentAddress;
		  // currentAddress = inetAddress.nextElement();
		   while (inetAddress.hasMoreElements()) {
			currentAddress = inetAddress.nextElement();
			if (currentAddress instanceof Inet4Address && !currentAddress.isLoopbackAddress()) {
				String ip = currentAddress.toString().substring(1);
				System.out.println("IPV4 address:  " + ip);
				iparray[i] = ip;
				break;
			}
		   }
        	   System.out.printf("\n");
		   i++;
   		 }

		System.out.println("Please enter the interface number you want to connect");
		int index = scanner.nextInt();
		while(index >= i){
			System.out.println("Invalid Input insert again");
			index = scanner.nextInt();
		}
		System.out.println("You choosed this IP: "+iparray[index]);
		return iparray[index];
		
	}
}
