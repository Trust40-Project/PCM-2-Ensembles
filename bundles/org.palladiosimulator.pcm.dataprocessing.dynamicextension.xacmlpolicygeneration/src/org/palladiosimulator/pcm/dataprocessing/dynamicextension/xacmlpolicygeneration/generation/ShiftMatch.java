package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ShiftContext;

import com.att.research.xacml.api.XACML3;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;

//TODO: noch fragen beantworten, dann noch java doc und test

public class ShiftMatch extends Match {
	private static final String CONTEXT_SHIFT = "context:shift";
	
	private final XMLGregorianCalendar startTime;
	private final XMLGregorianCalendar endTime;
	
	private final boolean isEnvironmental;
	
	public ShiftMatch(final ShiftContext context) {
		super(ID_CATEGORY_ENVIRONMENT, XACML3.ID_ENVIRONMENT_CURRENT_TIME.stringValue());
		this.startTime = context.getShift().getStartTime();
		this.endTime = context.getShift().getEndTime();
		this.isEnvironmental = true;
	}
	
	private ShiftMatch(final ShiftMatch other) {
		super(CONTEXT_SHIFT);
		this.startTime = other.startTime;
		this.endTime = other.endTime;
		this.isEnvironmental = false;
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
		
		if (this.isEnvironmental)  {
			// add subject attribute matches
			matches.addAll(new ShiftMatch(this).getMatches());
		}
		
		return matches;
	}
	
	private MatchType getShiftMatch(final XMLGregorianCalendar time) {
		final MatchType match = getEmptyMatch();
		match.getAttributeValue().getContent().add(time.toString() + "+00:00"); //TODO assuming timezone UTC 
		final String datatype = XACML3.ID_DATATYPE_TIME.stringValue(); //TODO time ok, or datetime?
		match.getAttributeValue().setDataType(datatype);
		match.getAttributeDesignator().setDataType(datatype);
		return match;
	}
}
