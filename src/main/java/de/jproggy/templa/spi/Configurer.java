package de.jproggy.templa.spi;

import java.util.ServiceLoader;

/**
 * This is just an interface to tag classes that are loaded on startup to register additional
 * elements on startup. Additional to tagging there must be a file named 
 * de.jproggy.templa.spi.Configurer containing the name of your implementation class.
 * Templa will use the {@link ServiceLoader} mechanism to load those classes to enable
 * you to have your additions available when you need them. Be aware that Templa can only 
 * load services if it's {@link ClassLoader} can find them.
 *   
 * @author Sir RotN
 */
public interface Configurer {

}
