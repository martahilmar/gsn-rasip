package gsn.electricity;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

public class RequestMeter {
	private byte[] request;
	private byte[] answer;
	private boolean dataSet;

	RequestMeter() {
		dataSet = false;
	}

	/**
	 * @return the request
	 */
	public byte[] getRequest() {
		return request;
	}

	/**
	 * @param request
	 *            the request to set
	 */
	public void setRequest(byte[] request) {
		this.request = request;
	}

	/**
	 * @return the answer
	 */
	public byte[] getAnswer() {
		return answer;
	}

	/**
	 * @param answer
	 *            the answer to set
	 */
	public void setAnswer(byte[] answer) {
		this.answer = answer;
	}

	/**
	 * @return the dataSet
	 */
	public boolean isDataSet() {
		return dataSet;
	}

	/**
	 * @param dataSet
	 *            the dataSet to set
	 */
	public void setDataSet(boolean dataSet) {
		this.dataSet = dataSet;
	}

}
