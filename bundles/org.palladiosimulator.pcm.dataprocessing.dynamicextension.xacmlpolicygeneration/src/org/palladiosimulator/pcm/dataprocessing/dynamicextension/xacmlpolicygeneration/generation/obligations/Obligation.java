package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.obligations;

import java.util.Arrays;
import java.util.List;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionType;

/**
 * Represents obligation expression(s) of an . Implementations of this interface can be used to
 * create obligations from contexts.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public interface Obligation {

    /**
     * Gets the XACML ObligationExpressionType of this obligation.
     * 
     * @return the XACML ObligationExpressionType of this obligation
     */
    ObligationExpressionType getObligation();

    /**
     * Gets a list of XACML ObligationExpressionType of this obligation. The default of this method
     * is {@code getObligation()} as a list. If this behavior is not wanted, this method should be
     * overridden.
     * 
     * @return a list of XACML ObligationExpressionType of this obligation
     */
    default List<ObligationExpressionType> getObligations() {
        return Arrays.asList(getObligation());
    }
}
