package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.generation.matches;

import com.att.research.xacml.api.XACML3;

/**
 * Represents a match of an attribute context.
 * @author vladsolovyev
 * @version 1.0
 */
public class AttributeContextMatch extends Match {

    private static final String ATTRIBUTE = "context:AttributeContext:ContextType:EntityName";

    /**
     * initialises a match with a subject-category:access-subject, function:string-regexp-match, context type entity name as an AttributeId and an expected value
     * @param attributeValue - an expected value of match
     */
    public AttributeContextMatch(String attributeValue, String dataType) {
        super(XACML3.ID_SUBJECT_CATEGORY_ACCESS_SUBJECT, XACML3.ID_FUNCTION_STRING_REGEXP_MATCH, String.format("%s:%s", ATTRIBUTE, dataType), attributeValue);
    }
}
