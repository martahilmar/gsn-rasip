package gsn.wrappers.rasip.RelayModel.commands;

import org.liva.core.commands.Command;


public class OutputStatusCommand extends Command {
		
	private boolean[] status;

	
	/* (non-Javadoc)
	 * @see org.liva.accessControl.commands.Command#getCommandCode()
	 */
	@Override
	public byte getCommandCode() {
		return CommandRegistry.OUTPUT_STATUS_COMMAND;
	}

	/* (non-Javadoc)
	 * @see org.liva.accessControl.commands.Command#getBinaryArguments()
	 */
	@Override
	public byte[] getBinaryArguments() {
		byte[] result = new byte[status.length + 1];
		
		result[0] = (byte) (status.length & 0xFF);
		
		for (int i = 0; i < status.length; i++) {
			result[i + 1] = (byte) ((status[i]) ? 1 : 0);
		}
				
		return result;
	}

	@Override
	public void setArgumentsFromBinary(byte[] arguments) {
		int size;
		if (arguments.length < 1) {
			// TODO error
			return;
		}
		size = arguments[0];
		status = new boolean[size];
		for(int i = 0; i < size; i++) {
			status[i] = arguments[i + 1] == 1;
		}
	}

	public boolean[] getStatus() {
		return status;
	}
}
