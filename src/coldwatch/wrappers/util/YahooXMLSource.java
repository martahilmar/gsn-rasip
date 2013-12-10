package coldwatch.wrappers.util;


import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import java.io.IOException;

/*
 * YahooXMLSource object is suppose to connect to Yahoo Weather xml source (exp. http://weather.yahooapis.com/forecastrss?w=851128&u=c)
 * More info about service and XML structure on: http://developer.yahoo.com/weather/
 */
public class YahooXMLSource {
	
	private String url;
	
	Document document;
	
	Builder xmlBuild;
	
	public YahooXMLSource(String url){
		this.url=url;
		xmlBuild = new Builder();
	}
	
	/*
	 *Call fetchData() to refresh data from data source 
	*/
	public void fetchData(){
		try {
			document = xmlBuild.build(this.url);
		} catch (ValidityException e) {
			e.printStackTrace();
		} catch (ParsingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * returns temperature from xml source (Yahoo weather). Before calling this call fetchData() to refresh xml source
	 */
	public int getTemperature(){
		Element root = document.getRootElement();
		Element channel = root.getFirstChildElement("channel");
		Element item = channel.getFirstChildElement("item");
		//get node with node name yweather:condition (yweather is namespace)
		Element yweatherCondition = item.getFirstChildElement("condition","http://xml.weather.yahoo.com/ns/rss/1.0");
		
		//get temperature
		String temp = new String(yweatherCondition.getAttributeValue("temp"));
		
		return Integer.parseInt(temp.trim());
		
	}
	
	/*
	 * returns humidity from xml source (Yahoo weather). Before calling this call fetchData() to refresh xml source
	 */
	public int getHumidity(){
		Element root = document.getRootElement();
		Element channel = root.getFirstChildElement("channel");
		//get node with node name yweather:atmosphere (yweather is namespace)
		Element yweatherAtmosphere = channel.getFirstChildElement("atmosphere","http://xml.weather.yahoo.com/ns/rss/1.0");
		
		//get humidity
		String temp = new String(yweatherAtmosphere.getAttributeValue("humidity"));
		
		return Integer.parseInt(temp.trim());
		
	}	

}
