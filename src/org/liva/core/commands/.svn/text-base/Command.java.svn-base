package org.liva.core.commands;

public abstract class Command {
	protected int uniqueId;
	protected CommandEndpoint sender = null; 
	
	
	protected Command() {
		super();
		assignRandomUniqueId();
	}
	
	
	public abstract byte getCommandCode();
	
	public abstract byte[] getBinaryArguments();
	
	public abstract void setArgumentsFromBinary(byte[] arguments);
	
	protected void assignRandomUniqueId() {
		uniqueId = (int) (Math.random() * 65536);
		// just in case...
		uniqueId &= 0xffff;
	}


	public byte[] getBinary() {
		byte code = getCommandCode();
		byte[] arguments = getBinaryArguments();
		if (arguments == null) {
			return null;
		}
		
		byte[] result = new byte[arguments.length + 7];
		
		result[0] = 0x02;
		result[1] = 0x4c;
		
		result[2] = (byte) ((uniqueId & 0xff00) >> 8) ;
		result[3] = (byte) (uniqueId & 0xff);
		
		result[4] = code;
		
		result[5] = (byte) arguments.length;
		for (int i = 0; i < arguments.length; i++) {
			result[6+i] = arguments[i];
		}
		
		byte bcc = 0x00;
		for (int i = 0; i < result.length-1; i++) {
			bcc ^= result[i];
		}
		result[result.length - 1] = bcc;
		
		return result;
	}

	/**
	 * @return the uniqueId
	 */
	public int getUniqueId() {
		return uniqueId;
	}
	
	public void setUniqueId(int uniqueId) {
		this.uniqueId = uniqueId;
	}

	/* (non-Javadoc)
	 * @see org.liva.core.commands.Command#getSender()
	 */
	public CommandEndpoint getSender() {
		return sender;
	}
	
	void setSender(CommandEndpoint sender) {
		this.sender = sender;
	}
	
	
}
