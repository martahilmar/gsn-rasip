package hr.fer.rasip.processingclasses;

import gsn.beans.DataField;
import gsn.beans.StreamElement;

import gsn.beans.VSensorConfig;
import gsn.vsensor.AbstractVirtualSensor;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.TreeMap;


public class AnalogWaspMoteAgricultural extends AbstractVirtualSensor{
	
	//postavi ime kolone za podatkovni dio poruke
	//predpostavljeni naziv je "data"
	private static final String DATA_FIELD_NAME = "data";
	private String dataFieldName;
	private int air_temp;
	private int battery;
	private double mv;
	private double eps;
	private double ground_hum_mineral_soil;
	private double ground_hum_potting_soil;
	private double ground_hum_rockwool;
	private int humidity;
	private int light_level;
	
    private static final transient Logger logger = Logger.getLogger(AnalogWaspMoteAgricultural.class);
    
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
				
				//Parsiranje svojstava
				air_temp = Integer.parseInt(message.split("!ta!")[1]);
				battery = Integer.parseInt(message.split("!b!")[1]);
				humidity = Integer.parseInt(message.split("!ha!")[1]);
				mv = Integer.parseInt(message.split("!mv!")[1]); //Dobivanje eps_a iz eps_raw
				light_level = Integer.parseInt(message.split("!ll!")[1]);
				
				//Provjera greske
				if(eps == 4095) { //4095 oznacava gresku, stavljamo sve na 0
					eps = 0;
					ground_hum_mineral_soil = 0;
					ground_hum_potting_soil = 0;
					ground_hum_rockwool = 0;
				} else {
					eps = -3.33260 * Math.pow(10, -9) * Math.pow(mv, 3) + 7.02180 * Math.pow(10, -6) * Math.pow(mv, 2) - 5.11647 * Math.pow(10, -3) * mv + 1.30746;
					eps = 1.0/eps; //Racunanje permitivnosti
					//Racunaj vrijednosti vlage zraka za razlicita tla iz dobivenog epsilona
					ground_hum_mineral_soil = 11.9e-4 * mv - 0.401;
					ground_hum_potting_soil = 2.11e-3 * mv - 0.675;
					ground_hum_rockwool = 2.63e-6 * Math.pow(mv, 2) + 5.07e-4 * mv - 0.0394;
				}
			}
		}
    	
		StreamElement out = new StreamElement(new DataField[] {
												new DataField("air_temperature","int", "Measured air temperature." ), 
												new DataField("battery", "int", "Battery percentage in mote."),
												new DataField("humidity", "int", "Measured humidity in the air"),
												new DataField("vwc_mineral", "double", "Measured volumetric water content for mineral soil"),
												new DataField("vwc_potting", "double", "Measured volumetric water content for potting soil"),
												new DataField("vwc_rockwool", "double", "Measured volumetric water content for rockwool"),
												new DataField("permittivity", "double", "Measured dielectric permittivity of soil"),
												new DataField("light_level", "int", "Measured light level")
											  },
											  new Serializable[]{
													air_temp, 
													battery, 
													humidity, 
													ground_hum_mineral_soil, 
													ground_hum_potting_soil, 
													ground_hum_rockwool, 
													eps, 
													light_level
													}
											);
        dataProduced(out);
    }

    public void dispose(){

    }
}