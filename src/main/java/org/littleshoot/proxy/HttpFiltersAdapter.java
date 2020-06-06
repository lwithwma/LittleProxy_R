package org.littleshoot.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.buffer.ByteBuf;
import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import org.dom4j.DocumentHelper;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.XPath;
import java.nio.charset.Charset;

import de.uniba.wiai.lspi.util.console.ConsoleException;
import de.uniba.wiai.lspi.chord.com.local.Registry;
//import de.uniba.wiai.lspi.chord.data.URL;
//import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;

/*import de.uniba.wiai.lspi.chord.com.CommunicationException;
import de.uniba.wiai.lspi.chord.com.Entry;
import de.uniba.wiai.lspi.chord.com.Node;
import de.uniba.wiai.lspi.chord.com.Proxy;
import de.uniba.wiai.lspi.chord.com.RefsAndEntries;
import de.uniba.wiai.lspi.chord.data.ID;
import de.uniba.wiai.lspi.chord.data.URL;*/
import de.uniba.wiai.lspi.chord.service.AsynChord;
import de.uniba.wiai.lspi.chord.service.Chord;
//import de.uniba.wiai.lspi.chord.service.ChordCallback;
//import de.uniba.wiai.lspi.chord.service.ChordFuture;
//import de.uniba.wiai.lspi.chord.service.ChordRetrievalFuture;
//import de.uniba.wiai.lspi.chord.service.Key;
//import de.uniba.wiai.lspi.chord.service.Report;
//import de.uniba.wiai.lspi.chord.service.ServiceException;
//import de.uniba.wiai.lspi.util.logging.Logger;
import java.io.IOException;
//import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.net.DatagramPacket; 
import java.net.DatagramSocket; 
import java.net.InetAddress; 
//import java.util.Scanner; 

/**
 * Convenience base class for implementations of {@link HttpFilters}.
 */
public class HttpFiltersAdapter implements HttpFilters {
	/**
	 * A default, stateless, no-op {@link HttpFilters} instance.
	 */
	public static final HttpFiltersAdapter NOOP_FILTER = new HttpFiltersAdapter(null);
	protected final HttpRequest originalRequest;
	protected final ChannelHandlerContext ctx;
	private int i;
	private String mpd;

	// private String[] array1;
	// private String[] newArray;
	// private String referer;
	// private String requestUri;
	// private String mpd;

	public HttpFiltersAdapter(HttpRequest originalRequest, ChannelHandlerContext ctx) {
		/*String getUrl=originalRequest.getUri();
        String[] array = getUrl.split("\\?",2);
        String segmentUrl=array[0];
        this.originalRequest.setUri(segmentUrl);*/
		this.originalRequest = originalRequest;
		this.ctx = ctx;
		System.out.println("inside HttpFiltersAdapter.java" +"\n"); //my comment
	}

	public HttpFiltersAdapter(HttpRequest originalRequest) {
		this(originalRequest, null);
		System.out.println("inside HttpFiltersAdapter.javas" +"\n"); //my comment
	}


//copy request URL to new string array newArray eg->10.0.0.1:8080/dash.html
	@Override
	public HttpResponse clientToProxyRequest(HttpObject httpObject) {
		System.out.println("Inside clientToProxyRequest-->start of httpObject:"+ httpObject +"<----end of httpObject\n");
		/*String requestUri = originalRequest.getUri();
		System.out.println("clientToProxyRequest-Request Uri:" + requestUri);
		String[] array1 = requestUri.split("/");
		if (array1.length > 2) {
			System.out.println("Array1: ");
			for (String str : array1)
				System.out.println(str);
			String[] newArray = Arrays.copyOfRange(array1, 3, array1.length);
		}*/

		String getUrl=originalRequest.getUri();
		if(getUrl.contains("?")){
          String[] array = getUrl.split("\\?",2);
          String segmentUrl=array[0];
          originalRequest.setUri(segmentUrl);
		}
        System.out.println("\n");
		return null;
	}

	@Override
	public HttpResponse proxyToServerRequest(HttpObject httpObject) {
		System.out.println("Inside proxyToServerRequest-->start of httpObject:"+ httpObject +"<----end of httpObject\n");
		return null;
	}

