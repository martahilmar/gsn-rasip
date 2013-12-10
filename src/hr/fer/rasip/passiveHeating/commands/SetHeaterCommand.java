package hr.fer.rasip.passiveHeating.commands;

import org.liva.core.commands.Command;


public class SetHeaterCommand extends Command {
	private boolean targetState;

	
	/* (non-Javadoc)
	 * @see org.liva.accessControl.commands.Command#getCommandCode()
	 */
	@Override
	public byte getCommandCode() {
		return CommandRegistry.SET_HEATER_COMMAND;
	}
	
	public SetHeaterCommand(boolean targetState) {
		super();
		this.targetState = targetState;
	}



	public SetHeaterCommand() {
		this.targetState = false;
	}

	/* (non-Javadoc)
	 * @see org.liva.accessControl.commands.Command#getBinaryArguments()
	 */
	@Override
	public byte[] getBinaryArguments() {
		byte[] result = new byte[1];
		
		result[0] = (byte) ((this.targetState) ? 1 : 0);
				
		return result;
	}

	@Override
	public void setArgumentsFromBinary(byte[] arguments) {
		if (arguments.length < 1) {
			// TODO error
			return;
		}
		this.targetState = (arguments[0] == 0) ? false : true;
		
	}

	public boolean isTargetState() {
		return targetState;
	}

	public void setTargetState(boolean targetState) {
		this.targetState = targetState;
	}

	
	
	
	
}
