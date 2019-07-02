package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation;

import java.util.Arrays;
import java.util.List;

import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.RelatedCharacteristics;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.InternalStateContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.PrivacyLevelContext;

import com.att.research.xacml.api.XACML3;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;

public class StringComparisonMatch extends Match {
	private static final String NAME_ENTITY = "entity:name";
	private static final String CONTEXT_INTERNAL_STATE = "context:internalstate";
	private static final String CONTEXT_PRIVACY_LEVEL = "context:privacylevel";
	
	private final String value;
	
	public StringComparisonMatch(final RelatedCharacteristics entity) {
		super(ID_CATEGORY_ACTION, NAME_ENTITY);
		this.value = entity.getEntityName();
	}
	
	public StringComparisonMatch(final InternalStateContext context) {
		super(CONTEXT_INTERNAL_STATE);
		this.value = context.getState().getEntityName();
	}
	
	public StringComparisonMatch(final PrivacyLevelContext context) {
		super(CONTEXT_PRIVACY_LEVEL);
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
		// Attribute value
		final AttributeValueType attributeValue = new AttributeValueType();
		attributeValue.getContent().add(this.value);
		attributeValue.setDataType(XACML3.ID_DATATYPE_STRING.stringValue());
							
		// Attribute designator
		final AttributeDesignatorType attributeDesignator = new AttributeDesignatorType();
		attributeDesignator.setCategory(getCategoryId());
		attributeDesignator.setAttributeId(getAttributeId());
		attributeDesignator.setDataType(XACML3.ID_DATATYPE_STRING.stringValue());
		attributeDesignator.setMustBePresent(false);
							
		// Match
		final MatchType match = new MatchType();
		match.setAttributeValue(attributeValue);
		match.setAttributeDesignator(attributeDesignator);
		match.setMatchId(XACML3.ID_FUNCTION_STRING_EQUAL.stringValue());
		return Arrays.asList(match);
	}
}
