package gsn.wrappers.rasip.PowerSupplyModel;

/**
 * Thrown if there was timeout in waiting for the board to respond.
 * 
 */
public class ResponseTimeoutException extends SmartPowerException
{
	private static final long serialVersionUID = 1L;

	ResponseTimeoutException()
	{
		super();
	}
	
	ResponseTimeoutException(String message)
	{
		super(message);
	}

}
