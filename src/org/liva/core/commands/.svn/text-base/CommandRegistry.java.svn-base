package org.liva.core.commands;


public abstract class CommandRegistry {
	
	public abstract Command getCommandByCode(byte code);

	public Command getCommandByBinary(byte[] data)
			throws CommandException {
				if (data.length < 5) {
					throw new CommandException("Data to short!");
				}
				if (data[0] != 0x02 || data[1] != 0x4c) {
					throw new CommandException("Command frame invalid!");
				}
				byte bcc = data[0];
				bcc ^= data[1];
				
				
				int uniqueId = ((data[2] & 0xff) << 8) | (data[3] & 0xff);
				bcc ^= data[2];
				bcc ^= data[3];
						
				Command c = getCommandByCode(data[4]);
				if (c == null) {
					return null;
				}
				bcc ^= data[4];
				
				c.setUniqueId(uniqueId);
				int length = data[5];
				bcc ^= data[5];
				
				
						
				byte[] arguments = new byte[data.length - 7];
				for (int i = 0; i < length; i++) {
					arguments[i] = data[i+6];
					bcc ^= data[i + 6];
				}
				bcc ^= data[length + 6];
				if (bcc != 0) {
					throw new CommandException("BCC check failed!");
				}
				
				c.setArgumentsFromBinary(arguments);
				return c;
			}

	public CommandRegistry() {
		super();
	}

}