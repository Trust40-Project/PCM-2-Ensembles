package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.matches;

import java.util.Arrays;
import java.util.List;

import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.RelatedCharacteristics;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.InternalStateContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.PrivacyLevelContext;

import com.att.research.xacml.api.XACML3;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;

/**
 * Represents a match which uses a string comparison. Standard implementation uses string equality.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class StringComparisonMatch extends Match {
	private static final String NAME_ENTITY = "entity:name";
	private static final String CONTEXT_INTERNAL_STATE = "context:internalstate";
	private static final String CONTEXT_PRIVACY_LEVEL = "context:privacylevel";
	
	private final String value;
	
	/**
	 * Creates a new StringComparisonMatch for the entity name.
	 * 
	 * @param entity - the related characteristics representing the entity
	 */
	public StringComparisonMatch(final RelatedCharacteristics entity) {
		super(ID_CATEGORY_ACTION, NAME_ENTITY);
		this.value = entity.getEntityName();
	}
	
	/**
	 * Creates a new StringComparisonMatch for an internal state.
	 * 
	 * @param context - the internal state context
	 */
	public StringComparisonMatch(final InternalStateContext context) {
		super(ID_CATEGORY_RESOURCE, CONTEXT_INTERNAL_STATE);
		this.value = context.getState().getEntityName();
	}
	
	/**
	 * Creates a new StringComparisonMatch for a privacy level.
	 * 
	 * @param context - the privacy level context
	 */
	public StringComparisonMatch(final PrivacyLevelContext context) {
		super(ID_CATEGORY_RESOURCE, CONTEXT_PRIVACY_LEVEL);
		this.value = context.getLevel().getEntityName();
	}
	
	protected StringComparisonMatch(final String contextId, final String value) {
		super(contextId);
		this.value = value;
	}
	
	protected String getValue() {
		return this.value;
	}

	@Override
	public List<MatchType> getMatches() {
		final MatchType match = getEmptyMatch();
		// Attribute value
		match.getAttributeValue().getContent().add(this.value);
		match.getAttributeValue().setDataType(XACML3.ID_DATATYPE_STRING.stringValue());
							
		// Attribute designator
		match.getAttributeDesignator().setDataType(XACML3.ID_DATATYPE_STRING.stringValue());
							
		// Match
		match.setMatchId(XACML3.ID_FUNCTION_STRING_EQUAL.stringValue());
		return Arrays.asList(match);
	}
}
