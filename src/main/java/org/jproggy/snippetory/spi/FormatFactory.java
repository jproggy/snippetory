package de.jproggy.snippetory.spi;

import java.util.Locale;


public interface FormatFactory {
	Format create(String definition);	
	Format create(String definition, Locale l);	

}
