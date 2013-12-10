package hr.fer.rasip.wrappers;

import gsn.beans.DataField;
import gsn.wrappers.AbstractWrapper;
import gsn.wrappers.MemoryMonitoringWrapper;
import gsn.beans.AddressBean;
import gsn.beans.DataTypes;
import gsn.beans.StreamElement;
import gsn.utils.ParamParser;

import java.io.Serializable;

import org.apache.log4j.Logger;

import hr.fer.rasip.wrappers.util.RabbitRasip;

public class XMLReader extends AbstractWrapper {
	
	private RabbitRasip rr;

	private static final int DEFAULT_SAMPLING_RATE = 1;

	private int samplingRate = DEFAULT_SAMPLING_RATE;

	private final transient Logger logger = Logger
			.getLogger(MemoryMonitoringWrapper.class);

	private static int threadCounter = 0;

	private transient DataField[] outputStructureCache = new DataField[] {
			new DataField(FIELD_NAME_TEMPERATURA, "int", "Temperatura na zavodu RASIP"), new DataField(FIELD_NAME_GRAF, "Binary","Graficki prikaz") };

	private static final String FIELD_NAME_TEMPERATURA = "temperature";
	private static final String FIELD_NAME_GRAF="GRAF";


	private static final String[] FIELD_NAMES = new String[] { FIELD_NAME_TEMPERATURA };

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		threadCounter--;
	}

	@Override
	public DataField[] getOutputFormat() {
		// TODO Auto-generated method stub
		return outputStructureCache;
	}

	@Override
	public String getWrapperName() {
		// TODO Auto-generated method stub
		return "XMLReader";
	}

	@Override
	public boolean initialize() {
		setName("XMLReader-Thread" + (++threadCounter));
		rr = new RabbitRasip("http://161.53.67.199/report.xml");
		AddressBean addressBean = getActiveAddressBean();
		if (addressBean.getPredicateValue("sampling-rate") != null) {
			samplingRate = ParamParser.getInteger(addressBean.getPredicateValue("sampling-rate"),DEFAULT_SAMPLING_RATE);
			if (samplingRate <= 0 || samplingRate>36000000) {
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

			StreamElement streamElement = new StreamElement(FIELD_NAMES,
					new Byte[] {DataTypes.INTEGER }, new Serializable[] {rr.getTemperature()},
					System.currentTimeMillis());
			postStreamElement(streamElement);
		}
	}
}

