package hr.fer.rasip.http;

import gsn.http.RequestHandler;

import hr.fer.rasip.passiveHeating.control.*;

import gsn.Main;

import java.io.IOException;

import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.InetAddress;


public class PassiveHeatingControllerHandler implements RequestHandler {

	private static final String CONTROL_SERVLET_PATH = "/passiveheating/control";
	
	private static final String AUTOCONTROL_SERVLET_PATH = "/passiveheating/autocontrol";
	
	private static final String CONFIG_SERVLET_PATH = "/passiveheating/config";
	
	private static final String AIR_SERVLET_PATH = "/passiveheating/air";
	
	private static final String FAN_PARAMETER_STRING = "fan";
	
	private static final String HEATER_PARAMETER_STRING = "heater";
	
	private static final String INTAKE_PARAMETER_STRING = "intake";
	
	private static final String INTAKE_NORMAL_STRING = "normal";
	
	private static final String INTAKE_OVERRIDE_STRING = "override";
	
	private static final String CONFIG_FILE_PATH = "passiveHeating/config.xml";
	
	private static transient Logger logger = Logger.getLogger(PassiveHeatingControllerHandler.class);


	public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
        Main.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(Main.getContainerConfig().getTimeFormat());
        
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<response>\n");
        
        String servletPath = request.getServletPath();
        
        if(servletPath.equalsIgnoreCase(CONTROL_SERVLET_PATH)){
        	//control servlet
        	int fan = -1;
        	int heater = -1;
        	if(request.getParameter(FAN_PARAMETER_STRING) != null && request.getParameter(FAN_PARAMETER_STRING).trim().length() != 0){
        		fan = Integer.parseInt(request.getParameter(FAN_PARAMETER_STRING));
        	}
        	if(request.getParameter(HEATER_PARAMETER_STRING) != null && request.getParameter(HEATER_PARAMETER_STRING).trim().length() != 0){
        		heater = Integer.parseInt(request.getParameter(HEATER_PARAMETER_STRING));
        	}
        	
        	try{
            	SAXBuilder builder = new SAXBuilder();
            	
    			File xmlFile = new File(CONFIG_FILE_PATH);
    			Document doc = (Document) builder.build(xmlFile);
    			Element root = doc.getRootElement();
    			Element state = root.getChild("state");
  
    			//disable auto control
    			state.getChild("auto-control").setText("0");
    			if(fan != -1){
    				state.getChild("manual-fan").setText(String.valueOf(fan));
    			}
    			if(heater != -1){
    				state.getChild("manual-heater").setText(String.valueOf(heater));
    			}
    			
    			// save updated xml file
    	        XMLOutputter xmlOutput = new XMLOutputter();
    	   	 	xmlOutput.setFormat(Format.getPrettyFormat());
    			xmlOutput.output(doc, new FileWriter(CONFIG_FILE_PATH));
        	}
        	catch(Exception e){
        		sb.append("<status>exception</status>\n<description>"+ e.getClass()+": " + e.getMessage() + "</description>\n</response>");
    			response.setHeader("Cache-Control", "no-store");
    	        response.setDateHeader("Expires", 0);
    	        response.setHeader("Pragma", "no-cache");
    	        response.getWriter().write(sb.toString());
        		return; 
        	}
        	
        	try {
        		//ovdje eventualno staviti kasnjenje
        		Thread.sleep(2000);
        		SAXBuilder builder = new SAXBuilder();
    			File xmlFile = new File(CONFIG_FILE_PATH);
    			Document doc = (Document) builder.build(xmlFile);
    			Element root = doc.getRootElement();
    			Element coreParameters = root.getChild("core-parameters");
    			String rabbitIP = coreParameters.getChild("rabbit-ip").getValue();
    			int freeServerPort = Integer.parseInt(coreParameters.getChild("free-server-port").getValue());
        		InetAddress localInetAddress = InetAddress.getLocalHost();
				InetAddress remoteInetAddress = InetAddress.getByName(rabbitIP);
				Control control = new Control(localInetAddress, freeServerPort, remoteInetAddress);
				if(fan != -1){
					control.setFanPower(fan);
				}
				if(heater != -1){
					control.setHeaterState((heater==1)?true:false);
				}
        	}
        	catch(Exception e){
        		sb.append("<status>exception</status>\n<description>"+ e.getClass()+": " + e.getMessage() + "</description>\n</response>");
    			response.setHeader("Cache-Control", "no-store");
    	        response.setDateHeader("Expires", 0);
    	        response.setHeader("Pragma", "no-cache");
    	        response.getWriter().write(sb.toString());
        		return; 
        	}
			
        	sb.append("<status>ok</status>\n<description>Passive heating values set to "
        				+ ((fan != -1)?"fan = " + fan + " ":" "  )
        				+ ((heater != -1)?"heater = " + heater + " ":" "  ) + "</description>\n</response>");
        	
        }

