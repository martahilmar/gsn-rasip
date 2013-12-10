package coldwatch.wrappers;

import gsn.beans.DataField;
import gsn.wrappers.AbstractWrapper;
import gsn.beans.AddressBean;
import gsn.utils.ParamParser;

import java.io.Serializable;

import org.apache.log4j.Logger;

import coldwatch.wrappers.util.YahooXMLSource;

public class YahooWeatherState extends AbstractWrapper {
	
	private static final String FIELD_NAME_TEMPERATURE = "temperature";
	
	private static final String FIELD_NAME_HUMIDITY = "humidity";

	private static final int DEFAULT_SAMPLING_RATE = 10000; //10 seconds
	
	private static final String DEFAULT_WOEID = "851128"; // where on earth id from yahoo weather for Zagreb Croatia
	
	private YahooXMLSource yahooXML;
	
	private int samplingRate = DEFAULT_SAMPLING_RATE;
	
	private String woeid;

	private final transient Logger logger = Logger.getLogger(YahooWeatherState.class);

	private static int threadCounter = 0;

	private transient DataField[] collection = new DataField[] {
			new DataField(FIELD_NAME_TEMPERATURE, "int", "Temperature from yahoo weather for choosen place "),
			new DataField(FIELD_NAME_HUMIDITY, "int", "Humidity from yahoo weather for choosen place ")};


	@Override
	public void dispose() {
		threadCounter--;
	}

	@Override
	public DataField[] getOutputFormat() {
		return collection;
	}

	@Override
	public String getWrapperName() {
		return "YahooWeatherState";
	}

	@Override
	public boolean initialize() {
		setName("YahooWeatherState-Thread" + (++threadCounter));

		AddressBean addressBean = getActiveAddressBean();
		
		if (addressBean.getPredicateValue("sampling-rate") != null){
			samplingRate = ParamParser.getInteger(addressBean.getPredicateValue("sampling-rate"), DEFAULT_SAMPLING_RATE);
			if (samplingRate < 10000 || samplingRate>36000000) {
				logger.warn("Sampling rate must be from interval 10000 < sampling-rate < 36000000. It is set to default: "+ DEFAULT_SAMPLING_RATE + " ms");
				samplingRate = DEFAULT_SAMPLING_RATE;
			}
		}
		else{
			logger.warn("Sampling rate set to default: " + DEFAULT_SAMPLING_RATE +" ms");
		}
			
		woeid = addressBean.getPredicateValue("woeid");
		if(woeid == null){
			woeid = DEFAULT_WOEID;
			logger.warn("WOEID (where on earth ID - yahoo weather) isn'set and wrapper is collecting data for Zagreb, Croatia. Check your WOEID on http://weather.yahoo.com");
		}
		
		yahooXML = new YahooXMLSource("http://weather.yahooapis.com/forecastrss?w=" + woeid + "&u=c");
		return true;
	}
	
	public void run(){
		while(isActive()){

           try {
				Thread.sleep(samplingRate);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
			//connect to XML source and get data
			yahooXML.fetchData();
            logger.warn("Grabbing data " + yahooXML.getTemperature());
			//extract temperature and humidity and post it
			postStreamElement(new Serializable[] { yahooXML.getTemperature(), yahooXML.getHumidity() }); 

		}
	}
}
