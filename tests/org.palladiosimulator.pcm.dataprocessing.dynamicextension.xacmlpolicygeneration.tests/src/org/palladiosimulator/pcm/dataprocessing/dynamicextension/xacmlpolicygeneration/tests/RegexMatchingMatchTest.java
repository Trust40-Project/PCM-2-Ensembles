package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.*;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataSpecification;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.RelatedCharacteristics;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.Context;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ContextCharacteristic;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.LocationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.OrganisationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.RoleContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.MainLoader.ModelLoader;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.RegexMatchingMatch;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.handlers.SampleHandler;

import com.att.research.xacml.api.XACML3;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;

public class RegexMatchingMatchTest {
	private DataSpecification data;
	
	private LocationContext location;
	private RoleContext role;
	private OrganisationContext organisation;
	
	@Before
	public void setUp() {
		ModelLoader loader = new ModelLoader(SampleHandler.PATH_DYNAMIC, SampleHandler.PATH_DATA);
		this.data = loader.loadDataSpecification();
		this.location = getContexts(data.getRelatedCharacteristics().get(0)).filter(LocationContext.class::isInstance)
				.map(LocationContext.class::cast).collect(Collectors.toList()).get(0);
		this.role = getContexts(data.getRelatedCharacteristics().get(0)).filter(RoleContext.class::isInstance)
				.map(RoleContext.class::cast).collect(Collectors.toList()).get(0);
		this.organisation = getContexts(data.getRelatedCharacteristics().get(0)).filter(OrganisationContext.class::isInstance)
				.map(OrganisationContext.class::cast).collect(Collectors.toList()).get(0);
	}
	
	private Stream<Context> getContexts(RelatedCharacteristics e) {
		return e.getCharacteristics().getOwnedCharacteristics().stream().filter(ContextCharacteristic.class::isInstance)
				.map(ContextCharacteristic.class::cast).flatMap(i -> i.getContext().stream());
	}
	
	@Test
	public void locationTest() {
		final RegexMatchingMatch match = new RegexMatchingMatch(this.location);
		final MatchType matchType = match.getMatches().get(0);
		final String locationRegex = matchType.getAttributeValue().getContent().get(0).toString();
		Assert.assertEquals("(\\QProduction_Hall\\E)|(\\QProduction_Hall_Section_1\\E)", locationRegex);
		Assert.assertEquals(XACML3.ID_FUNCTION_STRING_REGEXP_MATCH.toString(), matchType.getMatchId());
	}
	
	@Test
	public void roleTest() {
		final RegexMatchingMatch match = new RegexMatchingMatch(this.role);
		final MatchType matchType = match.getMatches().get(0);
		final String roleRegex = matchType.getAttributeValue().getContent().get(0).toString();
		Assert.assertEquals("(\\QWorker\\E)|(\\QWInspector\\E)", roleRegex);
		Assert.assertEquals(XACML3.ID_FUNCTION_STRING_REGEXP_MATCH.toString(), matchType.getMatchId());
	}
	
	@Test
	public void organisationTest() {
		final RegexMatchingMatch match = new RegexMatchingMatch(this.organisation);
		final MatchType matchType = match.getMatches().get(0);
		final String organisationRegex = matchType.getAttributeValue().getContent().get(0).toString();
		Assert.assertEquals("(\\QA\\E)|(\\QHans A.\\E)|(\\QASub\\E)", organisationRegex);
		Assert.assertEquals(XACML3.ID_FUNCTION_STRING_REGEXP_MATCH.toString(), matchType.getMatchId());
	}
}
