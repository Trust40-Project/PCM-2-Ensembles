package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.matches;

import java.util.List;

import com.att.research.xacml.api.XACML3;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;

/**
 * Represents an attribute match which can contain several XACML matches which are necessary to
 * create a correct implementation of a transformation of a context to XACML match(es).
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public abstract class Match {
    protected static final String ID_CATEGORY_ACTION = XACML3.ID_ATTRIBUTE_CATEGORY_ACTION.stringValue();
    protected static final String ID_CATEGORY_SUBJECT = XACML3.ID_SUBJECT_CATEGORY_ACCESS_SUBJECT.stringValue();
    protected static final String ID_CATEGORY_RESOURCE = XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE.stringValue();
    protected static final String ID_CATEGORY_ENVIRONMENT = XACML3.ID_ATTRIBUTE_CATEGORY_ENVIRONMENT.stringValue();

    private final String categoryId;
    private final String attributeId;

    /**
     * Constructor for a match with the given attribute id, and category
     * SUBJECT_CATEGORY_ACCESS_SUBJECT.
     * 
     * @param attributeId
     *            - the given attribute id
     */
    protected Match(final String attributeId) {
        this(ID_CATEGORY_SUBJECT, attributeId);
    }

    /**
     * Constructor for a match with the given category id, and attribute id.
     * 
     * @param categoryId - the given category id
     * @param attributeId - the given attribute id
     */
    protected Match(final String categoryId, final String attributeId) {
        this.categoryId = categoryId;
        this.attributeId = attributeId;
    }

    /**
     * Gets the XACML matches of this match.
     * 
     * @return gets the XACML matches of this match
     */
    public abstract List<MatchType> getMatches();

    /**
     * Gets an "empty" XACML match, i.e. an match without match id, type and value.
     * This method should only be used by subclasses.
     * 
     * @return an match without match id, type and value
     */
    protected MatchType getEmptyMatch() {
        // Attribute value
        final AttributeValueType attributeValue = new AttributeValueType();

        // Attribute designator
        final AttributeDesignatorType attributeDesignator = new AttributeDesignatorType();
        attributeDesignator.setCategory(getCategoryId());
        attributeDesignator.setAttributeId(getAttributeId());
        attributeDesignator.setMustBePresent(false);

        // Match
        final MatchType match = new MatchType();
        match.setAttributeValue(attributeValue);
        match.setAttributeDesignator(attributeDesignator);
        return match;
    }

    /**
     * Gets the category id.
     * 
     * @return the category id
     */
    protected String getCategoryId() {
        return this.categoryId;
    }

    /**
     * Gets the attribute id.
     * 
     * @return the attribute id
     */
    protected String getAttributeId() {
        return this.attributeId;
    }
}
