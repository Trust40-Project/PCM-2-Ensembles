package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.obligations;

import java.util.Objects;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ExtensionContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.PrerequisiteContext;

import com.att.research.xacml.api.XACML3;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.EffectType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionType;

/**
 * Represents an obligation for a single string value and an optional boolean value determining the
 * insertion of the text.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class TextObligation implements Obligation {
    static {
        ObligationRegistry.getInstance().put(ExtensionContext.class, TextObligation.class);
        ObligationRegistry.getInstance().put(PrerequisiteContext.class, TextObligation.class);
    }
    
    private static final String EXTENSION_OBLIGATION_ID = "obligation:extension";
    private static final String EXTENSION_ATTRIBUTE_ID = "context:extension";
    private static final String EXTENSION_END_ATTRIBUTE_ID = "context:extension:isend";
    private static final String PREREQUISITE_OBLIGATION_ID = "obligation:prerequisite";
    private static final String PREREQUISITE_ATTRIBUTE_ID = "context:prerequisite";

    private final String text;
    private final Boolean isAtEnd;

    private final String obligationId;
    private final String attributeId;

    /**
     * Creates a new text obligation for an extension context. The extension code is saved in the
     * attribute value.
     * 
     * @param context
     *            - an extension context
     */
    public TextObligation(final ExtensionContext context) {
        Objects.requireNonNull(context);
        this.text = Objects.requireNonNull(context.getCustomAccessPolicy());
        this.isAtEnd = context.isAddAtEnd();
        this.obligationId = EXTENSION_OBLIGATION_ID;
        this.attributeId = EXTENSION_ATTRIBUTE_ID + ":" + context.getEntityName();
    }

    /**
     * Creates a new text obligation for a prerequisite context. The prerequisite operation
     * signature is saved in the attribute value.
     * 
     * @param context
     *            - a prerequisite context
     */
    public TextObligation(final PrerequisiteContext context) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(context.getPrerequisite());
        Objects.requireNonNull(context.getPrerequisite().getPrerequisite());
        this.text = Objects.requireNonNull(context.getPrerequisite().getPrerequisite().getEntityName());
        this.isAtEnd = null;
        this.obligationId = PREREQUISITE_OBLIGATION_ID;
        this.attributeId = PREREQUISITE_ATTRIBUTE_ID + ":" + Objects.requireNonNull(context.getEntityName());
    }

    @Override
    public ObligationExpressionType getObligation() {
        final ObligationExpressionType obligation = new ObligationExpressionType();
        final var textExpression = getExpression(this.attributeId, this.text, XACML3.ID_DATATYPE_STRING.stringValue());
        obligation.getAttributeAssignmentExpression().add(textExpression);
        if (this.isAtEnd != null) {
            final var endExpression = getExpression(EXTENSION_END_ATTRIBUTE_ID, "" + this.isAtEnd,
                    XACML3.ID_DATATYPE_BOOLEAN.stringValue());
            obligation.getAttributeAssignmentExpression().add(endExpression);
        }
        obligation.setObligationId(this.obligationId);
        obligation.setFulfillOn(EffectType.PERMIT);

        return obligation;
    }

    /**
     * Gets an expression type with the given attribute id, value and datatype.
     * 
     * @param attributeId - the given attribute id
     * @param value - the value
     * @param datatype - the data type
     * @return the XACML AttributeAssignmentExpression type
     */
    private AttributeAssignmentExpressionType getExpression(final String attributeId, final String value,
            final String datatype) {
        final AttributeValueType attributeValue = new AttributeValueType();
        attributeValue.setDataType(datatype);
        attributeValue.getContent().add(value);

        final AttributeAssignmentExpressionType expression = new AttributeAssignmentExpressionType();
        expression.setAttributeId(attributeId);
        final QName qname = new QName(XACML3.XMLNS, XACML3.ELEMENT_ATTRIBUTEVALUE);
        final JAXBElement<AttributeValueType> element = new JAXBElement<AttributeValueType>(qname,
                AttributeValueType.class, attributeValue);
        expression.setExpression(element);
        return expression;
    }
}
