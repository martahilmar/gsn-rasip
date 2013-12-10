package org.liva.core.commands;

public class CommandException extends Exception {
	private static final long serialVersionUID = -4488596571938518415L;

	protected CommandException() {
		super();
	}

	protected CommandException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	protected CommandException(String arg0) {
		super(arg0);
	}

	protected CommandException(Throwable arg0) {
		super(arg0);
	}

	
}