        if(servletPath.equalsIgnoreCase(AUTOCONTROL_SERVLET_PATH)){
        	//auto control servlet
        	try {
        		SAXBuilder builder = new SAXBuilder();
    			File xmlFile = new File(CONFIG_FILE_PATH);
    			Document doc = (Document) builder.build(xmlFile);
    			Element root = doc.getRootElement();
    			
    			root.getChild("state").getChild("auto-control").setText("1");
    			
    			// save updated xml file
    	        XMLOutputter xmlOutput = new XMLOutputter();
    	   	 	xmlOutput.setFormat(Format.getPrettyFormat());
    			xmlOutput.output(doc, new FileWriter(CONFIG_FILE_PATH));
    			
        	}
        	catch(Exception e){
        		sb.append("<status>exception</status>\n<description>"+ e.getClass()+": " + e.getMessage() + "</description>\n</response>");
    			response.setHeader("Cache-Control", "no-store");
    	        response.setDateHeader("Expires", 0);
    	        response.setHeader("Pragma", "no-cache");
    	        response.getWriter().write(sb.toString());
        		return; 
        	}
        	sb.append("<status>ok</status>\n<description>Auto control active</description></response>\n");
        }
        
        if(servletPath.equalsIgnoreCase(CONFIG_SERVLET_PATH)){
        	//config servlet
        	sb = new StringBuilder("");
        	try {
        		FileInputStream fstream = new FileInputStream(CONFIG_FILE_PATH);
        		// Get the object of DataInputStream
        		DataInputStream in = new DataInputStream(fstream);
        		BufferedReader br = new BufferedReader(new InputStreamReader(in));
        		String strLine;
        		//Read File Line By Line
    		  	while ((strLine = br.readLine()) != null){
    		  		// Print the content on the console
    		  		sb.append(strLine + "\n");
    		  	}
    		  	in.close();
        	}
        	catch(Exception e){
        		sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<response>\n");
        		sb.append("<status>exception</status>\n<description>"+ e.getClass()+": " + e.getMessage() + "</description>\n</response>");
    			response.setHeader("Cache-Control", "no-store");
    	        response.setDateHeader("Expires", 0);
    	        response.setHeader("Pragma", "no-cache");
    	        response.getWriter().write(sb.toString());
        		return; 
        	}
        }
        
        if(servletPath.equalsIgnoreCase(AIR_SERVLET_PATH)){
        	
        	try {
        		SAXBuilder builder = new SAXBuilder();
    			File xmlFile = new File(CONFIG_FILE_PATH);
    			Document doc = (Document) builder.build(xmlFile);
    			Element root = doc.getRootElement();
    			
    			//get parameters from config file
    			Element coreParameters = root.getChild("core-parameters");
    			String rabbitIP = coreParameters.getChild("rabbit-ip").getValue();
    			int freeServerPort = Integer.parseInt(coreParameters.getChild("free-server-port").getValue());
        		InetAddress localInetAddress = InetAddress.getLocalHost();
				InetAddress remoteInetAddress = InetAddress.getByName(rabbitIP);
				Control control = new Control(localInetAddress, freeServerPort, remoteInetAddress);
						
    			//set value to config file and send command to system
    			if(request.getParameter(INTAKE_PARAMETER_STRING).equalsIgnoreCase(INTAKE_NORMAL_STRING)){
    				control.setAirIntake(AirIntake.NORMAL);
    				root.getChild("state").getChild("air-intake").setText(INTAKE_NORMAL_STRING);				
    			}
    			if(request.getParameter(INTAKE_PARAMETER_STRING).equalsIgnoreCase(INTAKE_OVERRIDE_STRING)){
    				control.setAirIntake(AirIntake.OVERRIDE);
    				root.getChild("state").getChild("air-intake").setText(INTAKE_OVERRIDE_STRING);
    			}
    					
    			// save updated xml file
    	        XMLOutputter xmlOutput = new XMLOutputter();
    	   	 	xmlOutput.setFormat(Format.getPrettyFormat());
    			xmlOutput.output(doc, new FileWriter(CONFIG_FILE_PATH));
    			
        	}
        	catch(Exception e){
        		sb.append("<status>exception</status>\n<description>"+ e.getClass()+": " + e.getMessage() + "</description>\n</response>");
    			response.setHeader("Cache-Control", "no-store");
    	        response.setDateHeader("Expires", 0);
    	        response.setHeader("Pragma", "no-cache");
    	        response.getWriter().write(sb.toString());
        		return; 
        	}
        	if(request.getParameter(INTAKE_PARAMETER_STRING).equalsIgnoreCase(INTAKE_NORMAL_STRING)){
        		sb.append("<status>ok</status>\n<description>Air intake set to " + INTAKE_NORMAL_STRING + "</description></response>\n");
			}
        	else{
        		sb.append("<status>ok</status>\n<description>Air intake set to " + INTAKE_OVERRIDE_STRING + "</description></response>\n");
        	}
        	
        	
        }
    	
