package hr.fer.rasip.passiveHeating.commands;

import org.liva.core.commands.Command;

public class SetAirIntakeCommand extends Command {
	private Intake targetAirIntake;
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.liva.accessControl.commands.Command#getCommandCode()
	 */
	@Override
	public byte getCommandCode() {
		return CommandRegistry.SET_AIR_INTAKE_COMMAND;
	}

	public SetAirIntakeCommand(Intake targetAirIntake) {
		super();
		this.targetAirIntake = targetAirIntake;
	}

	public SetAirIntakeCommand() {
		this.targetAirIntake = Intake.NORMAL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.liva.accessControl.commands.Command#getBinaryArguments()
	 */
	@Override
	public byte[] getBinaryArguments() {
		byte[] result = new byte[1];

		result[0] = this.targetAirIntake.getValue();

		return result;
	}

	@Override
	public void setArgumentsFromBinary(byte[] arguments) {
		if (arguments.length < 1) {
			// TODO error
			return;
		}
		this.targetAirIntake = Intake.getByValue(arguments[0]);

	}

	public Intake getTargetAirIntake() {
		return targetAirIntake;
	}

	public void setTargetAirIntake(Intake targetAirIntake) {
		this.targetAirIntake = targetAirIntake;
	}



	public enum Intake {
		NORMAL((byte) 0x00),
		OVERRIDE((byte) 0x01);

		private byte value;

		private Intake(byte value) {
			this.value = value;
		}

		public byte getValue() {
			return value;
		}
		
		public static Intake getByValue(byte value) {
			switch (value) {
			case 0x01:
				return Intake.OVERRIDE;
			case 0x00:
			default:
				return Intake.NORMAL;
			}
		}
	}

}
