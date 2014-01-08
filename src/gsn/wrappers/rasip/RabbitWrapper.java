package gsn.wrappers.rasip;

import gsn.beans.DataField;
import gsn.beans.AddressBean;
import gsn.wrappers.AbstractWrapper;
import gsn.wrappers.MemoryMonitoringWrapper;
import gsn.beans.AddressBean;
import gsn.beans.DataTypes;
import gsn.beans.StreamElement;
import gsn.utils.ParamParser;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class RabbitWrapper extends AbstractWrapper {
	private static final int DEFAULT_SAMPLING_RATE = 60000;
	private int samplingRate = DEFAULT_SAMPLING_RATE;
	
	private final transient Logger logger = Logger.getLogger(RabbitWrapper.class);

	private static int threadCounter = 0;
	
	private static final String FIELD_NAME_TEMPERATURA = "TEMPERATURA";
	private static final String FIELD_NAME_GRAF="GRAF";
    private static final String[] FIELD_NAMES = new String[] { FIELD_NAME_TEMPERATURA };
	
	private transient DataField[] outputStructureCache = new DataField[] {
			new DataField(FIELD_NAME_TEMPERATURA, "int", "Temperatura na zavodu RASIP")};
	  
	@Override
	public boolean initialize() {
		setName("RabbitWrapper-Thread" + (++threadCounter));
		AddressBean addressBean = getActiveAddressBean();
		if (addressBean.getPredicateValue("sampling-rate") != null) {
			samplingRate = ParamParser.getInteger(addressBean.getPredicateValue("sampling-rate"),DEFAULT_SAMPLING_RATE);
			if (samplingRate <= 0 || samplingRate > 36000000) {
				logger.warn("Frekvencija citanja podataka za 'wrapper' mora biti cijeli broj izmedju 0 i 36000000.\n GSN ce koristiti frekvenciju citanja ("
						+ DEFAULT_SAMPLING_RATE + "h ).");
				samplingRate = DEFAULT_SAMPLING_RATE;				
			}
		}
		return true;
	}

	public void run(){
		while(isActive()){
			try {
				Thread.sleep(samplingRate);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
			   
			StreamElement streamElement = new StreamElement(FIELD_NAMES, new Byte[] {DataTypes.INTEGER }, new Serializable[] {getTemperature()}, System.currentTimeMillis());
			postStreamElement(streamElement);
		}
	}

	private Serializable getTemperature() {
		int temperature = 0;
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = null;
		try {
			saxParser = factory.newSAXParser();
		} catch (ParserConfigurationException e1) {
			logger.error(e1.getMessage());
			return null;
		} catch(SAXException e1){
            logger.error(e1.getMessage());
            return null;
        }
		String resource = "http://161.53.67.199/report.xml";

		DefaultHandler handler = new DefaultHandler() {
			boolean bTemperature = false;
			public String temperature;
			public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
				if(qName.equalsIgnoreCase("TEMPERATURE"))
					bTemperature = true;
			}
			
			public void endElement(String uri, String localName,
					String qName) throws SAXException {		 
		    }
			
		   public void characters(char ch[], int start, int length) throws SAXException {				 
				if (bTemperature) {
					temperature = new String(ch, start, length);
					bTemperature = false;
				} 
			}

		   @Override
		   public String toString()
		   {
			   return temperature.trim();
		   }
		};
		
		try {
			if(saxParser != null)
				saxParser.parse(resource, handler);
			if(handler.toString() != null && handler.toString() != "")			
				temperature = Integer.parseInt(handler.toString());
		} catch (Exception e) {
		    logger.error(e.getMessage() + handler.toString());
		    return null;
		}
		
		return temperature;
	}


	@Override
	public void dispose() {
		threadCounter--;
	}

	@Override
	public String getWrapperName() {
	   return "Rabbit Temperature wrapper";
	}
	
    @Override
	public DataField[] getOutputFormat() {
		return outputStructureCache;
	}
}