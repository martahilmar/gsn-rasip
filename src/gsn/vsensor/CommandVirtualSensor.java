package gsn.vsensor;

import gsn.beans.InputStream;
import gsn.beans.StreamSource;
import gsn.beans.VSensorConfig;
import gsn.command.CommandWrapper;
import gsn.wrappers.AbstractWrapper;
import org.apache.log4j.Logger;

import javax.naming.OperationNotSupportedException;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: FR33D00M
 * Date: 13.06.13.
 * Time: 11:52
 * To change this template use File | Settings | File Templates.
 */
public class CommandVirtualSensor extends BridgeVirtualSensor {
    private static final transient Logger logger = Logger.getLogger(BridgeVirtualSensor.class);

    public boolean dataFromWeb ( String action,String[] paramNames, Serializable[] paramValues ){
        VSensorConfig config = this.getVirtualSensorConfiguration();
        boolean sent = false;
        for (InputStream is : config.getInputStreams()){
            for(StreamSource ss : is.getSources()){
                AbstractWrapper wrapper = ss.getWrapper();

                if(wrapper instanceof CommandWrapper) {
                    try {
                        sent |= wrapper.sendToWrapper(action, paramNames, paramValues);
                    } catch (OperationNotSupportedException e) {
                        logger.error("Error while sending data to wrapper!");
                    }
                }
           }
            logger.warn(is.getInputStreamName());
        }

        return sent;
    }
}
