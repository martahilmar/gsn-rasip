package gsn.wrappers.rasip;

import gsn.beans.AddressBean;
import gsn.beans.DataField;
import gsn.beans.DataTypes;
import gsn.beans.StreamElement;
import gsn.command.CommandWrapper;
import gsn.utils.ParamParser;
import gsn.wrappers.AbstractWrapper;
import gsn.wrappers.rasip.PowerSupplyModel.*;
import gsn.wrappers.rasip.RelayModel.Control;
import org.apache.log4j.Logger;
import gsn.wrappers.rasip.RelayModel.commands.CommandServer;
import org.liva.core.commands.UDPCommandEndpoint;

import java.io.Serializable;
import java.net.InetAddress;

public class RelayWrapper extends AbstractWrapper implements CommandWrapper {

    private final transient Logger logger = Logger.getLogger(PowerBoardWrapper.class);
    private static int threadCounter = 0;
    private static final int DEFAULT_NUMBER_OF_RELAYS = 16;
    private transient DataField[] outputStructure;
    private static final String WRAPPER_NAME = "RelayWrapper";
    private static final int DEFAULT_SAMPLING_RATE = 1;
    private static final int DEFAULT_RATE = 1000;
    private static final String DEFAULT_SERVER_NAME = "192.168.1.92";
    private static final int DEFAULT_PORT = 26352;
    private static final int DEFAULT_LOCAL_PORT = 27654;
    private int samplingRate = DEFAULT_SAMPLING_RATE;
    private int rate = DEFAULT_RATE;
    private static String ipAddress = DEFAULT_SERVER_NAME;
    private int relays = DEFAULT_NUMBER_OF_RELAYS;
    private Control relayControl;
    private static int port = DEFAULT_PORT;
    private static int localPort = DEFAULT_LOCAL_PORT;
    private static String TURN_ON = "ON";
    private static String TURN_OFF ="OFF";
    private String[] nazivi;
    private Byte[] types;


    public boolean sendToWrapper(String action, String[] paramNames,
                                 Object[] paramValues)
    {
        if(action.equals(TURN_ON)){

            if(paramValues.length != 1)
                return false;
            try {
                relayControl.setOutputState(parseNumber(paramValues[0].toString()), true);
            } catch (SmartPowerException e) {
                logger.error(e);
            } catch (Exception e) {
                logger.error(e);
            }
        } else if (action.equals(TURN_OFF)){
            if(paramValues.length != 1)
                return false;
            try {
                relayControl.setOutputState(parseNumber(paramValues[0].toString()), false);
            } catch (SmartPowerException e) {
                logger.error(e);
                return false;
            } catch (Exception e) {
                logger.error(e);
            }
        }
            return true;
    }


    public void run() {
        while (isActive()) {
            try {
                boolean[] statusi = relayControl.getOutputStatus();
                int length = statusi.length;
                
				Serializable[] podaci = new Serializable[relays];
                for (int i = 0; i <length; i++){
                    podaci[i] = new Integer(statusi[i]?1:0);
                }
                StreamElement streamElement = new StreamElement(
                        nazivi,
                        types,
                        podaci,
                        System.currentTimeMillis());
                postStreamElement(streamElement);

                Thread.sleep(rate);
            } catch (InterruptedException e) {
                logger.error(e);
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }


    public DataField[] getOutputFormat() {
        return outputStructure;
    }


    public boolean initialize() {
        try {
            setName(WRAPPER_NAME + (++threadCounter));

            AddressBean addressBean = getActiveAddressBean();
            if (addressBean.getPredicateValue("sampling-rate") != null) {
                samplingRate = ParamParser.getInteger(addressBean.getPredicateValue("sampling-rate"), DEFAULT_SAMPLING_RATE);
                if (samplingRate <= 0) {
                    samplingRate = DEFAULT_SAMPLING_RATE;
                }
            }

            if(addressBean.getPredicateValue("Relays") != null){
                relays = ParamParser.getInteger(addressBean.getPredicateValue("Sockets"), DEFAULT_NUMBER_OF_RELAYS);
            }
            if(addressBean.getPredicateValue("port") != null){
                port = ParamParser.getInteger(addressBean.getPredicateValue("port"), DEFAULT_PORT);
            }

            if(addressBean.getPredicateValue("IPAddress") != null){
                ipAddress = addressBean.getPredicateValue("IPAddress");
            }
			InetAddress local = InetAddress.getLocalHost();
			InetAddress address = InetAddress.getByName(ipAddress);
			
            relayControl = new Control(local, localPort, address, port);
            nazivi = new String[relays];
            types = new Byte[relays];
           outputStructure = new DataField[relays];
            for(int i = 0; i < relays; i++){
                String naziv = "Relay_" + i;
                nazivi[i] = naziv;
                types[i] = DataTypes.INTEGER;
                outputStructure[i] = new DataField(naziv, "int", "Status relaya " + i);
            }

            return true;
        } catch (Exception ex) {
            logger.error(ex);
        }
        return false;
    }

    public void dispose() {
        threadCounter--;
    }

    public String getWrapperName() {
        return WRAPPER_NAME;
    }

    private int parseNumber(String number){
        if(number == null || number.equals("") )
            return 0;
        else{
            int x = Integer.parseInt(number);

            return x;
        }

    }
}