	@Override
	public void proxyToServerRequestSending() {
		System.out.println("Inside proxyToServerRequestSending" +"\n");
	}

	@Override
	public void proxyToServerRequestSent() {
		System.out.println("Inside proxyToServerRequestSent" +"\n");
	}

	@Override
	public HttpObject serverToProxyResponse(HttpObject httpObject) {
		String requestUri = originalRequest.getUri();
		 System.out.println("Inside serverToProxyResponse -->start of httpObject:"+ httpObject +"<----end of httpObject\n");

		String[] array1 = requestUri.split("/");
		if (array1.length > 2) {
			String[] newArray = Arrays.copyOfRange(array1, 3, array1.length);
			//System.out.println("REFERER: " + originalRequest.headers().get("REFERER"));
			String referer = originalRequest.headers().get("REFERER");

			if (httpObject instanceof FullHttpResponse) {
				FullHttpResponse response = (FullHttpResponse) httpObject;
				// String requestUri = originalRequest.getUri();

				if (requestUri.matches(".*[./]mpd.*$") || requestUri.matches(".*[./]mp4.*$")
						|| requestUri.matches(".*[./]m4s.*$") || requestUri.matches(".*[./]woff2.*$")) {
					ByteBuf buff = response.content();
					//System.out.println("REQUEST recieved at server:" +i+" "+ requestUri);

					byte[] file = new byte[buff.capacity()];
					for (int i = 0; i < buff.capacity(); i++) {
						byte b = buff.getByte(i);
						file[i] = b;
					}
					String fileSize = Integer.toString(buff.capacity());
					System.out.println("Size of File: "+ fileSize);
					String str;
					//if (!requestUri.matches(".*[./]mpd.*$")) { //if not .mpd file 
					if(requestUri.matches(".*[./]m4s.*$")){ //if segment
						str = getRepoPath(); //calling ........
						System.out.println("RepoPath:"+str);
						System.out.println("Initial Mappings are: " + MyUtils.reftoMpd); 

						String data = MyUtils.reftoMpd.get(referer)+newArray[newArray.length - 1];
						//String data="hello";
						System.out.println("Data :" + data); 

						Key myKey = new Key(data);
						System.out.println("myKey :" + myKey); 
						//After downloading the video chunk, the IP address of the current peer is added into the list of IPs holding the segment.
						//Insert new segment for requested client in chord
						try{
							MyUtils.chord.insert(myKey, MyUtils.systemIP); //problem?????.......................
						} catch(Exception e){
							System.out.println("Error while inserting IP address as data in chord \n");
						}
						

						Key sizeKey = new Key(data+"size");
						System.out.println(" sizeKey:" + sizeKey); 
						////Insert new segment size for requested client in chord
						//MyUtils.chord.insert(sizeKey, fileSize);//???.................*/
						try{
							MyUtils.chord.insert(sizeKey, fileSize);//???.................*/
						} catch(Exception e){
							System.out.println("Error while inserting file size as data in chord \n");
						}
					//	createEntry(newArray[newArray.length - 1]);
						//System.out.println("..................................................................not mpd file inside serverToProxyResponse");

					} else {
						
						if(requestUri.matches(".*[./]mp4.*$")){
							str = getRepoPath(); //calling ........
						}else{
							str = MyUtils.mpdDir;
							MyUtils.reftoMpd.put(referer, newArray[newArray.length - 1]);//referer to mpd 
						}
						
					}
					str = str + "/" + newArray[newArray.length - 1];
					try {
						FileOutputStream fos = new FileOutputStream(str);
						fos.write(file);
						fos.close();
					} catch (Exception e) {
						System.out.println(e);
					}

				}

				// System.out.println(buf);

			}
		}
        System.out.println("\n");
		return httpObject;

	}

	@Override
	public void serverToProxyResponseTimedOut() {
		System.out.println("Inside serverToProxyResponseTimedOut:");
	}

	@Override
	public void serverToProxyResponseReceiving() {
				System.out.println("Inside serverToProxyResponseReceiving:");

	}

	@Override
	public void serverToProxyResponseReceived() {
				System.out.println("Inside serverToProxyResponseReceived:");

	}

