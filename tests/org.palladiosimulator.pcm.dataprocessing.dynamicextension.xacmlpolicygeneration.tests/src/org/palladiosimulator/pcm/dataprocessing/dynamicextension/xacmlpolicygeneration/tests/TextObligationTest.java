package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests;

import static org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests.TestUnitHandler.getContexts;

import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataSpecification;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ExtensionContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.PrerequisiteContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.MainLoader.ModelLoader;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.obligations.TextObligation;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationExpressionType;

public class TextObligationTest {
	private DataSpecification data;
	
	private ExtensionContext extension;
	private PrerequisiteContext prerequisite;
	
	@Before
	public void setUp() {
		ModelLoader loader = new ModelLoader(TestUnitHandler.DATA_PATH);
		this.data = loader.loadDataSpecification();
		this.extension = getContexts(this.data.getRelatedCharacteristics().get(0)).filter(ExtensionContext.class::isInstance)
				.map(ExtensionContext.class::cast).collect(Collectors.toList()).get(0);
		this.prerequisite = getContexts(this.data.getRelatedCharacteristics().get(0)).filter(PrerequisiteContext.class::isInstance)
				.map(PrerequisiteContext.class::cast).collect(Collectors.toList()).get(0);
	}
	
	/**
	 * Tests an extension obligation.
	 */
	@Test
	public void extensionTest() {
		final TextObligation obligation = new TextObligation(this.extension);
		final ObligationExpressionType obligationType = obligation.getObligation();
		final Object textValue = ((AttributeValueType) obligationType.getAttributeAssignmentExpression().get(0).getExpression().getValue()).getContent().get(0);
		Assert.assertEquals("testExtensionMethod", textValue);
	}
	
	/**
	 * Tests a prerequisite obligation.
	 */
	@Test
	public void prerequisiteTest() {
		final TextObligation obligation = new TextObligation(this.prerequisite);
		final ObligationExpressionType obligationType = obligation.getObligation();
		final Object textValue = ((AttributeValueType) obligationType.getAttributeAssignmentExpression().get(0).getExpression().getValue()).getContent().get(0);
		Assert.assertEquals("checkAccess", textValue);
	}
}
