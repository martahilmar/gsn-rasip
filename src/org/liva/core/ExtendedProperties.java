package org.liva.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class ExtendedProperties {
	public static final String DEFAULT_STRING_PROP_DELIMITER = ";";
	public static final String DEFAULT_STRING_PROP_EQ = "=";

	private Properties properties = null;
	private String originalFileName = null;
	private ExtendedProperties parentProperties = null;

	public ExtendedProperties() {
		properties = new Properties();
	}
	
	public ExtendedProperties(ExtendedProperties parentProperties) {
		this.parentProperties = parentProperties;
		properties = new Properties();
	}

	public void clear() {
		properties.clear();
	}

	public void loadFromXML(String fileName)
			throws InvalidPropertiesFormatException, FileNotFoundException,
			IOException {
		originalFileName = fileName;
		loadFromXML(new BufferedInputStream(new FileInputStream(fileName)));
	}

	public void loadFromXML(InputStream is)
			throws InvalidPropertiesFormatException, IOException {
		properties.loadFromXML(is);
	}
	
	public void storeToXML(String comment) throws IOException {
		if (originalFileName == null) {
			throw new IOException("Original file name not known!");
		}
		storeToXML(new BufferedOutputStream(new FileOutputStream(originalFileName)),
				comment);
	}

	public void storeToXML(String fileName, String comment) throws IOException {
		storeToXML(new BufferedOutputStream(new FileOutputStream(fileName)),
				comment);
	}

	public void storeToXML(OutputStream os, String comment) throws IOException {
		properties.storeToXML(os, comment);
	}

	public void loadFromString(String s) {
		loadFromString(s, true);
	}

	public void loadFromString(String s, boolean trim) {
		loadFromString(s, DEFAULT_STRING_PROP_DELIMITER,
				DEFAULT_STRING_PROP_EQ, trim);
	}

	public void loadFromString(String s, String delimiter, String eq,
			boolean trim) {
		String[] props = s.split(delimiter);

		for (String prop : props) {
			String[] keyValue = prop.split(eq);
			if (keyValue.length == 2) {
				String key = keyValue[0];
				String value = keyValue[1];
				if (trim) {
					key = key.trim();
					value = value.trim();
				}
				properties.setProperty(key, value);
			}
		}
	}

	public boolean containsKey(Object arg0) {
		return properties.containsKey(arg0);
	}

	public boolean containsValue(Object arg0) {
		return properties.containsValue(arg0);
	}

	public Enumeration<Object> elements() {
		return properties.elements();
	}

	public String getProperty(String key, String defaultValue) {
		String result = properties.getProperty(key);
		if (result == null && parentProperties != null) {
			result = parentProperties.getProperty(key);
		}
		if (result == null) {
			result = defaultValue;
		}
		return (result != null) ? result : defaultValue;
	}

	public String getProperty(String key) {
		return getProperty(key, null);
	}

	public Enumeration<?> propertyNames() {
		return properties.propertyNames();
	}

	public Object remove(Object arg0) {
		return properties.remove(arg0);
	}

	public int size() {
		return properties.size();
	}

	public Collection<Object> values() {
		return properties.values();
	}

	public String getStringProperty(String key) {
		return getStringProperty(key, null);
	}

	public String getStringProperty(String key, String defaultValue) {
		return getProperty(key, defaultValue);
	}

	public Boolean getBooleanProperty(String key) {
		return getBooleanProperty(key, null);
	}

	public Boolean getBooleanProperty(String key, Boolean defaultValue) {
		String value = getProperty(key);
		return (value != null) ? stringToBoolean(value) : defaultValue;
	}

	public Integer getIntegerProperty(String key) {
		return getIntegerProperty(key, null);
	}

	public Integer getIntegerProperty(String key, Integer defaultValue) {
		String value = getProperty(key);
		if (value == null) {
			return defaultValue;
		}
		Integer i = Integer.parseInt(value.trim());
		return (i != null) ? i : defaultValue;
	}

	public Float getFloatProperty(String key) {
		return getFloatProperty(key, null);
	}

	public Float getFloatProperty(String key, Float defaultValue) {
		String value = getProperty(key);
		if (value == null) {
			return defaultValue;
		}
		Float f = Float.parseFloat(value.trim());
		return (f != null) ? f : defaultValue;
	}

	private boolean stringToBoolean(String value) {
		String v = value.trim();
		if (v.equalsIgnoreCase("yes") || v.equalsIgnoreCase("da")
				|| v.equalsIgnoreCase("1") || v.equalsIgnoreCase("ja")
				|| v.equalsIgnoreCase("si")) {
			return true;
		} else if (v.equalsIgnoreCase("no") || v.equalsIgnoreCase("ne")
				|| v.equalsIgnoreCase("0") || v.equalsIgnoreCase("nein")) {
			return false;
		} else {
			throw new IllegalArgumentException("'"+value+"' cannot be parsed to boolean!");
		}
	}

	public ExtendedProperties getParentProperties() {
		return parentProperties;
	}

	public void setParentProperties(ExtendedProperties parentProperties) {
		this.parentProperties = parentProperties;
	}
	
	

}