        //control and servlet finished successfully
		response.setHeader("Cache-Control", "no-store");
        response.setDateHeader("Expires", 0);
        response.setHeader("Pragma", "no-cache");
        response.getWriter().write(sb.toString());
       
    }

    public boolean isValid(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
    	boolean oneParameter = false;
    	
        String servletPath = request.getServletPath();
        
        if(servletPath.equalsIgnoreCase(CONTROL_SERVLET_PATH)){
        	//control servlet
        	if(request.getParameter(FAN_PARAMETER_STRING) != null && request.getParameter(FAN_PARAMETER_STRING).trim().length() != 0){
        		int fan = Integer.parseInt(request.getParameter(FAN_PARAMETER_STRING));
        		if((fan < 0)|| (fan > 5)){
        			response.sendError(WebConstants.UNSUPPORTED_REQUEST_ERROR, "Unsupported value for parameter " + FAN_PARAMETER_STRING );
        			return false;
        		}
        		else{
        			oneParameter = true;
        		}
        	}
        	if(request.getParameter(HEATER_PARAMETER_STRING) != null && request.getParameter(HEATER_PARAMETER_STRING).trim().length() != 0){
        		int heater = Integer.parseInt(request.getParameter(HEATER_PARAMETER_STRING));
        		if ((heater < 0) || (heater > 1)){
        			response.sendError(WebConstants.UNSUPPORTED_REQUEST_ERROR, "Unsupported value for parameter " + HEATER_PARAMETER_STRING + ".");
        			return false;
        		}
        		else{
        			oneParameter = true;
        		}
        	}
        	if(!oneParameter){
        		response.sendError(WebConstants.UNSUPPORTED_REQUEST_ERROR, "Unsupported parameters. Parameters for " + CONTROL_SERVLET_PATH + " are " + HEATER_PARAMETER_STRING + " and " + FAN_PARAMETER_STRING + ".");
    			return false;
        	}
        	
        }
        else{
        	if(servletPath.equalsIgnoreCase(AUTOCONTROL_SERVLET_PATH)){
        		//switch to auto control servlet
        	}
        	else{
        		if(servletPath.equalsIgnoreCase(CONFIG_SERVLET_PATH)){
        			//return passive heating config file servlet
        		}
        		else{
        			if(servletPath.equalsIgnoreCase(AIR_SERVLET_PATH)){
        				if(request.getParameter(INTAKE_PARAMETER_STRING) == null || request.getParameter(INTAKE_PARAMETER_STRING).trim().length() == 0){
        					response.sendError(WebConstants.UNSUPPORTED_REQUEST_ERROR, "Unsupported parameter. Try with \"" + INTAKE_PARAMETER_STRING + "\"" );
                			return false;
        				}
        				if(request.getParameter(INTAKE_PARAMETER_STRING).equalsIgnoreCase(INTAKE_NORMAL_STRING) == false  && request.getParameter(INTAKE_PARAMETER_STRING).equalsIgnoreCase(INTAKE_OVERRIDE_STRING) == false){
        					response.sendError(WebConstants.UNSUPPORTED_REQUEST_ERROR, "Unsupported " + INTAKE_PARAMETER_STRING + " parameter value. Allowed is: " + INTAKE_NORMAL_STRING + " or " + INTAKE_OVERRIDE_STRING + ".");
                			return false;
        				}
        				
        			}
        			else{
        				response.sendError(WebConstants.UNSUPPORTED_REQUEST_ERROR, "Unknown url-pattern for PassiveHeatingControllServlet. Check web.xml.");
        				logger.error("Validation failed. Unknown url-pattern for PassiveHeatingControllServlet. Check web.xml");
        				return false;
        			}
        			
        		}
        	}
        }        
        return true;
    }
}

