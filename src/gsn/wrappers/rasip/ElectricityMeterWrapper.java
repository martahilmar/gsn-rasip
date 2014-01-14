package gsn.wrappers.rasip;

import gsn.beans.DataField;
import gsn.beans.DataTypes;
import gsn.beans.StreamElement;

import java.io.File;
import java.io.Serializable;

import org.apache.log4j.Logger;

import gsn.wrappers.*;
import gsn.electricity.*;

public class ElectricityMeterWrapper extends AbstractWrapper{
    
    private static final int            DEFAULT_SAMPLING_RATE	= 5000;
    private int                         samplingRate			= DEFAULT_SAMPLING_RATE;
    private static int                  threadCounter			= 0;
    private final transient Logger      logger					= Logger.getLogger(ElectricityMeterWrapper.class);
      
    private static final String       FIELD_CURRENT_POWER_T1	= "CURRENT_POWER_T1";
    private static final String       FIELD_MAX_CUMM_POWER_T1	= "MAX_CUMM_POWER_T1";
    private static final String       FIELD_POWER				= "POWER";
    private static final String       FIELD_POWER_T1			= "POWER_T1";
	private static final String       ENERGY_PHASE_1			= "ENERGY_PHASE_1";
	private static final String       ENERGY_PHASE_2			= "ENERGY_PHASE_2";
	private static final String       ENERGY_PHASE_3			= "ENERGY_PHASE_3";

	
    
    private transient DataField [ ] outputStructureCache = new DataField[] { new DataField( FIELD_CURRENT_POWER_T1, "double", "Current power tarrife one" ),
            new DataField( FIELD_MAX_CUMM_POWER_T1, "double" , "Maximum cummulative poweer tarrife one." ), new DataField( FIELD_POWER, "double", "Electricity Power" ),
            new DataField( FIELD_POWER_T1, "double", "Electricity Power" ),new DataField( ENERGY_PHASE_1, "double", "Energy phase 1" ),new DataField( ENERGY_PHASE_2, "double", "Energy phase 2" ),new DataField( ENERGY_PHASE_3, "double", "Energy phase 3" )};
    
    private static final String [ ] FIELD_NAMES = new String [ ] { FIELD_CURRENT_POWER_T1, FIELD_MAX_CUMM_POWER_T1, FIELD_POWER, FIELD_POWER_T1,ENERGY_PHASE_1,ENERGY_PHASE_2,ENERGY_PHASE_3 };
    
	KomunikatorMeter comunicator;
	double current_power_t1, max_cumm_power_t1, power, power_t1,energy_phase1,energy_phase2,energy_phase3;
	
    public boolean initialize() {
        logger.info("Initializing ElectricityMeterWrapper Class");
        String javaVersion = System.getProperty("java.version");
        if(!javaVersion.startsWith("1.6")){
            logger.error("Error in initializing DiskSpaceWrapper because of incompatible jdk version: " + javaVersion + " (should be 1.6.x)");
            return false;
        }
        setName("ElectricityMeterWrapper-Thread" + (++threadCounter));
        return true;
    }
    
    public void run(){
        while(isActive()){
            try{
                Thread.sleep(samplingRate);
            }catch (InterruptedException e){
                logger.error(e.getMessage(), e);
            }
			
            comunicator = new KomunikatorMeter();
            
            try {

    			byte[] zahtjev = new byte[] { 0x01, 0x52, 0x31, 0x02, 
			0x36, 0x31, 0x2e, 0x38, 0x2e, 0x30, 0x28, 0x29, 0x03 };			
				comunicator.Connect();
				
    			current_power_t1 = comunicator.GetCurrentPowerTariff1();
    			//power = comunicator.GetCurrentPowerTariff2();
    			max_cumm_power_t1 = comunicator.GetMaxCummulativePowerTariff1();
    			//power = comunicator.GetMaxCummulativePowerTariff2();
    			power = comunicator.GetPower();
    			power_t1 = comunicator.GetPowerTariffOne();
				//power = comunicator.GetPowerTariffTwo();
    			energy_phase1 =  comunicator.GetEnergyPhase1();
    			energy_phase2 =  comunicator.GetEnergyPhase2();
    			energy_phase3 =  comunicator.GetEnergyPhase3();
    			
				comunicator.Close();

				//System.out.println("******************** Vrijdnosti **********************" + power);
    			//System.out.println("BCC vrijednost 1: " + GetBCC(zahtjev));

    		} catch (Exception e) {
    			System.out.println(e.getMessage());

    		}
            
            StreamElement streamElement = new StreamElement( FIELD_NAMES , new Byte [ ] { DataTypes.DOUBLE, DataTypes.DOUBLE, DataTypes.DOUBLE, DataTypes.DOUBLE,DataTypes.DOUBLE,DataTypes.DOUBLE,DataTypes.DOUBLE } , new Serializable [ ] { current_power_t1,
            		max_cumm_power_t1, power, power_t1,energy_phase1,energy_phase2,energy_phase3 }, System.currentTimeMillis( ) );
            
            postStreamElement(streamElement);
        }
    }
    
	public static byte GetBCC(byte[] inputStream) {
		byte bcc = 0;

		if (inputStream != null && inputStream.length > 0) {
			// Exclude SOH during BCC calculation
			for (int i = 1; i < inputStream.length; i++) {
				bcc ^= inputStream[i];
			}
		}

		return bcc;
	}
    
    public void dispose() {
        threadCounter--;
    }
    
    public String getWrapperName() {
        return "Electricity Meter";
    }
    
    public DataField[] getOutputFormat() {
        return outputStructureCache;
    }
    
}