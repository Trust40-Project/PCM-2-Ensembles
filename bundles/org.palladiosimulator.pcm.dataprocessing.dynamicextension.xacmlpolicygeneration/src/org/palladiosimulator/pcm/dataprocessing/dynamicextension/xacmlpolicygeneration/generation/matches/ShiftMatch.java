package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.matches;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.datatype.XMLGregorianCalendar;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ShiftContext;

import com.att.research.xacml.api.XACML3;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;

/**
 * Represents a match for a shift context. The start end end time are used for comparison with the
 * environment attribute 'time' and the name of the shift is compared with a subject attribute.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class ShiftMatch extends Match {
    static {
        MatchRegistry.getInstance().put(ShiftContext.class, ShiftMatch.class);
    }
    
    private static final String CONTEXT_SHIFT = "context:shift:name";

    private final XMLGregorianCalendar startTime;
    private final XMLGregorianCalendar endTime;

    private final String shiftName;

    /**
     * Creates a new shift match for the given shift context.
     * 
     * @param context - the given shift context
     */
    public ShiftMatch(final ShiftContext context) {
        super(ID_CATEGORY_ENVIRONMENT, XACML3.ID_ENVIRONMENT_CURRENT_TIME.stringValue());
        Objects.requireNonNull(context);
        Objects.requireNonNull(context.getShift());
        this.startTime = Objects.requireNonNull(context.getShift().getStartTime());
        this.endTime = Objects.requireNonNull(context.getShift().getEndTime());
        this.shiftName = Objects.requireNonNull(context.getShift().getEntityName());
    }

    @Override
    public List<MatchType> getMatches() {
        final List<MatchType> matches = new ArrayList<>();

        // start
        final MatchType start = getShiftMatch(this.startTime);
        start.setMatchId(XACML3.ID_FUNCTION_TIME_LESS_THAN_OR_EQUAL.stringValue());
        matches.add(start);

        // end
        final MatchType end = getShiftMatch(this.endTime);
        end.setMatchId(XACML3.ID_FUNCTION_TIME_GREATER_THAN_OR_EQUAL.stringValue());
        matches.add(end);

        // add subject attribute match
        matches.addAll(new StringComparisonMatch(ID_CATEGORY_SUBJECT, CONTEXT_SHIFT, this.shiftName).getMatches());

        return matches;
    }

    /**
     * Gets the shift match to the given time.
     * 
     * @param time - the given time
     * @return the shift match for the given time, greater/less must yet be set
     */
    private MatchType getShiftMatch(final XMLGregorianCalendar time) {
        final MatchType match = getEmptyMatch();
        match.getAttributeValue().getContent().add(time.toString()); // timezone must be set in
                                                                     // model
        final String datatype = XACML3.ID_DATATYPE_TIME.stringValue();
        match.getAttributeValue().setDataType(datatype);
        match.getAttributeDesignator().setDataType(datatype);
        return match;
    }
}