	@Override
	public HttpObject proxyToClientResponse(HttpObject httpObject) {
				System.out.println("Inside proxyToClientResponse:");

		return httpObject;
	}

	@Override
	public void proxyToServerConnectionQueued() {
				System.out.println("Inside proxyToServerConnectionQueued:");

	}

	@Override
	public void proxyToServerResolutionFailed(String hostAndPort) {
				System.out.println("Inside proxyToServerResolutionFailed:");

	}

	@Override
	public void proxyToServerResolutionSucceeded(String serverHostAndPort, InetSocketAddress resolvedRemoteAddress) {
				System.out.println("Inside proxyToServerResolutionSucceeded:");

	}

	@Override
	public void proxyToServerConnectionStarted() {
				System.out.println("Inside proxyToServerConnectionStarted:");

	}

	@Override
	public void proxyToServerConnectionSSLHandshakeStarted() {
				System.out.println("Inside proxyToServerConnectionSSLHandshakeStarted:");

	}

	@Override
	public void proxyToServerConnectionFailed() {
				System.out.println("Inside proxyToServerConnectionFailed:");

	}

	@Override
	public void proxyToServerConnectionSucceeded(ChannelHandlerContext serverCtx) {
				System.out.println("Inside proxyToServerConnectionSucceeded:");

	}

	@Override
	public InetSocketAddress proxyToServerResolutionStarted(String resolvingServerHostAndPort) {
		System.out.println("Inside proxyToServerResolutionStarted");

		return null;
	}

   //Modules 1)
	@Override
	public String getRelatedHostAndPortForBola(String serverHostAndPort){
		String requestUri = originalRequest.getUri();
		System.out.println("Inside getRelatedHostAndPortForBola:" + serverHostAndPort);

		if (requestUri.matches(".*[./]m4s.*$")){ //if segment
		   String[] array1 = requestUri.split("/");

		   if (array1.length > 2) {
				String[] newArray = Arrays.copyOfRange(array1, 3, array1.length);
				System.out.println("REFERER: " + originalRequest.headers().get("REFERER"));
				String referer = originalRequest.headers().get("REFERER");
				String mpdName = MyUtils.reftoMpd.get(referer);
				String relatedHostAndPort = null;

				if (newArray != null && newArray.length != 0) {
					String segName=newArray[newArray.length - 1];
					
					//-------Search set of host(IPs) containing the required segment--------------------------------------------->
					try{
						    Key myKey = new Key(mpdName + segName);  //key for  segment 
							Set<Serializable> peerSet = MyUtils.chord.retrieve(myKey); //calling retrieve function from ChordImpl(??? values of all node that should be store in given id(node))
							HashSet<String> pset = (peerSet instanceof HashSet) ? (HashSet)peerSet : new HashSet<Serializable>(peerSet);
						    //if peer is not empty
							if(!peerSet.isEmpty()){
								//--------------------------A data structure to store end to end available bandwidth between the requesting peer and the available peers;---------------------->
								HashMap<String, String> hostToAvailBandwidth = retrieveBandwidths(pset); //calling retrieveBandwidths function
                                //selecting the largest bandwidth
                                String host=null;
                                BigInteger initBw=new BigInteger("0");
								for(Serializable peer: peerSet) { 
								  String hostToSelect = peer.toString();
								  String hostBandwidth=hostToAvailBandwidth.get(hostToSelect);
								  BigInteger hostBandwidthInt=new BigInteger(hostBandwidth);
								  int comparevalue = hostBandwidthInt.compareTo(initBw); 
								  if((comparevalue ==1)|| (comparevalue==0)){
								  	host=hostToSelect;
								  	initBw=hostBandwidthInt;
								  }

							     }


								relatedHostAndPort = host+":8080";
								array1[2] = relatedHostAndPort;
								String str = array1[0];
								array1[array1.length - 1] = segName;
								for(int i = 1; i<array1.length; i++){
									str = str+"/"+array1[i];//<<=================/???
								}
								System.out.println("str in bola: "+ str +"\n");
								if(exists(str)){
								  // true if str is an url to any peer
								    originalRequest.setUri(str);
									return relatedHostAndPort;
								} 
								else relatedHostAndPort = null;
								

							 }else{
									System.out.println("Segment :"+ segName + " is not present in chord");
									//ifSegIsPresent=false;
					             }
							
						} catch(Exception e){
							System.out.println("Error while receiveing key data \n");
						}

				}




		   }


		}
       return serverHostAndPort;
	}


//TODO: Host and port identified, Retrive host list from controller.

