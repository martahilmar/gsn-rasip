package gsn.wrappers.rasip;

import gsn.beans.AddressBean;
import gsn.beans.DataField;
import gsn.beans.DataTypes;
import gsn.beans.StreamElement;
import gsn.command.CommandWrapper;
import gsn.utils.Base64;
import gsn.utils.ParamParser;
import gsn.wrappers.AbstractWrapper;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class IPCamWrapper extends AbstractWrapper implements CommandWrapper {
	
	private final transient Logger logger = Logger.getLogger(IPCamWrapper.class);
	private static int threadCounter = 0;
    
    private transient DataField[] outputStructure = new DataField[]{new DataField("picture", "binary", "USB camera picture.")};
    
	private static final String WRAPPER_NAME = "IPCamWrapper";

    private static final int DEFAULT_SAMPLING_RATE = 1;
    private static final int DEFAULT_WIDTH = 640;
    private static final int DEFAULT_HEIGHT = 480;
    private static final int DEFAULT_DEVICE_ID = 0;
    private static final int DEFAULT_RATE = 1000;
    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_USER = "ivica";
    private static final String DEFAULT_PASS = "hiperion";
    private static final String DEFAULT_SERVER_NAME = "161.53.67.95";
    private static final int DEFAULT_PROFILE = 1;
    
    private int samplingRate = DEFAULT_SAMPLING_RATE;
    private int rate = DEFAULT_RATE;
    private int width = DEFAULT_WIDTH;
    private int height = DEFAULT_HEIGHT;
    private int deviceId = DEFAULT_DEVICE_ID;
    private static String schema = "http://";
    private static String serverName= DEFAULT_SERVER_NAME;
    private static int port = DEFAULT_PORT;
    private static String user = DEFAULT_USER;
    private static String pass = DEFAULT_PASS;
    private static int profile = DEFAULT_PROFILE;

    private static String MOVE = "MOVE";
    private static String ABSOLUTE_MOVE ="ABS_MOVE";
    private static String RESET="RST";
    

    public boolean sendToWrapper(String action, String[] paramNames,
                              Object[] paramValues){
        if(action.equals(MOVE)){
            if(paramValues.length < 1)
                return false;
            logger.warn("U wrapperu");
            int X = 0;
            int Y = 0;
            if(paramValues.length >= 2){
                X = parseNumber(paramValues[0].toString());
                Y = parseNumber(paramValues[1].toString());
            } else if (paramValues.length == 1) {
                X = parseNumber(paramValues[0].toString());
            }
            return Move(X, Y);
        } else if (action.equals(ABSOLUTE_MOVE)){

            if(paramValues.length < 1)
                return false;

            int X = 0;
            int Y = 0;
            if(paramValues.length >= 2){
                X = parseNumber(paramValues[0].toString());
                Y = parseNumber(paramValues[1].toString());
            } else if (paramValues.length == 1) {
                X = parseNumber(paramValues[0].toString());
            }
          return MoveAbs(X, Y);
        } else if(action.equals(RESET)){
              return MoveAbs(0,0);
        }
            return true;
    }
     private boolean MoveAbs(int X, int Y)
     {
         try {
             String urlString =  schema + serverName + ":" + port + "/cgi/ptdc.cgi?command=set_pos&posX="+ X + "&posY=" + Y;
             logger.warn("Url je: " + urlString);
             URL url = new URL(urlString);
             String authStr = user + ":" + pass;
             String authEncoded = Base64.encodeToString(authStr.getBytes(), false);

             HttpURLConnection connection = (HttpURLConnection) url.openConnection();
             connection.setRequestProperty("Authorization", "Basic " + authEncoded);
             connection.setRequestMethod("GET");
             connection.getResponseMessage();
         } catch (MalformedURLException e) {
             return false;
         } catch (ProtocolException e) {
             return false;
         } catch (IOException e) {
             return false;
         }
         return true;
     }
     private boolean Move(int X, int Y) {
        try {
                String urlString =  schema + serverName + ":" + port + "/cgi/ptdc.cgi?command=set_relative_pos&posX="+ X + "&posY=" + Y;
                logger.warn("Url je: " + urlString);
                URL url = new URL(urlString);
                String authStr = user + ":" + pass;
                String authEncoded = Base64.encodeToString(authStr.getBytes(), false);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", "Basic " + authEncoded);
                connection.setRequestMethod("GET");
                connection.getResponseMessage();
        } catch (MalformedURLException e) {
            return false;
        } catch (ProtocolException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }


    public void run() {
        while (isActive()) {

            ByteArrayOutputStream baos = null;
            try {
                URL url = new URL(schema + serverName + ":" + port +"/video/mjpg.cgi?profileid=" + profile);

                String authStr = user + ":" + pass;
                String authEncoded = Base64.encodeToString(authStr.getBytes(), false);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Basic " + authEncoded);

                baos = new ByteArrayOutputStream();

                InputStream webStream = (InputStream) connection.getContent();
                IPKameraStreamWrapper ipStream = null;
                try{
                    ipStream = new IPKameraStreamWrapper(webStream);
                    ipStream.writeNextImage(baos);

                } catch (Exception e) {
                    logger.error(e);
                } finally {
                    try {
                        if(ipStream != null)
                            ipStream.close();
                    } catch (Exception e) {
                        logger.warn("Could not close IP Cam stream: " + e.getStackTrace());
                    }
                }

                byte[] imageInByte = baos.toByteArray();
                baos.close();

                StreamElement streamElement = new StreamElement(new String[]{ "picture"},
                        new Byte[]{DataTypes.BINARY},
                        new Serializable[]{imageInByte},
                        System.currentTimeMillis());
                postStreamElement(streamElement);
                Thread.sleep(rate);

            } catch (MalformedURLException e1) {
                logger.error(e1);
            } catch (IOException e) {
                logger.error(e);
            } catch (InterruptedException e) {
                logger.error(e);
            } finally {
                if (baos != null) {
                    try {
                        baos.close();
                    } catch (IOException e) {
                       logger.error(e);
                    }
                }
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
            
            if(addressBean.getPredicateValue("image-width") != null){
                width = ParamParser.getInteger(addressBean.getPredicateValue("image-width"), DEFAULT_WIDTH);
                if (width <= 0) {
                    width = DEFAULT_WIDTH;
                }
            }
            
            if(addressBean.getPredicateValue("image-height") != null){
                height = ParamParser.getInteger(addressBean.getPredicateValue("image-height"), DEFAULT_HEIGHT);
                if (height <= 0) {
                    height = DEFAULT_HEIGHT;
                }
            }
            
            if(addressBean.getPredicateValue("device-id") != null){
                deviceId = ParamParser.getInteger(addressBean.getPredicateValue("device-id"), DEFAULT_DEVICE_ID);
                if (deviceId <= 0) {
                    deviceId = DEFAULT_DEVICE_ID;
                }
            }

            if(addressBean.getPredicateValue("port") != null){
                port = ParamParser.getInteger(addressBean.getPredicateValue("port"), DEFAULT_PORT);
                if (port <= 0) {
                    port = DEFAULT_PORT;
                }
            }

            if(addressBean.getPredicateValue("profile") != null){
                profile = ParamParser.getInteger(addressBean.getPredicateValue("profile"), DEFAULT_PROFILE);
                if (profile < 1 || profile > 3) {
                    profile = DEFAULT_PROFILE;
                }
            }

            if(addressBean.getPredicateValue("server-name") != null){
                serverName = addressBean.getPredicateValue("server-name");
            }

            if(addressBean.getPredicateValue("user") != null){
                user = addressBean.getPredicateValue("user");
            }

            if(addressBean.getPredicateValue("pass") != null){
                pass = addressBean.getPredicateValue("pass");
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
