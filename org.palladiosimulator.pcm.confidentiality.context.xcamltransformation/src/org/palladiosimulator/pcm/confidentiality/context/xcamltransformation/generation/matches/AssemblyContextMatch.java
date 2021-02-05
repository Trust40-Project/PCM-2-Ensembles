package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.generation.matches;

import com.att.research.xacml.api.XACML3;

/**
 * Represents a match of assembly context.
 * @author vladsolovyev
 * @version 1.0
 */
public class AssemblyContextMatch extends Match {

    private static final String ASSEMBLY_CONTEXT = "methodSpecification:assemblyContext";

    /**
     * initialises a match with a attribute-category:action, function:string-equal, assemblyContext as an AttributeId and an expected value
     * @param attributeValue - an expected value of match
     */
    public AssemblyContextMatch(String attributeValue) {
        super(XACML3.ID_ATTRIBUTE_CATEGORY_ACTION, XACML3.ID_FUNCTION_STRING_EQUAL, ASSEMBLY_CONTEXT, attributeValue);
    }
}
