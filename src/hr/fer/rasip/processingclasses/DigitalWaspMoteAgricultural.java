package hr.fer.rasip.processingclasses;

import gsn.beans.DataField;
import gsn.beans.StreamElement;

import gsn.beans.VSensorConfig;
import gsn.vsensor.AbstractVirtualSensor;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.TreeMap;


public class DigitalWaspMoteAgricultural extends AbstractVirtualSensor{
	
	//postavi ime kolone za podatkovni dio poruke
	//predpostavljeni naziv je "data"
	private static final String DATA_FIELD_NAME = "data";
	private String dataFieldName;
	private int air_temp;
	private int battery;
	private double eps;
	private double ground_hum_mineral_soil;
	private double ground_hum_potting_soil;
	private double ground_hum_rockwool;
	private double ground_hum_perlite;
	private int humidity;
	private int light_level;
	private double earth_temp;
	
    private static final transient Logger logger = Logger.getLogger(DigitalWaspMoteAgricultural.class);
    
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
				eps = Integer.parseInt(message.split("!ea!")[1]); //Dobivanje eps_a iz eps_raw
				light_level = Integer.parseInt(message.split("!ll!")[1]);
				earth_temp = Integer.parseInt(message.split("!te!")[1]); //T_raw
				
				//Provjera greske
				if(eps == 4095) { //4095 oznacava gresku, stavljamo sve na 0
					eps = 0;
					ground_hum_mineral_soil = 0;
					ground_hum_potting_soil = 0;
					ground_hum_rockwool = 0;
					ground_hum_perlite = 0;
				} else {
					eps = eps / 50.0;
					//Racunaj vrijednosti vlage zraka za razlicita tla iz dobivenog epsilona
					ground_hum_mineral_soil = 4.3e-6 * Math.pow(eps, 3) - 5.5e-4 * Math.pow(eps, 2) + 2.92e-2 * eps - 5.3e-2;
					ground_hum_potting_soil = 2.25e-5 * Math.pow(eps, 3) - 2.06e-3 * Math.pow(eps, 2) + 7.24e-2 * eps - 0.247;
					ground_hum_rockwool = -1.68e-3 * Math.pow(eps, 2) + 6.56e-2 * eps + 0.0266;
					ground_hum_perlite = -1.07e-3 * Math.pow(eps, 2) + 5.25e-2 * eps - 0.0685;
				}
				
				if(earth_temp == 1023) {//1023 oznacava gresku, stavljamo apsolutnu nulu
					earth_temp = -273.15;
				} else {
					//Racunanje temperature iz T_raw
					if(earth_temp > 900) {
						earth_temp = 900 + 5 * (earth_temp - 900);  
					}
				
					earth_temp = (earth_temp - 400) / 10.0; //U stupnjevima Celsiusa
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
												new DataField("vwc_perlite", "double", "Measured volumetric water content for perlite"),
												new DataField("light_level", "int", "Measured light level"),
												new DataField("earth_temperature", "double", "Measured temperature of the earth")
											  },
											  new Serializable[]{
													air_temp, 
													battery, 
													humidity, 
													ground_hum_mineral_soil, 
													ground_hum_potting_soil, 
													ground_hum_rockwool, 
													ground_hum_perlite, 
													light_level, 
													earth_temp
													}
											);
        dataProduced(out);
    }

    public void dispose(){

    }
}