	/*Based on the information received from different modules such as the 1) segment names(segNames), 2) size of each segment(segToSize), 
	3) set of IPs holding those segments(hostToRep) and 4) bandwidth of those IPs from the local machine(hostToAvailBandwidth)*/

	//if there is no peer cointaining the required segment then client will connect to main server
	@Override
	public String getRelatedHostAndPort(String serverHostAndPort){   // serverHostAndPort:10.0.0.1:8080
		String requestUri = originalRequest.getUri();
		 System.out.println("Inside getRelatedHostAndPort:" + serverHostAndPort);

	   if (requestUri.matches(".*[./]m4s.*$")){ //if segment
		String[] array1 = requestUri.split("/");

		if (array1.length > 2) {
			String[] newArray = Arrays.copyOfRange(array1, 3, array1.length);
			System.out.println("REFERER: " + originalRequest.headers().get("REFERER"));
			String referer = originalRequest.headers().get("REFERER");

			String mpdName = MyUtils.reftoMpd.get(referer);
			String relatedHostAndPort = null;

			if (newArray != null && newArray.length != 0) {
				//relatedHostAndPort = getUrl(newArray[newArray.length - 1]);
				//Get all representation segment names
				HashSet<String> segNames = getAllRepSegNames(newArray[newArray.length - 1]);  //get all representation segment names of given segment argument
				System.out.println("end of getAllRepSegNames \n");
				//for(String s:segNames)
				HashMap<String,Set<String>> hostToRep = new HashMap<String,Set<String>>(); // host to representation or segment
				
				String data = mpdName + newArray[newArray.length - 1];
				HashMap<String,String> segToSize = new HashMap<String,String>();    //An ordered map in decreasing order of segment size
				HashMap<Integer, String> sizeToSeg = new HashMap<Integer, String>();
				boolean ifSegIsPresent=true; 

				//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
				//creating hostToRep, segToSize and sizeToSeg for each segment present in segNames
				for (String segName: segNames) {
    					//String rep = entry.getKey();
    					//String segName = entry.getValue();
					System.out.println("segName: "+segName+"  mpdName: "+mpdName);
					Key myKey = new Key(mpdName + segName);  //key for each segment 

					//<---------------------------------Search set of host(IPs) containing the required segment--------------------------------------------->
					try{
							Set<Serializable> values = MyUtils.chord.retrieve(myKey); //calling retrieve function from ChordImpl(??? values of all node that should be store in given id(node))


						//add segment to each host present in set values
							if(!values.isEmpty()){
								for(Serializable s: values) {
								String host = s.toString();
								if(hostToRep.get(host) == null){
									hostToRep.put(host, new HashSet<String>());
								}
								hostToRep.get(host).add(segName);
							    }

							}else{
									System.out.println("Segment :"+ segName + " is not present in chord");
									ifSegIsPresent=false;
					             }
							
						} catch(Exception e){
							System.out.println("Error while receiveing key data \n");
						}

					//segment to size
					Key sizeKey = new Key(mpdName+segName+"size");
					//retrive size of segment from chord
					try{
						Set<Serializable> sizeSet = MyUtils.chord.retrieve(sizeKey);//????????????


					if(!sizeSet.isEmpty()){
						for(Serializable s: sizeSet){
							String size = s.toString();
							if(isNumber(size)){
								segToSize.put(segName,size);
								sizeToSeg.put(new Integer(size), segName);
								System.out.println("Size of File is " + size); 
								break;
							}
						}
					}
					else{
						System.out.println("Size of :" + segName +" is not present in chord");
					}
					} catch(Exception e){
						System.out.println("Error while receiveing size of key data \n");
					}
							
				}//end of for loop


               if(ifSegIsPresent){   //if required segment is  present in chord
				//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
				Set<String> set  = hostToRep.keySet(); //set of all host containing segments
				HashSet<String> values = (set instanceof HashSet) ? (HashSet)set : new HashSet<Serializable>(set);

				//--------------------------A data structure to store end to end available bandwidth between the requesting peer and the available peers;---------------------->
				HashMap<String, String> hostToAvailBandwidth = retrieveBandwidths(values); //calling retrieveBandwidths function

				HashMap<String, Set<String>> positiveRepToHosts = new HashMap<String, Set<String>>(); 
				HashMap<Integer, String> negativeMetricToHostRep = new HashMap<Integer, String>();
				//HashMap
				//------------------------------Algorithm to choose peer with optimal bit rate starts-------------------------->
				BigInteger requiredMetric = new BigInteger("20");

				//coping data from segToSize to entry map.
				for (Map.Entry<String, String> entry : segToSize.entrySet()){
    					System.out.println(entry.getKey() + "/" + entry.getValue());
				}

				//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
				for (Map.Entry<String, Set<String>> entry : hostToRep.entrySet())
				{
    					//System.out.println(entry.getKey() + "/" + entry.getValue());
					String host = entry.getKey();
					Set<String> availableReps = entry.getValue();
					String availableBandwidth = hostToAvailBandwidth.get(host);
					for(String rep : availableReps){
						System.out.println("segment: "+rep);
						String requiredBandwidth = segToSize.get(rep);
						//BigInteger metric = 0;
						//Todo: Logic To calculate metric based on available and required bandwidth.
						//BigInteger
						BigInteger aB = new BigInteger(availableBandwidth);
						BigInteger rB = new BigInteger(requiredBandwidth);
						BigInteger x = aB.subtract(rB);
						BigInteger metric = x.divide(rB);  //availedMetric
						
						if(metric.compareTo(requiredMetric) >= 0){
							if(positiveRepToHosts.get(rep) == null){
								positiveRepToHosts.put(rep,new HashSet<String>());
							}
							positiveRepToHosts.get(rep).add(host);
						}	
						else{					
							BigInteger diff = requiredMetric.subtract(metric);
							int dificiency = diff.intValue();	
							String hostRep = host + "/" + rep;
							negativeMetricToHostRep.put(dificiency, hostRep);
						} 
					}
				}
				

				//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx	
				ArrayList<String> repList = new ArrayList(sizeToSeg.values()); //list of segment in array
				if(repList != null){

					for (int j = repList.size() - 1; j >= 0; j--) { //for each segment
				//get key
						String rep = repList.get(j);
						Set<String> hosts = positiveRepToHosts.get(rep);
						if(hosts != null){
							for(String host : hosts){  //for each host
								relatedHostAndPort = host;
								array1[2] = relatedHostAndPort;
								String str = array1[0];
								array1[array1.length - 1] = rep;
								for(int i = 1; i<array1.length; i++){
									str = str+"/"+array1[i];//<<=================/???
								}
								System.out.println("str1: "+ str +"\n");
								if(exists(str)) // true if str is an url to any peer
									return relatedHostAndPort;
								else relatedHostAndPort = null;
							}
						}
					}
				}


				//xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
				//if only if condition above fails in an algo it get executed
				ArrayList<String> hostReps = new ArrayList(negativeMetricToHostRep.values()); //host/segment list eg.10.0.0.2/segment_name

				if(hostReps != null){
					for(String hostRep : hostReps){ //for each host/seg
						String[] array2 = hostRep.split("/");

						relatedHostAndPort = array2[0]+":8080"; //10.0.0.2:8080
						System.out.println("relatedHostAndPort: "+ relatedHostAndPort +"\n");
						String rep = array2[1];  //segment_name
						System.out.println("rep: "+ rep +"\n");
						array1[2] = relatedHostAndPort; 
						String str = array1[0]; //http:
						System.out.println("str: "+ str +"\n");
						array1[array1.length - 1] = rep;
						for(int i = 1; i<array1.length; i++){
							str = str+"/"+array1[i];
						}
						System.out.println("str2: "+ str +"\n"); //str2: http://10.0.0.2:8080/das/segment_name
						if(exists(str)){
							originalRequest.setUri(str);
							return relatedHostAndPort;
						}
						else relatedHostAndPort = null;
					}					
				}

              }
			}

			if (relatedHostAndPort == null)
				relatedHostAndPort = serverHostAndPort;

			System.out.println("Sercer" + serverHostAndPort);
			return relatedHostAndPort;
		}
		System.out.println("RElated Hsst");
	  }
		return serverHostAndPort;
	}

