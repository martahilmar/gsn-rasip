package gsn.wrappers.rasip.PowerSupplyModel;

/**
 * Thrown if unknown board ID is used.
 */
public class UnknownBoardException extends SmartPowerException
{
	private static final long serialVersionUID = 1L;

	public UnknownBoardException()
	{
		super();
	}

	public UnknownBoardException(String message)
	{
		super(message);
	}
}
