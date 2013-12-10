package org.liva.core;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.liva.core.util.UTF8Control;

public class LocaleMessages {

	private ResourceBundle resourceBundle;
	private String boundleName;

	public LocaleMessages(String boundleName) {
		initResourceBundle(boundleName, (Locale) null);
	}
	
	public LocaleMessages(String boundleName, String locale) {
		Locale localeObject = new Locale(locale);
		initResourceBundle(boundleName, localeObject);
	}
	
	public LocaleMessages(String boundleName, Locale locale) {
		initResourceBundle(boundleName, locale);
	}
	
	private void initResourceBundle(String boundleName, Locale locale) {
		this.boundleName = boundleName;
				
		if (locale == null) {
			resourceBundle = ResourceBundle.getBundle(boundleName, new UTF8Control());
		} else {
			resourceBundle = ResourceBundle.getBundle(boundleName, locale, new UTF8Control());
		}
	}
	
	public void setLocale(String code) {
		Locale locale = new Locale(code);
		resourceBundle = ResourceBundle.getBundle(boundleName, locale, new UTF8Control());
	}
	
	public void setLocale(Locale locale) {
		resourceBundle = ResourceBundle.getBundle(boundleName, locale, new UTF8Control());
	}

	public  String getString(String key) {
		try {
			return resourceBundle.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	}
