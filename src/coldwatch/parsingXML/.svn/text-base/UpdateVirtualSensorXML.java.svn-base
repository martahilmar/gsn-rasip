package coldwatch.parsingXML;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.apache.log4j.Logger;

public class UpdateVirtualSensorXML {
	
	private static final transient Logger logger = Logger.getLogger(UpdateVirtualSensorXML.class);
	
	
	/*
	 * updateNotificationVSXML method gets xml file from filePath, positions on node from outputSQLNodePath (path to SQL output query) and 
	 * changes it value to newQuery. It also stores newState in processing-class/init-params/param with attribute name="notification-state"
	 * example: updateTagValue("/virtual-sensors/myVS.xml","streams/stream/query","select * from source1",1);
	 */
	public static void updateNotificationVSXML(String filePath, String outputSQLNodePath, String newQuery, int newState){
		
		try {
	        SAXBuilder builder = new SAXBuilder();
			File xmlFile = new File(filePath);
			Document doc = (Document) builder.build(xmlFile);
			Element rootNode = doc.getRootElement();
			Element outputSQLNode=rootNode;
	 
			//position on output SQL query node
			String[] nodePathList = outputSQLNodePath.split("/");
			
			for(int i=0;i<nodePathList.length;i++){
				outputSQLNode= outputSQLNode.getChild(nodePathList[i]);
			}	
			outputSQLNode.setText(newQuery);
		
			//positioning in processing-class/init-params/param with attribute name="notification-state" 
			List parameters = rootNode.getChild("processing-class").getChild("init-params").getChildren("param");
			Iterator iterator = parameters.iterator();
		    while (iterator.hasNext()) {
		    	Element parameter = (Element) iterator.next();
		    	if (parameter.getAttribute("name").getValue().equals("notification-state")){
		    		parameter.setText(String.valueOf(newState)); //new state 
		    	}
		    }

			XMLOutputter xmlOutput = new XMLOutputter();
	 
			// save updated xml file
			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(doc, new FileWriter(filePath));
			
	  } catch(IOException io) {
		logger.error(io.getMessage(),io);
	  } catch(JDOMException e) {
		logger.error(e.getMessage(), e);
	  }
		
	}


	public static void updateNotificationVSXMLState(String filePath, int newState){

		try {
	        SAXBuilder builder = new SAXBuilder();
			File xmlFile = new File(filePath);
			Document doc = (Document) builder.build(xmlFile);
			Element rootNode = doc.getRootElement();
			
			//positioning in processing-class/init-params/param with attribute name="notification-state" 
			List parameters = rootNode.getChild("processing-class").getChild("init-params").getChildren("param");
			Iterator iterator = parameters.iterator();
		    while (iterator.hasNext()) {
		    	Element parameter = (Element) iterator.next();
		    	if (parameter.getAttribute("name").getValue().equals("notification-state")){
		    		parameter.setText(String.valueOf(newState)); //new state 
		    	}
		    }

			XMLOutputter xmlOutput = new XMLOutputter();
	 
			// save updated xml file
			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(doc, new FileWriter(filePath));
			
	  } catch(IOException io) {
		logger.error(io.getMessage(),io);
	  } catch(JDOMException e) {
		logger.error(e.getMessage(), e);
	  }
		
	}
	
	public static void updateNotificationVSXMLErrorMessageTime(String filePath, long newTime){

		try {
	        SAXBuilder builder = new SAXBuilder();
			File xmlFile = new File(filePath);
			Document doc = (Document) builder.build(xmlFile);
			Element rootNode = doc.getRootElement();
			
			//positioning in processing-class/init-params/param with attribute name="notification-state" 
			List parameters = rootNode.getChild("processing-class").getChild("init-params").getChildren("param");
			Iterator iterator = parameters.iterator();
		    while (iterator.hasNext()) {
		    	Element parameter = (Element) iterator.next();
		    	if (parameter.getAttribute("name").getValue().equals("last-error-message-time")){
		    		parameter.setText(String.valueOf(newTime)); //new state 
		    	}
		    }

			XMLOutputter xmlOutput = new XMLOutputter();
	 
			// save updated xml file
			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(doc, new FileWriter(filePath));
			
	  } catch(IOException io) {
		logger.error(io.getMessage(),io);
	  } catch(JDOMException e) {
		logger.error(e.getMessage(), e);
	  }
		
	}
	
	public static void updateMailState(String filePath, int newMailState)
	{
		try{
			SAXBuilder builder = new SAXBuilder();
			File xmlFile = new File(filePath);
			Document doc = (Document) builder.build(xmlFile);
			Element rootNode = doc.getRootElement();
			
			List parameters = rootNode.getChild("processing-class").getChild("init-params").getChildren("param");
			Iterator iterator = parameters.iterator();
			
			while (iterator.hasNext()) {
		    	Element parameter = (Element) iterator.next();
		    	if (parameter.getAttribute("name").getValue().equals("mail-state")){
		    		parameter.setText(String.valueOf(newMailState));
		    	}
		    }
			XMLOutputter xmlOutput = new XMLOutputter();
			
			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(doc, new FileWriter(filePath));
			
		} catch(IOException io){
			logger.error(io.getMessage(),io);
		} catch(JDOMException e){
			logger.error(e.getMessage(), e);
		}
	}


	public static void updateNotificationVSXMLErrorMessageTimeAndNotificationState(String filePath, long newTime, int newState ){

		try {
	        SAXBuilder builder = new SAXBuilder();
			File xmlFile = new File(filePath);
			Document doc = (Document) builder.build(xmlFile);
			Element rootNode = doc.getRootElement();
			
			//positioning in processing-class/init-params/param with attribute name="notification-state" 
			List parameters = rootNode.getChild("processing-class").getChild("init-params").getChildren("param");
			Iterator iterator = parameters.iterator();
		    while (iterator.hasNext()) {
		    	Element parameter = (Element) iterator.next();
		    	if (parameter.getAttribute("name").getValue().equals("last-error-message-time")){
		    		parameter.setText(String.valueOf(newTime)); //new state 
		    	}
		    	if (parameter.getAttribute("name").getValue().equals("notification-state")){
		    		parameter.setText(String.valueOf(newState)); //new state 
		    	}
		    	
		    }

			XMLOutputter xmlOutput = new XMLOutputter();
	 
			// save updated xml file
			xmlOutput.setFormat(Format.getPrettyFormat());
			xmlOutput.output(doc, new FileWriter(filePath));
			
	  } catch(IOException io) {
		logger.error(io.getMessage(),io);
	  } catch(JDOMException e) {
		logger.error(e.getMessage(), e);
	  }
		
	}





}
