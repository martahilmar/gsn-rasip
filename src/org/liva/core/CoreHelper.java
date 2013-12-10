package org.liva.core;

import java.util.Calendar;
import java.util.Date;

public class CoreHelper {
	
	/**
	 * Sleeps for a number of milliseconds, witouth need for catching exceptions.
	 * @param milliseconds Number of milliseconds to sleep.
	 * @return True if not interupted, false if interupted.
	 */
	public static boolean sleep(long milliseconds) {
		try { 
			Thread.sleep(milliseconds);
		}
    	catch (InterruptedException e) {
    		return false;
    	}
    	return true;
	}
	
	/**
	 * Calculates difference in milliseconds taking into account only time of day.
	 * @param time1 First Date containing time of day. 
	 * @param time2 Second Date containing time of day.
	 * @return Difference in milliseconds taking into account time of day, time1 - time2.
	 */
	public static long compareTimesOfDay(Date time1, Date time2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(time1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(time2);
		
		long difference = 0;
		
		difference = (cal1.get(Calendar.HOUR_OF_DAY) - cal2.get(Calendar.HOUR_OF_DAY)) * 60 * 60 * 1000;
		difference += (cal1.get(Calendar.MINUTE) - cal2.get(Calendar.MINUTE)) * 60 * 1000;
		difference += (cal1.get(Calendar.SECOND) - cal2.get(Calendar.SECOND)) * 1000;
		difference += cal1.get(Calendar.MILLISECOND) - cal2.get(Calendar.MILLISECOND);
		
		
		return difference;
	}
	
	public static boolean verifyIntegerString(String string) {
		char[] chars = string.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (!Character.isDigit(chars[i])) {
				return false;
			}
		}
		return true;
	}
}
