package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.*;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataSpecification;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.RelatedCharacteristics;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.Comparison;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.Context;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ContextCharacteristic;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.FloatingComparisonContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.IntegralComparisonContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.MainLoader.ModelLoader;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.matches.ComparisonMatch;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.handlers.SampleHandler;

import com.att.research.xacml.api.XACML3;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;

public class ComparisonMatchTest {
	private DataSpecification data;
	
	private IntegralComparisonContext intComparison;
	private FloatingComparisonContext floComparison;
	
	@Before
	public void setUp() {
		ModelLoader loader = new ModelLoader(SampleHandler.PATH_DYNAMIC, SampleHandler.PATH_DATA);
		this.data = loader.loadDataSpecification();
		this.intComparison = getContexts(data.getRelatedCharacteristics().get(0)).filter(IntegralComparisonContext.class::isInstance)
				.map(IntegralComparisonContext.class::cast).collect(Collectors.toList()).get(0);
		this.floComparison = getContexts(data.getRelatedCharacteristics().get(0)).filter(FloatingComparisonContext.class::isInstance)
				.map(FloatingComparisonContext.class::cast).collect(Collectors.toList()).get(0);
	}
	
	private Stream<Context> getContexts(RelatedCharacteristics e) {
		return e.getCharacteristics().getOwnedCharacteristics().stream().filter(ContextCharacteristic.class::isInstance)
				.map(ContextCharacteristic.class::cast).flatMap(i -> i.getContext().stream());
	}
	
	@Test
	public void intTest() {
		final ComparisonMatch match = new ComparisonMatch(this.intComparison);
		final MatchType matchType = match.getMatches().get(0);
		Assert.assertEquals(this.intComparison.getThreshold(), Integer.parseInt((String) matchType.getAttributeValue().getContent().get(0)));
		Assert.assertEquals(XACML3.ID_DATATYPE_INTEGER.stringValue(), matchType.getAttributeValue().getDataType());
		Assert.assertEquals(XACML3.ID_DATATYPE_INTEGER.stringValue(), matchType.getAttributeDesignator().getDataType());
		final String comparisonStr = this.intComparison.getComparison() == Comparison.GREATER
				? XACML3.ID_FUNCTION_INTEGER_GREATER_THAN.stringValue() : XACML3.ID_FUNCTION_INTEGER_LESS_THAN.stringValue();
		Assert.assertEquals(comparisonStr, matchType.getMatchId());
	}
	
	@Test
	public void floTest() {
		final ComparisonMatch match = new ComparisonMatch(this.floComparison);
		final MatchType matchType = match.getMatches().get(0);
		final double delta = 1E-8;
		Assert.assertEquals(this.floComparison.getThreshold(), Double.parseDouble((String) matchType.getAttributeValue().getContent().get(0)), delta);
		Assert.assertEquals(XACML3.ID_DATATYPE_DOUBLE.stringValue(), matchType.getAttributeValue().getDataType());
		Assert.assertEquals(XACML3.ID_DATATYPE_DOUBLE.stringValue(), matchType.getAttributeDesignator().getDataType());
		final String comparisonStr = this.intComparison.getComparison() == Comparison.GREATER
				? XACML3.ID_FUNCTION_DOUBLE_GREATER_THAN.stringValue() : XACML3.ID_FUNCTION_DOUBLE_LESS_THAN.stringValue();
		Assert.assertEquals(comparisonStr, matchType.getMatchId());
	}
}
