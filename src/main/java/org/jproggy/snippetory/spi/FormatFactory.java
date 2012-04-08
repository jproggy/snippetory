package org.jproggy.snippetory.spi;

import java.util.Locale;

/**
 * To be able to use a format in Snippetory one has to register FormatFactory able to create
 * it. Each {@link Format} has to be defined by a single attribute value. This means sometimes
 * there will be some parsing to do. 
 * 
 * @author B. Ebertz
 */
public interface FormatFactory {
	/**
	 * 
	 * @param definition the attribute value from the template 
	 * @param locale the locale defined for the template
	 * @return the format that will be assigned to the location
	 */
	Format create(String definition, Locale locale);	
}