	@Override
	public HttpRequest sendingFinalRequest(HttpRequest httpRequest){
				             		System.out.println("Inside sendingFinalRequest:"+ httpRequest + "\n");

		httpRequest.setUri(originalRequest.getUri());
		return httpRequest;
	}


   //return all representation segment names
	private HashSet<String> getAllRepSegNames(String requestedFile) { //requestedFile is segment name
				             		System.out.println("Inside getAllRepSegNames");

		String referer = originalRequest.headers().get("REFERER");
		String mpd = MyUtils.reftoMpd.get(referer);
		String str = MyUtils.mpdDir + "/" + mpd; //MyUtils.reftoMpd.get(referer); //location of mpd file
		Vector<Vector<String>> segUrls= MyUtils.mpdtosegurls.get(mpd);
		/*System.out.println("Data inside segUrls:");
		for(int i=0;i<segUrls.size();i++){
			 for(int j=0;j<segUrls[i].size();j++){
				System.out.println(segUrls[i][j]+ "\n");
			 }
		}
*/
		//if mpd have no segment associted
		if(segUrls == null){
			xmlParser.xmlprocess(mpd);  // calling of xmlprocess function
			segUrls = MyUtils.mpdtosegurls.get(mpd);
		}
		//find index of given segment from segurls
		int index = -1;
		for(Vector<String> ve : segUrls){    
			if(ve.indexOf(requestedFile) != -1){
				index = ve.indexOf(requestedFile);
				break;
			}
		}
		System.out.println("Size is " + segUrls.size() + " Index is " + index);
		HashSet<String> segs = new HashSet<String>();

		for(Vector<String> ve : segUrls){
			System.out.println("Size of 1D vector is " + ve.size());
			if(ve.size()>0)//add new line
			 segs.add(ve.get(index));
		}
		return segs; //seg2,seg2,seg2 from representation 1080p,720p and 480p
	}




