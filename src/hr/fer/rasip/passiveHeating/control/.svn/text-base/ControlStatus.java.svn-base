package hr.fer.rasip.passiveHeating.control;

import hr.fer.rasip.passiveHeating.commands.FullStatusCommand;

/**
 * Contains information about passive heating controller.
 * 
 * @author Luka Lednicki
 *
 */
public class ControlStatus {
	private boolean heaterOn;
	private boolean airflowPresent;
	private boolean fanOn;
	
	/**
	 * Creates new <i>ControlStatus</i> with all status variables initialized to false.
	 */
	public ControlStatus() {
		heaterOn = false;
		airflowPresent = false;
		fanOn = false;
	}
	
	/**
	 * Creates new <i>ControlStatus</i> and initializes status variables using values from a <i>FullStatusCommand</i>.
	 */
	public ControlStatus(FullStatusCommand command) {
		this.heaterOn = command.isHeaterOn();
		this.airflowPresent = command.isAirflowPresent();
		this.fanOn = command.isFanOn();
	}
	
	/**
	 * Is the heater on?
	 * @return <i>True</i> if on, <i>false</i> if not.
	 */
	public boolean isHeaterOn() {
		return heaterOn;
	}
	
	/**
	 * Set the status of the heater.
	 * @param heaterOn <i>True</i> if on, <i>false</i> if not.
	 */
	public void setHeaterOn(boolean heaterOn) {
		this.heaterOn = heaterOn;
	}
	
	/**
	 * Is aiflow present on?
	 * @return <i>True</i> if yes, <i>false</i> if not.
	 */
	public boolean isAirflowPresent() {
		return airflowPresent;
	}
	
	/**
	 * Set the airflow status.
	 * @param airflowPresent <i>True</i> if present, <i>false</i> if not.
	 */
	public void setAirflowPresent(boolean airflowPresent) {
		this.airflowPresent = airflowPresent;
	}
	
	/**
	 * Is the fan on?
	 * @return <i>True</i> if on, <i>false</i> if not.
	 */
	public boolean isFanOn() {
		return fanOn;
	}
	
	/**
	 * Set the status of the fan.
	 * @param fanOn <i>True</i> if on, <i>false</i> if not.
	 */
	public void setFanOn(boolean fanOn) {
		this.fanOn = fanOn;
	}
	
	
}
