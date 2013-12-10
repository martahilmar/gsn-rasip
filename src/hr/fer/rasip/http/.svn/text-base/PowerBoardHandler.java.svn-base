package hr.fer.rasip.http;

import gsn.http.*;
import gsn.Main;
import gsn.Mappings;
import gsn.beans.DataTypes;
import gsn.beans.StreamElement;
import gsn.beans.VSensorConfig;
//import gsn.http.accesscontrol.User;
import gsn.http.ac.User;
import gsn.storage.DataEnumerator;
//import hr.fer.rasip.RabbitRasip;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.TreeMap;
import hr.fer.rasip.smartpower.*;

public class PowerBoardHandler implements RequestHandler {

    private static transient Logger logger = Logger.getLogger(PowerBoardHandler.class);

    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {


        SimpleDateFormat sdf = new SimpleDateFormat(Main.getInstance().getContainerConfig().getTimeFormat());
        
        String id = request.getParameter("id");
        Integer socket = Integer.parseInt(request.getParameter("socket"));
        String action = request.getParameter("action");
        
        StringBuilder sb = new StringBuilder("<result>\n");
        
        
        PowerSupply supply = null;
        
        PowerBoard board = null;
		
        try {
			supply = PowerSupplyConfig.loadFromFile("./conf/smartpower.xml");
		} catch (FileNotFoundException e) {
			sb.append("<info>" + e.getMessage() + "</info>\n");
		} catch (ConfigException e) {
			sb.append("<info>" + e.getMessage() + "</info>\n");
		} catch (IOException e) {
			sb.append("<info>" + e.getMessage() + "</info>\n");
		}
        
        try {
			board = supply.getBoard(id);
		} catch (UnknownBoardException e1) {
			response.sendError(WebConstants.BOARD_NOT_EXIST,"Board with specified ID does not exist.");
			//sb.append("<info>" + e1.getMessage() + "</info>\n");
		}
        
		
		if (socket == 0) {
        	int numberOfSockets = board.getNumberOfSockets();
        	  
        	if (action.equalsIgnoreCase("on")) {
             	for (int i = 1; i <= numberOfSockets; i++ ) {
             		try {
						supply.socketOn(id, i);
						sb.append("<info><board>" + id + "</board><socket>" + i + "</socket><status>On</status></info>\n");
					} catch (UnknownCommandException e) {
						sb.append("<info><board>" + id + "</board><socket>" + i + "</socket><status>" + e.getMessage() + "</status></info>\n");
					} catch (UnknownSocketException e) {
						sb.append("<info><board>" + id + "</board><socket>" + i + "</socket><status>" + e.getMessage() + "</status></info>\n");
					} catch (UnknownBoardException e) {
						sb.append("<info><board>" + id + "</board><socket>" + i + "</socket><status>" + e.getMessage() + "</status></info>\n");
					} catch (ResponseTimeoutException e) {
						sb.append("<info><board>" + id + "</board><socket>" + i + "</socket><status>" + e.getMessage() + "</status></info>\n");
					} catch (SmartPowerException e) {
						sb.append("<info><board>" + id + "</board><socket>" + i + "</socket><status>" + e.getMessage() + "</status></info>\n");
					}
             	}
            } else if (action.equalsIgnoreCase("off")) {
        		for (int i = 1; i <= numberOfSockets; i++ ) {
             		try {
						supply.socketOff(id, i);
						sb.append("<info><board>" + id + "</board><socket>" + i + "</socket><status>Off</status></info>\n");
					} catch (UnknownCommandException e) {
						sb.append("<info><board>" + id + "</board><socket>" + i + "</socket><status>" + e.getMessage() + "</status></info>\n");
					} catch (UnknownSocketException e) {
						sb.append("<info><board>" + id + "</board><socket>" + i + "</socket><status>" + e.getMessage() + "</status></info>\n");
					} catch (UnknownBoardException e) {
						sb.append("<info><board>" + id + "</board><socket>" + i + "</socket><status>" + e.getMessage() + "</status></info>\n");
					} catch (ResponseTimeoutException e) {
						sb.append("<info><board>" + id + "</board><socket>" + i + "</socket><status>" + e.getMessage() + "</status></info>\n");
					} catch (SmartPowerException e) {
						sb.append("<info><board>" + id + "</board><socket>" + i + "</socket><status>" + e.getMessage() + "</status></info>\n");
					}
             	}
        	} else {
        		response.sendError(WebConstants.SOCKET_ACTION_ERROR,"Socket action can be only on or off.");
        	}
        } else {
        	if (action.equalsIgnoreCase("on")) { 
        		try {
        			supply.socketOn(id, socket);
					sb.append("<info><board>" + id + "</board><socket>" + socket + "</socket><status>On</status></info>\n");
				} catch (UnknownCommandException e) {
					sb.append("<info><board>" + id + "</board><socket>" + socket + "</socket><status>" + e.getMessage() + "</status></info>\n");
				} catch (UnknownSocketException e) {
					sb.append("<info><board>" + id + "</board><socket>" + socket + "</socket><status>" + e.getMessage() + "</status></info>\n");
				} catch (UnknownBoardException e) {
					sb.append("<info><board>" + id + "</board><socket>" + socket + "</socket><status>" + e.getMessage() + "</status></info>\n");
				} catch (ResponseTimeoutException e) {
					sb.append("<info><board>" + id + "</board><socket>" + socket + "</socket><status>" + e.getMessage() + "</status></info>\n");
				} catch (SmartPowerException e) {
					sb.append("<info><board>" + id + "</board><socket>" + socket + "</socket><status>" + e.getMessage() + "</status></info>\n");
				}
        	} else if (action.equalsIgnoreCase("off")) {
        		try {
					supply.socketOff(id, socket);
					sb.append("<info><board>" + id + "</board><socket>" + socket + "</socket><status>Off</status></info>\n");
				} catch (UnknownCommandException e) {
					sb.append("<info><board>" + id + "</board><socket>" + socket + "</socket><status>" + e.getMessage() + "</status></info>\n");
				} catch (UnknownSocketException e) {
					sb.append("<info><board>" + id + "</board><socket>" + socket + "</socket><status>" + e.getMessage() + "</status></info>\n");
				} catch (UnknownBoardException e) {
					sb.append("<info><board>" + id + "</board><socket>" + socket + "</socket><status>" + e.getMessage() + "</status></info>\n");
				} catch (ResponseTimeoutException e) {
					sb.append("<info><board>" + id + "</board><socket>" + socket + "</socket><status>" + e.getMessage() + "</status></info>\n");
				} catch (SmartPowerException e) {
					sb.append("<info><board>" + id + "</board><socket>" + socket + "</socket><status>" + e.getMessage() + "</status></info>\n");
				}
        		
        	} else {
        		response.sendError(WebConstants.SOCKET_ACTION_ERROR,"Socket action can be only on or off.");
        	}
        	
        }
        
		sb.append("</result>");
		response.setHeader("Cache-Control", "no-store");
        response.setDateHeader("Expires", 0);
        response.setHeader("Pragma", "no-cache");
        response.getWriter().write(sb.toString());
       
    }

    public boolean isValid(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
    	String vsName = request.getParameter("name");
    	HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
       
        String conditionTemperature = request.getParameter("socket");
        if (conditionTemperature == null || conditionTemperature.trim().length() == 0) {
        	response.sendError(WebConstants.MISSING_SOCKET_NUMBER, "Socket number not specified.");
        	return false;
        }
        
        String XML = request.getParameter("action");
        if (XML == null || XML.trim().length() == 0) {
        	response.sendError(WebConstants.MISSING_ACTION_DEFINITION,"Action definition not specified.");
        	return false;
        }
        
        String ID = request.getParameter("id");
        if (ID == null || ID.trim().length() == 0) {
        	response.sendError(WebConstants.MISSING_BOARD_ID,"Board ID not specified.");
        	return false;
        }
        
       
        
        if (Main.getContainerConfig().isAcEnabled() == true) {
            
            if (user != null)
                if (user.hasReadAccessRight(vsName) == false && user.isAdmin() == false)  // ACCESS_DENIED
                {
                    response.sendError(WebConstants.ACCESS_DENIED, "Access denied to the specified virtual sensor .");
                    //response.sendError(response.SC_UNAUTHORIZED);

                    return false;
                }

        }


        return true;
    }

}

