package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.handlers;

import org.eclipse.e4.core.services.log.Logger;

/**
 * Represents a mock-up for the eclipse logger.
 * 
 * @author Jonathan Schenkenberger
 * @verison 1.0
 */
public class MockLogger extends Logger {
    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    @Override
    public void error(Throwable t, String message) {
        error(message);
    }
    
    @Override
    public void error(String message) {
        System.err.println("ERROR | " + message);
    }

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public void warn(Throwable t, String message) {
        warn(message);
    }
    
    @Override
    public void warn(String message) {
        System.err.println("WARN | " + message);
    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public void info(Throwable t, String message) {
       info(message);
    }
    
    @Override
    public void info(String message) {
        System.err.println("INFO | " + message);
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void trace(Throwable t, String message) {
        trace(message);
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void debug(Throwable t) {
        debug(t.getMessage());
    }

    @Override
    public void debug(Throwable t, String message) {
        debug(message);
    }
}
