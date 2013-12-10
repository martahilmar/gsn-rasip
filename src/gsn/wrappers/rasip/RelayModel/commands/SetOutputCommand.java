package gsn.wrappers.rasip.RelayModel.commands;

import org.liva.core.commands.Command;

public class SetOutputCommand extends Command {
	private int output;
	private boolean on;
	
	public SetOutputCommand(int output, boolean on) {
		super();
		this.output = output;
		this.on = on;
	}

	public SetOutputCommand() {
	}

	@Override
	public byte getCommandCode() {
		return CommandRegistry.SET_OUTPUT_COMMAND;
	}

	@Override
	public byte[] getBinaryArguments() {
		byte[] result = new byte[2];
		result[0] = (byte) (output & 0xFF);
		result[1] = (byte) ((on) ? 1 : 0);
		return result;
	}

	@Override
	public void setArgumentsFromBinary(byte[] arguments) {
		if (arguments.length < 2) {
			// TODO error!
			return;
		}
		this.output = arguments[0];
		this.on = arguments[1] != 0;
	}

	public int getOutput() {
		return output;
	}

	public void setOutput(int output) {
		this.output = output;
	}

	public boolean isOn() {
		return on;
	}

	public void setOn(boolean on) {
		this.on = on;
	}
	
	

}
