package hr.fer.rasip.processingclasses;

import gsn.beans.DataField;
import gsn.beans.StreamElement;

import gsn.beans.VSensorConfig;
import gsn.vsensor.AbstractVirtualSensor;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.TreeMap;


public class WaspMoteAgricultural extends AbstractVirtualSensor{
	
	//postavi ime kolone za podatkovni dio poruke
	//predpostavljeni naziv je "data"
	private static final String DATA_FIELD_NAME = "data";
	private String dataFieldName;
	private int air_temp;
	private int battery;
	private int mv;
	private double eps;
	private double ground_hum_mineral_soil;
	private double ground_hum_potting_soil;
	private double ground_hum_rockwool;
	private int humidity;
	private int light_level;
	private int air_pressure;
	
    private static final transient Logger logger = Logger.getLogger(WaspMoteAgricultural.class);
    
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
			if(fieldNames[i].toUpperCase().equals(dataFieldName.toUpperCase())) {
				//procitaj data dio primljene poruke
				message = (String) data.getData()[i];
				//parsiraj tako da dobijes temperaturu i stanje baterije
				//ovdje dodati za citanje drugih parametara
				air_temp = Integer.parseInt(message.split("!ta!")[1]);
				battery = Integer.parseInt(message.split("!b!")[1]);
				humidity = Integer.parseInt(message.split("!ha!")[1]);
				mv = Integer.parseInt(message.split("!mv!")[1]);
				light_level = Integer.parseInt(message.split("!ll!")[1]);
				air_pressure = Integer.parseInt(message.split("!ap!")[1]);
				
				//Racunaj vrijednosti vlage zraka za razlicita tla iz dobivenog epsilona
				ground_hum_mineral_soil = 11.9e-4 * mv - 0.401;
				ground_hum_potting_soil = 2.11e-3 * mv - 0.675;
				ground_hum_rockwool = 2.63e-6 * Math.pow(mv, 2) + 5.07e-4 * mv - 0.0394;
				//eps = 1.0570e-9 * Math.pow(mv, 3) + 3.57500e-6 * Math.pow(mv, 2) - 3.95570e-3 * mv + 1.53153; 
				eps = -3.33260 * 1e-9 * Math.pow(mv, 3) + 7.02180 * 1e-6 * Math.pow(mv, 2) - 5.11647 * 1e-3 * mv + 1.30746; 
				eps = Math.pow(eps, -1);
			}
		}
    	
		StreamElement out = new StreamElement(new DataField[] {
												new DataField("air_temp","int", "Measured air temperature." ), 
												new DataField("battery", "int", "Battery percentage in mote."),
												new DataField("humidity", "int", "Measured humidity in the air"),
												new DataField("eps", "double", "Dielectric permittivity of the ground"),
												new DataField("vwc_mineral", "double", "Measured volumetric water constant for mineral soil"),
												new DataField("vwc_potting", "double", "Measured volumetric water constant for potting soil"),
												new DataField("vwc_rockwool", "double", "Measured volumetric water constant for rockwool"),
												new DataField("light_level", "int", "Measured light level"),
												new DataField("air_pressure", "int", "Measured air pressure")
											  },
											  new Serializable[]{
													air_temp, battery, humidity, eps, ground_hum_mineral_soil, 
													ground_hum_potting_soil, ground_hum_rockwool , light_level, air_pressure
													}
											);
        dataProduced(out);
    }

    public void dispose(){

    }
}

