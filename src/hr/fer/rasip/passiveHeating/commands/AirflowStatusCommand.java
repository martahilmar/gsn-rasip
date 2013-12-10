package hr.fer.rasip.passiveHeating.commands;

import org.liva.core.commands.Command;


public class AirflowStatusCommand extends Command {
	
	private boolean airflowPresent;

	
	/* (non-Javadoc)
	 * @see org.liva.accessControl.commands.Command#getCommandCode()
	 */
	@Override
	public byte getCommandCode() {
		return CommandRegistry.AIRFLOW_STATUS_COMMAND;
	}

	/* (non-Javadoc)
	 * @see org.liva.accessControl.commands.Command#getBinaryArguments()
	 */
	@Override
	public byte[] getBinaryArguments() {
		byte[] result = new byte[1];
		
		result[0] = (byte) ((this.airflowPresent)? 0x01 : 0x00);
				
		return result;
	}

	@Override
	public void setArgumentsFromBinary(byte[] arguments) {
		if (arguments.length < 1) {
			// TODO error
			return;
		}
		this.airflowPresent = (arguments[0] != 0);
	}

	public boolean isAirflowPresent() {
		return airflowPresent;
	}

	public void setAirflowPresent(boolean on) {
		this.airflowPresent = on;
	}
}
