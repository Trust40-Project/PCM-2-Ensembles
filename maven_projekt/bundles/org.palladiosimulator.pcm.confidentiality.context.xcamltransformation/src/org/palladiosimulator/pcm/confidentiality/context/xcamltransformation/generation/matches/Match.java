package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.generation.matches;

import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.XACML3;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;

/**
 * Abstract Match class which is responsible for creating of match types
 * @author vladsolovyev
 * @version 1.0.0
 */
public abstract class Match {

    private final String attributeId;
    private final String attributeValue;
    private final Identifier matchId;
    private final Identifier category;

    /**
     * Initialises a new object of Match
     * @param category - is a category of the match (e.g. urn:oasis:names:tc:xacml:3.0:attribute-category:action)
     * @param matchId - is an id of the match (e.g. urn:oasis:names:tc:xacml:1.0:function:string-equal)
     * @param attributeId - is an id of the attribute (e.g. methodSpecification:signature)
     * @param attributeValue - is a value of the attribute (may be any value)
     */
    protected Match(Identifier category, Identifier matchId, String attributeId, String attributeValue) {
        this.category = category;
        this.matchId = matchId;
        this.attributeId = attributeId;
        this.attributeValue = attributeValue;
    }

    /**
     * create a match type with all its attributes.
     * @return a created match type
     */
    public MatchType createMatchType() {
        MatchType match = new MatchType();
        match.setMatchId(matchId.stringValue());
        match.setAttributeValue(createAttributeValueType());
        match.setAttributeDesignator(createAttributeDesignatorType());
        match.getAttributeDesignator().setDataType(XACML3.ID_DATATYPE_STRING.stringValue());
        return match;
    }

    /**
     * create an attribute value type which contains a data type an expected value of the attribute
     * @return created attribute value type
     */
    private AttributeValueType createAttributeValueType() {
        AttributeValueType attributeValueType = new AttributeValueType();
        attributeValueType.setDataType(XACML3.ID_DATATYPE_STRING.stringValue());
        attributeValueType.getContent().add(attributeValue);
        return attributeValueType;
    }

    /**
     * creates an attribute designator type which contains a category and an attribute id
     * @return created attribute designator type
     */
    private AttributeDesignatorType createAttributeDesignatorType() {
        AttributeDesignatorType attributeDesignator = new AttributeDesignatorType();
        attributeDesignator.setCategory(category.stringValue());
        attributeDesignator.setAttributeId(attributeId);
        //check it
        attributeDesignator.setMustBePresent(false);
        return attributeDesignator;
    }
}
