package gsn.wrappers.rasip;

import gsn.beans.AddressBean;
import gsn.beans.DataField;
import gsn.beans.DataTypes;
import gsn.beans.StreamElement;
import gsn.command.CommandWrapper;
import gsn.utils.Base64;
import gsn.utils.ParamParser;
import gsn.wrappers.AbstractWrapper;
import gsn.wrappers.rasip.PowerSupplyModel.*;
import org.apache.log4j.Logger;
import org.omg.CORBA.portable.ApplicationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class PowerBoardWrapper extends AbstractWrapper implements CommandWrapper {

    private final transient Logger logger = Logger.getLogger(PowerBoardWrapper.class);
    private static int threadCounter = 0;

    private transient DataField[] outputStructure = new DataField[] {new DataField("PowerBoardOn", "int", "Status letve - 2 - nije omogućena veza do letve. 1 - upaljena, 2 ugašena.")};


    private static final String WRAPPER_NAME = "PowerBoardWrapper";
    private static final int DEFAULT_SAMPLING_RATE = 1;
    private static final int DEFAULT_RATE = 1000;
    private static final String DEFAULT_SERVER_NAME = "192.168.1.93";
    private static final int DEFAULT_SOCKETS = 5;

    private int samplingRate = DEFAULT_SAMPLING_RATE;
    private int rate = DEFAULT_RATE;
    private static String ipAddress = DEFAULT_SERVER_NAME;
    private int sockets = DEFAULT_SOCKETS;
    private PowerBoard board;

    private static String TURN_ON = "ON";
    private static String TURN_OFF ="OFF";
    private static String RESET="RST";



    public boolean sendToWrapper(String action, String[] paramNames,
                                 Object[] paramValues){
        if(action.equals(TURN_ON)){

            if(paramValues.length != 1)
               return false;
            try {
                board.socketOn(parseNumber(paramValues[0].toString()));
            } catch (SmartPowerException e) {
                logger.error(e);
            }
        } else if (action.equals(TURN_OFF)){
            if(paramValues.length != 1)
                return false;
            try {
                board.socketOff(parseNumber(paramValues[0].toString()));
            } catch (SmartPowerException e) {
                logger.error(e);
                return false;
            }
        } else if(action.equals(RESET)){
            if(paramValues.length != 2)
                return false;
            try {
                board.socketReset(parseNumber(paramValues[0].toString()), (parseNumber(paramValues[1].toString())));
            } catch (SmartPowerException e) {
                logger.error(e);
                return false;
            }
        }
        return true;
    }

    public void run() {
        while (isActive()) {

            Integer on = new Integer(2);
            try {
                int onUnboxxed = board.powerStatus()?1:0;
                on = new Integer(onUnboxxed);
            } catch (UnknownCommandException e) {
                logger.error(e);
            } catch (UnknownSocketException e) {
                logger.error(e);
            } catch (ResponseTimeoutException e) {
                logger.error(e);
            } catch (SmartPowerException e) {
                logger.error(e);
            }
            try {
                StreamElement streamElement = new StreamElement(
                        new String[]{ "PowerBoardOn"},
                        new Byte[]{DataTypes.INTEGER},
                        new Serializable[]{on},
                        System.currentTimeMillis());
                postStreamElement(streamElement);

                Thread.sleep(rate);
            } catch (InterruptedException e) {
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

            if(addressBean.getPredicateValue("Sockets") != null){
                sockets = ParamParser.getInteger(addressBean.getPredicateValue("Sockets"), DEFAULT_SOCKETS);
            }

            if(addressBean.getPredicateValue("IPAddress") != null){
                ipAddress = addressBean.getPredicateValue("IPAddress");
            }
            board = new PowerBoard("1", ipAddress, sockets);

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
