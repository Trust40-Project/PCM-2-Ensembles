package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.obligations;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.Context;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ExtensionContext;

import com.att.research.xacml.api.XACML3;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentExpressionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.EffectType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionType;

public class Obligation {
	//TODO: wenn es funktioniert, auch mit registry machen
	private static final String EXTENSION_OBLIGATON_ID = "obligation:extension";
	private static final String EXTENSION_ATTRIBUTE_ID = "context:extension";
	
	private final String text;
	
	public Obligation (final Context context) {
		//TODO 
		if (ExtensionContext.class.isInstance(context)) {
			final ExtensionContext extContext = (ExtensionContext) context;
			this.text = extContext.getExtensionCode();
		} else {
			this.text = null;
		}
	}
	
	public ObligationExpressionType getObligation() {
		if (this.text != null) {
			final AttributeValueType attributeValue = new AttributeValueType();
			attributeValue.setDataType(XACML3.ID_DATATYPE_STRING.stringValue());
			attributeValue.getContent().add(this.text);
			
			final AttributeAssignmentExpressionType expression = new AttributeAssignmentExpressionType();
			expression.setAttributeId(EXTENSION_ATTRIBUTE_ID);
			final QName qname = new QName(XACML3.XMLNS, XACML3.ELEMENT_ATTRIBUTEVALUE); //TODO qname ok?
			final JAXBElement<AttributeValueType> element 
				= new JAXBElement<AttributeValueType>(qname, AttributeValueType.class, attributeValue);
			expression.setExpression(element);
			
			final ObligationExpressionType obligation = new ObligationExpressionType();
			obligation.getAttributeAssignmentExpression().add(expression);
			obligation.setObligationId(EXTENSION_OBLIGATON_ID); //TODO
			obligation.setFulfillOn(EffectType.PERMIT);
			
			return obligation;
		}
		return null;
	}
}
