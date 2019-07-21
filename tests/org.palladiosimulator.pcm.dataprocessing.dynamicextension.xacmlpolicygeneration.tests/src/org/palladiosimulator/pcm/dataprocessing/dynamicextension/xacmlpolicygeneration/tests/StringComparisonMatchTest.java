package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests;

import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataSpecification;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.RelatedCharacteristics;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.InternalStateContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.PrivacyLevelContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.MainLoader.ModelLoader;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.matches.StringComparisonMatch;

import com.att.research.xacml.api.XACML3;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;

import static org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests.TestUnitHandler.getContexts;

public class StringComparisonMatchTest {
	private DataSpecification data;
	
	private RelatedCharacteristics action;
	private InternalStateContext state;
	
	
	@Before
	public void setUp() {
		ModelLoader loader = new ModelLoader(TestUnitHandler.PATH_DYNAMIC, TestUnitHandler.PATH_DATA);
		this.data = loader.loadDataSpecification();
		this.action = data.getRelatedCharacteristics().get(0);
		this.state = getContexts(this.action).filter(InternalStateContext.class::isInstance)
				.map(InternalStateContext.class::cast).collect(Collectors.toList()).get(0);
	}
	
	@Test
	public void actionTest() {
		final StringComparisonMatch match = new StringComparisonMatch(this.action);
		Assert.assertEquals(1, match.getMatches().size());
		final MatchType matchType = match.getMatches().get(0);
		Assert.assertEquals("testAction", matchType.getAttributeValue().getContent().get(0));
		Assert.assertEquals(XACML3.ID_FUNCTION_STRING_EQUAL.toString(), matchType.getMatchId());
	}
	
	@Test
	public void stateTest() {
		final StringComparisonMatch match = new StringComparisonMatch(this.state);
		Assert.assertEquals(1, match.getMatches().size());
		final MatchType matchType = match.getMatches().get(0);
		Assert.assertEquals("INCIDENT_HAPPENED", matchType.getAttributeValue().getContent().get(0));
		Assert.assertEquals(XACML3.ID_FUNCTION_STRING_EQUAL.toString(), matchType.getMatchId());
	}
}
