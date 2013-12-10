package hr.fer.rasip.passiveHeating.commands;

import org.liva.core.commands.Command;


public class FanStatusCommand extends Command {
	
	private boolean on;

	
	/* (non-Javadoc)
	 * @see org.liva.accessControl.commands.Command#getCommandCode()
	 */
	@Override
	public byte getCommandCode() {
		return CommandRegistry.FAN_STATUS_COMMAND;
	}

	/* (non-Javadoc)
	 * @see org.liva.accessControl.commands.Command#getBinaryArguments()
	 */
	@Override
	public byte[] getBinaryArguments() {
		byte[] result = new byte[1];
		
		result[0] = (byte) ((this.on)? 0x01 : 0x00);
				
		return result;
	}

	@Override
	public void setArgumentsFromBinary(byte[] arguments) {
		if (arguments.length < 1) {
			// TODO error
			return;
		}
		this.on = (arguments[0] != 0);
	}

	public boolean isOn() {
		return on;
	}

	public void setOn(boolean on) {
		this.on = on;
	}
}
