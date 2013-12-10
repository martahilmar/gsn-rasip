package hr.fer.rasip.passiveHeating.commands;

import org.liva.core.commands.Command;


public class SetFanCommand extends Command {
	private int targetPower;

	public SetFanCommand(int targetPower) {
		this.targetPower = targetPower;
	}

	public SetFanCommand() {
		this.targetPower = 0;
	}

	/* (non-Javadoc)
	 * @see org.liva.accessControl.commands.Command#getCommandCode()
	 */
	@Override
	public byte getCommandCode() {
		return CommandRegistry.SET_FAN_COMMAND;
	}

	/* (non-Javadoc)
	 * @see org.liva.accessControl.commands.Command#getBinaryArguments()
	 */
	@Override
	public byte[] getBinaryArguments() {
		byte[] result = new byte[1];
		
		result[0] = (byte) this.targetPower;
				
		return result;
	}

	@Override
	public void setArgumentsFromBinary(byte[] arguments) {
		if (arguments.length < 1) {
			// TODO error
			return;
		}
		this.targetPower = arguments[0];
		
	}

	public int getTargetPower() {
		return targetPower;
	}

	public void setTargetPower(int targetPower) {
		this.targetPower = targetPower;
	}
	
	
	
}
