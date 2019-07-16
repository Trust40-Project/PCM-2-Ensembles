package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.obligations;

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
 * Represents an obligation for a single string value.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class TextObligation implements Obligation {
	private static final String EXTENSION_OBLIGATION_ID = "obligation:extension";
	private static final String EXTENSION_ATTRIBUTE_ID = "context:extension";
	private static final String PREREQUISITE_OBLIGATION_ID = "obligation:prerequisite";
	private static final String PREREQUISITE_ATTRIBUTE_ID = "context:prerequisite";

	private final String text;
	
	private final String obligationId;
	private final String attributeId;

	/**
	 * Creates a new text obligation for an extension context. 
	 * The extension code is saved in the attribute value.
	 * 
	 * @param context - an extension context
	 */
	public TextObligation(final ExtensionContext context) {
		this.text = context.getExtensionCode();
		this.obligationId = EXTENSION_OBLIGATION_ID;
		this.attributeId = EXTENSION_ATTRIBUTE_ID + ":" + context.getEntityName();
	}
	
	/**
	 * Creates a new text obligation for a prerequisite context. 
	 * The prerequisite operation signature is saved in the attribute value.
	 * 
	 * @param context - a prerequisite context
	 */
	public TextObligation(final PrerequisiteContext context) {
		this.text = context.getPrerequisite().getPrerequisite().getEntityName();
		this.obligationId = PREREQUISITE_OBLIGATION_ID;
		this.attributeId = PREREQUISITE_ATTRIBUTE_ID + ":" + context.getEntityName();
	}

	@Override
	public ObligationExpressionType getObligation() {
		final AttributeValueType attributeValue = new AttributeValueType();
		attributeValue.setDataType(XACML3.ID_DATATYPE_STRING.stringValue());
		attributeValue.getContent().add(this.text);
		
		final AttributeAssignmentExpressionType expression = new AttributeAssignmentExpressionType();
		expression.setAttributeId(this.attributeId);
		final QName qname = new QName(XACML3.XMLNS, XACML3.ELEMENT_ATTRIBUTEVALUE);
		final JAXBElement<AttributeValueType> element = new JAXBElement<AttributeValueType>(qname,
				AttributeValueType.class, attributeValue);
		expression.setExpression(element);

		final ObligationExpressionType obligation = new ObligationExpressionType();
		obligation.getAttributeAssignmentExpression().add(expression);
		obligation.setObligationId(this.obligationId);
		obligation.setFulfillOn(EffectType.PERMIT);

		return obligation;
	}
}
