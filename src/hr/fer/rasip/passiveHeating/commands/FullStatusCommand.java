package hr.fer.rasip.passiveHeating.commands;

import org.liva.core.commands.Command;


public class FullStatusCommand extends Command {
	
	private boolean heaterOn;
	private boolean airflowPresent;
	private boolean fanOn;

	
	/* (non-Javadoc)
	 * @see org.liva.accessControl.commands.Command#getCommandCode()
	 */
	@Override
	public byte getCommandCode() {
		return CommandRegistry.FULL_STATUS_COMMAND;
	}

	/* (non-Javadoc)
	 * @see org.liva.accessControl.commands.Command#getBinaryArguments()
	 */
	@Override
	public byte[] getBinaryArguments() {
		byte[] result = new byte[3];
		
		result[0] = (byte) ((this.heaterOn)? 0x01 : 0x00);
		result[1] = (byte) ((this.fanOn)? 0x01 : 0x00);
		result[2] = (byte) ((this.airflowPresent)? 0x01 : 0x00);
				
		return result;
	}

	@Override
	public void setArgumentsFromBinary(byte[] arguments) {
		if (arguments.length < 3) {
			// TODO error
			return;
		}
		this.heaterOn = (arguments[0] != 0);
		this.fanOn = (arguments[1] != 0);
		this.airflowPresent = (arguments[2] != 0);
	}

	public boolean isHeaterOn() {
		return heaterOn;
	}

	public void setHeaterOn(boolean heaterOn) {
		this.heaterOn = heaterOn;
	}

	public boolean isAirflowPresent() {
		return airflowPresent;
	}

	public void setAirflowPresent(boolean airflowPresent) {
		this.airflowPresent = airflowPresent;
	}

	public boolean isFanOn() {
		return fanOn;
	}

	public void setFanOn(boolean fanOn) {
		this.fanOn = fanOn;
	}
	
	
}
