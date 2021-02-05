package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.generation.matches;

import com.att.research.xacml.api.XACML3;

/**
 * Represents a signature match.
 * @author vladsolovyev
 * @version 1.0
 */
public class SignatureMatch extends Match {

    private static final String METHOD_SIGNATURE = "methodSpecification:signature";

    /**
     * initialises a match with a attribute-category:action, function:string-equal, signature as an AttributeId and an expected value
     * @param attributeValue - an expected value of match
     */
    public SignatureMatch(String attributeValue) {
        super(XACML3.ID_ATTRIBUTE_CATEGORY_ACTION, XACML3.ID_FUNCTION_STRING_EQUAL, METHOD_SIGNATURE, attributeValue);
    }
}