	/*private void xmlprocess(String mpd){
				             		System.out.println("Inside xmlprocess");


		if (mpd == null){
		    System.out.println("Inside xmlprocess: No MPD file");

			return ;
		}
		try {
			File inputFile = new File(MyUtils.mpdDir + "/" + mpd); //create mpd file
			SAXReader reader = new SAXReader();
			Document document = reader.read(inputFile); //read mpd file
			HashMap<String,String> map = new HashMap<>();
			Vector<Vector<String>> vec = new Vector<Vector<String>>();
			map.put("edx", "urn:mpeg:dash:schema:mpd:2011"); //xmlns=urn:mpeg:dash:schema:mpd:2011   ???
			System.out.println("one \n");
			System.out.println("MPD File Size in bytes :"+inputFile.length() + "\n"); 

			XPath xpath = document.createXPath("/edx:MPD/edx:Period/edx:AdaptationSet/edx:Representation");  //get all representation
			//document.createXPath("/edx:MPD/edx:Period/edx:AdaptationSet/edx:Representation");
			System.out.println("two \n");
			xpath.setNamespaceURIs(map);

			//System.out.println("XPath:"+ xpath.getText());  //printing xpath

			List<Node> nodes = xpath.selectNodes(document); //list of all representation .Each representation are nodes

			System.out.println("Root element :" + document.getRootElement().getName());
			Element classElement = document.getRootElement();
			System.out.println("----------------------------");
			int index = 0;

			for (Node node : nodes) { //for each representation
				//int i = 0;
				vec.add(new Vector<String>());
				vec.get(index).add(node.valueOf("@bandwidth"));
				System.out.println("\nCurrent Element :" + node.getName());
				
				XPath xpath1 = document.createXPath("edx:SegmentList/edx:SegmentURL"); //get all SegmentURL
				xpath1.setNamespaceURIs(map);
				List<Node> segNodes = xpath1.selectNodes(node, xpath1, false); //Each SegmentURL are nodes
				
				for (Node node1 : segNodes) { //for each segmentURL
					
					vec.get(index).add(node1.valueOf("@media"));
				}
				index++;
			}

			MyUtils.mpdtosegurls.put(mpd,vec);//<========================================

		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("ERROR Inside xmlprocess ");
		}
		
	}*/

