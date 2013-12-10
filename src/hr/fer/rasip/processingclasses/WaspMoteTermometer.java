package hr.fer.rasip.processingclasses;

import gsn.beans.DataField;
import gsn.beans.StreamElement;

import gsn.beans.VSensorConfig;
import gsn.vsensor.AbstractVirtualSensor;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.TreeMap;


public class WaspMoteTermometer extends AbstractVirtualSensor{
	
	//postavi ime kolone za podatkovni dio poruke
	//predpostavljeni naziv je "data"
	private static final String DATA_FIELD_NAME = "data";
	private String dataFieldName;
	private int temperature;
	private int battery;
	
    private static final transient Logger logger = Logger.getLogger(WaspMoteTermometer.class);
    
    public boolean initialize() {
    	VSensorConfig vsensor = getVirtualSensorConfiguration();
        TreeMap<String, String> params = vsensor.getMainClassInitialParams();
        
        if (params.get("data-field-name") ==  null){
        	dataFieldName = DATA_FIELD_NAME;
        }
        else{
        	dataFieldName = params.get("data-field-name");
        }
        return true;
    }

    public void dataAvailable(String inputStreamName, StreamElement data) {
    	
    	String [] fieldNames = data.getFieldNames();
    	String message;
		for(int i=0; i < fieldNames.length; i++) {
			if(fieldNames[i].equals(dataFieldName.toUpperCase())) {
				//procitaj data dio primljene poruke
				message = (String) data.getData()[i];
				//parsiraj tako da dobijes temperaturu i stanje baterije
				//ovdje dodati za citanje drugih parametara
				temperature = Integer.parseInt(message.split("!t!")[1]);
				battery = Integer.parseInt(message.split("!b!")[1]);
			}
		}
    	
		StreamElement out = new StreamElement(new DataField[] {new DataField("temperature","int", "Measured temperature" ), 
											  new DataField("battery", "int", "Battery percentage in mote")},
											  new Serializable[]{temperature,battery});
        dataProduced(out);
    }

    public void dispose(){

    }
}

