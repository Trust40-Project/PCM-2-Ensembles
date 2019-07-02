package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation;

import java.util.List;

import com.att.research.xacml.api.XACML3;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;

public abstract class Match {
	public static final String ID_CATEGORY_ACTION = XACML3.ID_ATTRIBUTE_CATEGORY_ACTION.stringValue();
	public static final String ID_CATEGORY_SUBJECT = XACML3.ID_SUBJECT_CATEGORY_ACCESS_SUBJECT.stringValue();
	
	private final String categoryId;
	private final String attributeId;
	
	protected Match(final String attributeId) {
		this(ID_CATEGORY_SUBJECT, attributeId);
	}
	
	protected Match(final String categoryId, final String attributeId) {
		this.categoryId = categoryId;
		this.attributeId = attributeId;
	}
	
	public abstract List<MatchType> getMatches();
	
	protected String getCategoryId() {
		return this.categoryId;
	}
	
	protected String getAttributeId() {
		return this.attributeId;
	}
}
