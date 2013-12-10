package gsn.wrappers.rasip.RelayModel.commands;

import org.liva.core.commands.Command;


public class ResultCommand extends Command {
	public static final int RESULT_OK = 0x00;
	public static final int COMMAND_RESULT_BAD_ARG = 0x01;
		
	private int result;

	
	/* (non-Javadoc)
	 * @see org.liva.accessControl.commands.Command#getCommandCode()
	 */
	@Override
	public byte getCommandCode() {
		return CommandRegistry.RESULT_COMMAND;
	}

	/* (non-Javadoc)
	 * @see org.liva.accessControl.commands.Command#getBinaryArguments()
	 */
	@Override
	public byte[] getBinaryArguments() {
		byte[] result = new byte[1];
		
		result[0] = (byte) this.result;
				
		return result;
	}

	@Override
	public void setArgumentsFromBinary(byte[] arguments) {
		if (arguments.length < 1) {
			// TODO error
			return;
		}
		this.result = arguments[0];
		
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	
	
	
	
}
