package gsn.wrappers.rasip.RelayModel.commands;


import org.liva.core.commands.Command;

public class CommandRegistry extends org.liva.core.commands.CommandRegistry {
	
public static CommandRegistry instance = null;

	public static final byte SET_OUTPUT_COMMAND = 0x00;
	
	public static final byte GET_OUTPUT_STATUS_COMMAND = 0x01;
	public static final byte OUTPUT_STATUS_COMMAND = 0x02;

	public static final byte RESULT_COMMAND = (byte) (0xFF & 0xFF);

	
	
	public static CommandRegistry getInstance() {
		if (instance == null) {
			instance = new CommandRegistry();
		}
		return instance;
	}

	@Override
	public Command getCommandByCode(byte code) {
		if (code == SET_OUTPUT_COMMAND) {
			return new SetOutputCommand();
		} else if (code == GET_OUTPUT_STATUS_COMMAND) {
			return new GetOutputStatusCommand();
		} else if (code == OUTPUT_STATUS_COMMAND) {
			return new OutputStatusCommand();
		} else if (code == RESULT_COMMAND) {
			return new ResultCommand();
		}
		return null;
	}

}
