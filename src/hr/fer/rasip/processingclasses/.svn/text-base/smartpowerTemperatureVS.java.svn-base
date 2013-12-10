package hr.fer.rasip.processingclasses;

import gsn.beans.StreamElement;
import gsn.vsensor.AbstractVirtualSensor;
import gsn.beans.VSensorConfig;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.TreeMap;
import hr.fer.rasip.smartpower.*;


public class smartpowerTemperatureVS extends AbstractVirtualSensor {

    private static final transient Logger logger = Logger.getLogger(smartpowerTemperatureVS.class);
    private boolean allow_nulls = true; // by default allow nulls
    private TreeMap<String, String> params;
    private int socket;
    private String idletva;
    private String xmlconfig;
    
    private Integer temp;
    
    
    public boolean initialize() {
      
    	VSensorConfig vsensor = getVirtualSensorConfiguration();
        params = vsensor.getMainClassInitialParams();

        String allow_nulls_str = params.get("allow-nulls");
        if (allow_nulls_str != null)
            allow_nulls = allow_nulls_str.equalsIgnoreCase("true"); 
        
        socket = Integer.parseInt(params.get("socket"));
        idletva = params.get("idletva");
        temp = Integer.parseInt(params.get("temp"));
        xmlconfig = params.get("xmlconfig");   
        
        return true;
    }

    public void dataAvailable(String inputStreamName, StreamElement data) { 
    	
    	int temperature = (Integer) data.getData("temperature");
    	
        dataProduced(data);
        
        if (logger.isDebugEnabled()) logger.debug("Data received under the name: " + inputStreamName);
        
       PowerSupply supply = null;
        
        try {
			supply = PowerSupplyConfig.loadFromFile(xmlconfig);
		} catch (FileNotFoundException e) {
			e.getMessage();
			e.printStackTrace();
		} catch (ConfigException e) {
			e.getMessage();
			e.printStackTrace();
		} catch (IOException e) {
			e.getMessage();
			e.printStackTrace();
		}
       
        
        
        PowerBoard board = null;
		
        try {
			board = supply.getBoard(idletva);
		} catch (UnknownBoardException e1) {
			e1.getMessage();
			e1.printStackTrace();
		}
		
		if ((board != null) && (socket == 0)) {
        	
			int numberOfSockets = board.getNumberOfSockets();
        	  
        	if (temperature <= temp) {
             	for (int i = 1; i <= numberOfSockets; i++ ) {
             		try {
						supply.socketOff(idletva, i);
						
					} catch (UnknownCommandException e) {
						logger.warn(e.getMessage());
						e.printStackTrace();
					} catch (UnknownSocketException e) {
						logger.warn(e.getMessage());
						e.printStackTrace();
					} catch (UnknownBoardException e) {
						logger.warn(e.getMessage());
						e.printStackTrace();
					} catch (ResponseTimeoutException e) {
						logger.warn(e.getMessage());
						e.printStackTrace();
					} catch (SmartPowerException e) {
						logger.warn(e.getMessage());
						e.printStackTrace();
					}
             	}
            }
        	else {
        		for (int i = 1; i <= numberOfSockets; i++ ) {
             		
						try {
							supply.socketOn(idletva, i);
						} catch (UnknownCommandException e) {
							logger.warn(e.getMessage());
							e.printStackTrace();
						} catch (UnknownSocketException e) {
							logger.warn(e.getMessage());
							e.printStackTrace();
						} catch (UnknownBoardException e) {
							logger.warn(e.getMessage());
							e.printStackTrace();
						} catch (ResponseTimeoutException e) {
							logger.warn(e.getMessage());
							e.printStackTrace();
						} catch (SmartPowerException e) {
							logger.warn(e.getMessage());
							e.printStackTrace();
						}
						
        		}
             }
		} else {
        
	        if ( temperature > temp ){
	        		try {
						supply.socketOn(idletva, socket);
					} catch (UnknownCommandException e) {
						logger.warn(e.getMessage());
						e.printStackTrace();
					} catch (UnknownSocketException e) {
						logger.warn(e.getMessage());
						e.printStackTrace();
					} catch (ResponseTimeoutException e) {
						logger.warn(e.getMessage());
						e.printStackTrace();
					} catch (SmartPowerException e) {
						logger.warn(e.getMessage());
						e.printStackTrace();
					}
	        } else {
	            try {
					supply.socketOff(idletva, socket);
				} catch (UnknownCommandException e) {
					logger.warn(e.getMessage());
					e.printStackTrace();
				} catch (UnknownSocketException e) {
					logger.warn(e.getMessage());
					e.printStackTrace();
				} catch (ResponseTimeoutException e) {
					logger.warn(e.getMessage());
					e.printStackTrace();
				} catch (SmartPowerException e) {
					logger.warn(e.getMessage());
					e.printStackTrace();
				}
	        } 
		}
    
				
    }

    public boolean areAllFieldsNull(StreamElement data) {
        boolean allFieldsNull = false;
        for (int i = 0; i < data.getData().length; i++)
            if (data.getData()[i] == null) {
                allFieldsNull = true;
                break;
            }

        return allFieldsNull;
    }

    public void dispose() {
    	
    }

}