	private String getRepoPath() {
		String requestUri = originalRequest.getUri();
		System.out.println(" Inside getRepoPath " + requestUri);
		String[] array1 = requestUri.split("/");

		String str = MyUtils.repoDir;
		if (array1.length > 2) {
			String[] newArray = Arrays.copyOfRange(array1, 3, array1.length);
			for (int i = 0; i < newArray.length - 1; i++) {
				str = str + "/" + newArray[i];
			}
		}
		File f = new File(str);
		boolean isCreated = f.mkdirs();
		if (isCreated) {
			System.out.println("Directory created");
		}
		System.out.println("\n");
		return str;
	}

//---------------------------send all set of host(IPs) to retrive bandwidth of all host containing segment from controller ---------------------------------------------------->
	private HashMap<String,String> retrieveBandwidths(HashSet<String> values){ //values argument contain set of host containing segments
		try{
             		System.out.println("Inside retrieveBandwidths");

            //Datagram socket is a type of network socket which provides connection-less point for sending and receiving packets(UDP)
             		//Step 1:Create the socket object for carrying the data. 
			    DatagramSocket serverSocket = new DatagramSocket(); //controller socket
     			ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
      			ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
      			os.flush();
      			os.writeObject(values);
      			os.flush();
      			//retrieves byte array
      			byte sendBuf[] = byteStream.toByteArray(); //data to send
      			//String testSend="hello";
      			//byte sendBuf[]=testSend.getBytes();
			    byte receiveData[] = new byte[1024];
      			//int byteCount = packet.getLength();
			    String ip="10.0.0.1";
      			//String ip="127.0.0.10"; //ip of controller
    	  		InetAddress address = InetAddress.getByName(ip);
          		int port = 7777;
		    //creating datagram packet cointaining all set of host containing segment
          		//Step 2 : Create the datagramPacket for sending the data. 
			DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, address, port);
			   //serverSocket.connect(address,port);

			    System.out.println("Sending Packet to controller:-->"+sendPacket +"<--");
			    // Step 3 : invoke the send call to actually send the data.
          		serverSocket.send(sendPacket);
			    System.out.println("Packet Send");

			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			//DatagramSocket recServerSocket = new DatagramSocket(6653);
			// Step 4 : revieve the data in byte buffer. 
			serverSocket.receive(receivePacket);
			byte[] dataBytes = receivePacket.getData();
			 System.out.println("Packet Received from controller:-->"+receivePacket+"<--");
			
			HashMap<String, String> o = null;
			ByteArrayInputStream byteStream1 = new ByteArrayInputStream(dataBytes);
			ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream1));
			Map<String, String> readObject = (Map)is.readObject();
			o = (readObject instanceof HashMap) ? (HashMap)readObject : new HashMap<String,String>(readObject);
			//if( readObject instanceof HashSet) o = (HashSet) readObject;
			is.close();
			System.out.println("Converted to HASHMAP");
					
    	   		os.close();       	
           		serverSocket.close();
			return o;
           		// return sentence;
         }
		  catch(Exception e){
			System.out.println(e);
		    }

		System.out.println("\n");
		return null;
         
	}


	public static boolean exists(String URLName){
    		try {
    					System.out.println("Inside exists" );

     			 HttpURLConnection.setFollowRedirects(false);
     			 // note : you may also need
     			 //        HttpURLConnection.setInstanceFollowRedirects(false)
     			 HttpURLConnection con =(HttpURLConnection) new URL(URLName).openConnection();//connection to URLName
     			 con.setRequestMethod("HEAD"); //The HEAD method is functionally similar to GET, except that the server replies with a response line and headers, but no entity-body.
      			return (con.getResponseCode() == HttpURLConnection.HTTP_OK); //Used to retrieve the response status from server.
    		}
    		catch (Exception e) {
       			e.printStackTrace();
       			return false;
    		}
  	}


	private boolean isNumber(String s){
    		try{
    			System.out.println("Inside isNumber" );
        		Integer.parseInt(s);
    		}
    		catch(NumberFormatException e){
        		return false;
    		}
    		return true;
	}
}
