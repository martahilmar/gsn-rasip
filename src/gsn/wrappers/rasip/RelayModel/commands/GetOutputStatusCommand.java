package gsn.wrappers.rasip.RelayModel.commands;

import org.liva.core.commands.Command;

public class GetOutputStatusCommand extends Command {

	public GetOutputStatusCommand() {
	}

	@Override
	public byte getCommandCode() {
		return CommandRegistry.GET_OUTPUT_STATUS_COMMAND;
	}

	@Override
	public byte[] getBinaryArguments() {
		byte[] result = new byte[0];
		return result;
	}

	@Override
	public void setArgumentsFromBinary(byte[] arguments) {
	}

	
	

}
