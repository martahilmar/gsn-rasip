package hr.fer.rasip.wrappers.util;

import java.io.IOException;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

public class RabbitRasip {
	
	private String url;
	
	Document doc;
	
	Builder xmlBuild;
	
	public RabbitRasip(String url){
		this.url=url;
		xmlBuild = new Builder();
	}
	
	private void refreshData(){
		try {
			doc = xmlBuild.build(this.url);
		} catch (ValidityException e) {
			e.printStackTrace();
		} catch (ParsingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getTemperature(){
		this.refreshData();
		Element root = doc.getRootElement();
		Element temperature = root.getFirstChildElement("Temperature");
		String temp = new String(temperature.getValue());
		
		return Integer.parseInt(temp.trim());
		
	}
	
	public String toString(){
		return "Temperatura: " + String.valueOf(getTemperature());
	}

}
