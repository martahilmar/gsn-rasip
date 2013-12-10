package gsn.wrappers.rasip.PowerSupplyModel;

/**
 * Thrown if the command sent had an unknown socket as parameter.
*/
public class UnknownSocketException extends SmartPowerException
{
	private static final long serialVersionUID = 1L;

	public UnknownSocketException()
	{
		super();
	}

	public UnknownSocketException(String message)
	{
		super(message);
	}
}
