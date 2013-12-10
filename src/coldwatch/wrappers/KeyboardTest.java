package coldwatch.wrappers;
import java.io.*;
import gsn.beans.AddressBean;
import gsn.beans.DataField;
import gsn.wrappers.AbstractWrapper;

import java.io.Serializable;

import org.apache.log4j.Logger;

/*This wrapper is for testing purposes. It reads integer temperature from testing/testFile.txt every second. Different period of file readings
 * can be set with rate predicate in Virtual Sensor configuration file.
*/

public class KeyboardTest extends AbstractWrapper {
	
  private DataField[] collection = new DataField[] {new DataField("temperature", "int", "Presents integer reading from .txt file")};
  private final transient Logger logger = Logger.getLogger(KeyboardTest.class);
  private int counter;
  private AddressBean params;
  private long rate = 1000;
  private String sourceFile = new String("testing/testFile.txt");
  
  public boolean initialize() {
    setName("KeyboardTest" + counter++);
    
    params = getActiveAddressBean();
    
    if ( params.getPredicateValue( "rate" ) != null ){
    	rate = (long) Integer.parseInt( params.getPredicateValue( "rate"));
    	logger.info("Sampling rate set to " + params.getPredicateValue( "rate") + " msec.");
    }
    if (params.getPredicateValue("source-file") != null){
    	sourceFile = params.getPredicateValue("source-file");
    	logger.info("Source file set to " + sourceFile + ".");
    }
    
    return true;
  }

  public void run() {
    int temperature = 0;

    while (isActive()) {
      try { 
        Thread.sleep(rate); //delay
      } catch (InterruptedException e) {
        logger.error(e.getMessage(), e);
      }
      //read value from file
      try {
    	    BufferedReader in = new BufferedReader(new FileReader(sourceFile));
    	    String str;
    	    while ((str = in.readLine()) != null) {
    	        temperature = Integer.parseInt(str); 
    	    }
    	    in.close();
    	} catch (IOException e) {
    		logger.error("Unable to read from file: " + sourceFile);
    	}
      
      // post the data to GSN
      postStreamElement(new Serializable[] {temperature});       
    }
  }

  public DataField[] getOutputFormat() {
    return collection;
  }

  public String getWrapperName() {
    return "Keyboard testing wrapper";
  }  

  public void dispose() {
    counter--;
  }
}
