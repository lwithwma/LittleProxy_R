package org.littleshoot.proxy;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.xml.DOMConfigurator;
import org.littleshoot.proxy.extras.SelfSignedMitmManager;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.littleshoot.proxy.impl.ProxyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.Arrays;
import de.uniba.wiai.lspi.chord.com.local.Registry;
import de.uniba.wiai.lspi.chord.data.URL;
import de.uniba.wiai.lspi.chord.service.Chord;
import de.uniba.wiai.lspi.chord.service.ServiceException;
import de.uniba.wiai.lspi.chord.service.impl.ChordImpl;
import java.util.List;
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

import java.util.Scanner;

/**
 * Launches a new HTTP proxy.
 */
public class Launcher {

    private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);

    private static final String OPTION_DNSSEC = "dnssec";

    private static final String OPTION_PORT = "port";

    private static final String OPTION_HELP = "help";

    private static final String OPTION_MITM = "mitm";

    private static final String OPTION_NIC = "nic";

    /**
     * Starts the proxy from the command line.
     * 
     * @param args
     *            Any command line arguments.
     */
    public static void main(final String[] args) {
    	
    	Scanner scanner = new Scanner( System.in );
    	de.uniba.wiai.lspi.chord.service.PropertiesLoader.loadPropertyFile();
    	String protocol = URL.KNOWN_PROTOCOLS.get(URL.SOCKET_PROTOCOL);
		System.out.println(protocol);
		URL localURL = null;
		///System.out.println("If you want to create new chord network type yes else type the bootstrap");
		//String input = scanner.nextLine();
	try{
		MyUtils.systemIP = MyUtils.getSystemIP();
	}
	catch(Exception e){
		System.out.println(e);
	}
	System.out.println("Do you want to create Bootstrap node \n Press 1 to create\n Press 0 to joint the existing network");
	int isBootStrap = scanner.nextInt();
	scanner.nextLine();
	URL bootStrapURL = null;
	if (isBootStrap == 1) {
		System.out.println("Enter the Port at which you want to start Bootstrap");
		String bootStrapPort = scanner.nextLine();
		try {
			//localURL = new URL(protocol + "://"+ MyUtils.systemIP+ ":"+ localPort +"/");
			bootStrapURL = new URL ( protocol + "://"+MyUtils.systemIP+ ":"+ bootStrapPort +"/" ) ;
             System.out.println("bootStrapURL :");
            System.out.println(bootStrapURL);
		} catch ( Exception e ) {
			throw new RuntimeException ( e ) ;
		}	
		try {
			MyUtils.chord.create(bootStrapURL); //creating chord network
            System.out.println("Chord created ");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	else {
		System.out.println("Enter bootStrap");
		String bootstra = scanner.nextLine();   //ip:port
		//String bootstra = args[0];	
		System.out.println("Enter port to which you want to connect");
		String localPort = scanner.nextLine();
		//String localPort = args[1];	
	
		try {
			localURL = new URL(protocol + "://"+ MyUtils.systemIP+ ":"+ localPort +"/");
			bootStrapURL = new URL ( protocol + "://"+bootstra+"/" ) ;
            System.out.println("bootStrapURL :");
            System.out.println(bootStrapURL);
		} catch ( Exception e ) {
			throw new RuntimeException ( e ) ;
		}	
		try {
			MyUtils.chord.join(localURL,bootStrapURL); //joining the chord network
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	try{
		java.lang.Runtime.getRuntime().exec("arp -s 127.0.0.10 99:99:99:99:99:99"); //set ARP addres 10.0.0.253-->99:99:99:99:99:99(controllers ip )
	}
	catch(Exception e){
		System.out.println(e);
	}
    	
	System.out.println("Enter full path for directory in which you want to store repo like-/home/user/dir");
	String mpddir=scanner.nextLine();
	// String mpddir = args[2];	
	File folder = new File(mpddir);
	while (!folder.exists() || !folder.isDirectory()) {
		System.out.println("invalid Dierctory, Enter full path again");
		mpddir=scanner.nextLine();
		folder = new File(mpddir);
   	}
	MyUtils.repoDir = mpddir;		
	
	System.out.println("Enter full path for directory in which you want to store temperory data of mpd like-/home/user/dir");
	mpddir=scanner.nextLine();
	// mpddir = args[3];	
	folder = new File(mpddir);
	while (!folder.exists() || !folder.isDirectory()) {
		System.out.println("invalid Dierctory, Enter full path again");
		mpddir=scanner.nextLine();
		folder = new File(mpddir);
   	}
	MyUtils.mpdDir = mpddir;

        pollLog4JConfigurationFileIfAvailable();
        LOG.info("Running LittleProxy with args: {}", Arrays.asList(args));
	
        final Options options = new Options();
        options.addOption(null, OPTION_DNSSEC, true,
                "Request and verify DNSSEC signatures.");
        options.addOption(null, OPTION_PORT, true, "Run on the specified port.");
        options.addOption(null, OPTION_NIC, true, "Run on a specified Nic");
        options.addOption(null, OPTION_HELP, false,
                "Display command line help.");
        options.addOption(null, OPTION_MITM, false, "Run as man in the middle.");
        
        final CommandLineParser parser = new PosixParser();
        final CommandLine cmd;
	String[] ab = null; //my comment
        try {
            cmd = parser.parse(options, ab);//ab replace with args by lwith
            if (cmd.getArgs().length > 0) {
                throw new UnrecognizedOptionException(
                        "Extra arguments were provided in "
                                + Arrays.asList(args));
            }
        } catch (final ParseException e) {
            printHelp(options,
                    "Could not parse command line: " + Arrays.asList(args));
            return;
        }
        if (cmd.hasOption(OPTION_HELP)) {
            printHelp(options, null);
            return;
        }
        final int defaultPort = 8888;  //default port for littleproxy server
        int port;
        if (cmd.hasOption(OPTION_PORT)) {
            final String val = cmd.getOptionValue(OPTION_PORT);
            try {
                port = Integer.parseInt(val);
            } catch (final NumberFormatException e) {
                printHelp(options, "Unexpected port " + val);
                return;
            }
        } else {
            port = defaultPort;
        }


        System.out.println("About to start server on port: " + port);
        HttpProxyServerBootstrap bootstrap = DefaultHttpProxyServer
                .bootstrapFromFile("./littleproxy.properties")
                .withPort(port)
                .withAllowLocalOnly(false);

        if (cmd.hasOption(OPTION_NIC)) {
            final String val = cmd.getOptionValue(OPTION_NIC);
            bootstrap.withNetworkInterface(new InetSocketAddress(val, 0));
        }

        if (cmd.hasOption(OPTION_MITM)) {
            LOG.info("Running as Man in the Middle");
            bootstrap.withManInTheMiddle(new SelfSignedMitmManager());
        }
        
        if (cmd.hasOption(OPTION_DNSSEC)) {
            final String val = cmd.getOptionValue(OPTION_DNSSEC);
            if (ProxyUtils.isTrue(val)) {
                LOG.info("Using DNSSEC");
                bootstrap.withUseDnsSec(true);
            } else if (ProxyUtils.isFalse(val)) {
                LOG.info("Not using DNSSEC");
                bootstrap.withUseDnsSec(false);
            } else {
                printHelp(options, "Unexpected value for " + OPTION_DNSSEC
                        + "=:" + val);
                return;
            }
        }

        System.out.println("About to start...");
        bootstrap.start();
    }

    private static void printHelp(final Options options,
            final String errorMessage) {
        if (!StringUtils.isBlank(errorMessage)) {
            LOG.error(errorMessage);
            System.err.println(errorMessage);
        }

        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("littleproxy", options);
    }

    private static void pollLog4JConfigurationFileIfAvailable() {
        File log4jConfigurationFile = new File("src/test/resources/log4j.xml");
        if (log4jConfigurationFile.exists()) {
            DOMConfigurator.configureAndWatch(
                    log4jConfigurationFile.getAbsolutePath(), 15);
        }
    }
}
