package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.matches;

import java.util.Arrays;
import java.util.List;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.Comparison;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.FloatingComparisonContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.IntegralComparisonContext;

import com.att.research.xacml.api.XACML3;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;

/**
 * Represents a match used for comparisons.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class ComparisonMatch extends Match {
    private static final String CONTEXT_INT_COMPARISON = "context:comparison:int";
    private static final String CONTEXT_DOUBLE_COMPARISON = "context:comparison:double";

    private final boolean isFloating;
    private final int intValue;
    private final double doubleValue;
    private final Comparison comparison;

    /**
     * Creates a new ComparisonMatch.
     * 
     * @param context
     *            - an integral comparison context
     */
    public ComparisonMatch(final IntegralComparisonContext context) {
        super(ID_CATEGORY_RESOURCE, CONTEXT_INT_COMPARISON);
        this.comparison = context.getComparison();
        this.intValue = context.getThreshold();
        this.doubleValue = 0;
        this.isFloating = false;
    }

    /**
     * Creates a new ComparisonMatch.
     * 
     * @param context
     *            - a floating comparison context
     */
    public ComparisonMatch(final FloatingComparisonContext context) {
        super(ID_CATEGORY_RESOURCE, CONTEXT_DOUBLE_COMPARISON);
        this.comparison = context.getComparison();
        this.intValue = 0;
        this.doubleValue = context.getThreshold();
        this.isFloating = true;
    }

    @Override
    public List<MatchType> getMatches() {
        final MatchType match = getEmptyMatch();
        final String datatype = this.isFloating ? XACML3.ID_DATATYPE_DOUBLE.stringValue()
                : XACML3.ID_DATATYPE_INTEGER.stringValue();
        // Attribute value
        match.getAttributeValue().getContent().add(this.isFloating ? "" + this.doubleValue : "" + this.intValue);
        match.getAttributeValue().setDataType(datatype);

        // Attribute designator
        match.getAttributeDesignator().setDataType(datatype);

        // Match
        final String function;
        if (this.isFloating) {
            function = comparison == Comparison.GREATER ? XACML3.ID_FUNCTION_DOUBLE_GREATER_THAN.stringValue()
                    : XACML3.ID_FUNCTION_DOUBLE_LESS_THAN.stringValue();
        } else {
            function = comparison == Comparison.GREATER ? XACML3.ID_FUNCTION_INTEGER_GREATER_THAN.stringValue()
                    : XACML3.ID_FUNCTION_INTEGER_LESS_THAN.stringValue();
        }
        match.setMatchId(function);
        return Arrays.asList(match);
    }
}
