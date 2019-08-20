package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests;

import java.util.stream.Collectors;

import org.junit.*;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataSpecification;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.LocationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.OrganisationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.PrivacyLevelContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.RoleContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.MainLoader.ModelLoader;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.matches.RegexMatchingMatch;

import com.att.research.xacml.api.XACML3;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;

import static org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests.TestUnitHandler.getContexts;

public class RegexMatchingMatchTest {
	private DataSpecification data;
	
	private LocationContext location;
	private RoleContext role;
	private OrganisationContext organisation;
	
	private PrivacyLevelContext privacy;
	
	@Before
	public void setUp() {
		ModelLoader loader = new ModelLoader(TestUnitHandler.PATH_DATA);
		this.data = loader.loadDataSpecification();
		this.location = getContexts(data.getRelatedCharacteristics().get(0)).filter(LocationContext.class::isInstance)
				.map(LocationContext.class::cast).collect(Collectors.toList()).get(0);
		this.role = getContexts(data.getRelatedCharacteristics().get(0)).filter(RoleContext.class::isInstance)
				.map(RoleContext.class::cast).collect(Collectors.toList()).get(0);
		this.organisation = getContexts(data.getRelatedCharacteristics().get(0)).filter(OrganisationContext.class::isInstance)
				.map(OrganisationContext.class::cast).collect(Collectors.toList()).get(0);
		
		this.privacy = getContexts(data.getRelatedCharacteristics().get(0)).filter(PrivacyLevelContext.class::isInstance)
				.map(PrivacyLevelContext.class::cast).collect(Collectors.toList()).get(0);
	}
	
	/**
	 * Tests a location match.
	 */
	@Test
	public void locationTest() {
		final RegexMatchingMatch match = new RegexMatchingMatch(this.location);
		Assert.assertEquals(1, match.getMatches().size());
		final MatchType matchType = match.getMatches().get(0);
		final String locationRegex = matchType.getAttributeValue().getContent().get(0).toString();
		Assert.assertEquals("(\\QProduction_Hall\\E)|(\\QProduction_Hall_Section_1\\E)", locationRegex);
		Assert.assertEquals(XACML3.ID_FUNCTION_STRING_REGEXP_MATCH.toString(), matchType.getMatchId());
	}
	
	/**
	 * Tests a role match.
	 */
	@Test
	public void roleTest() {
		final RegexMatchingMatch match = new RegexMatchingMatch(this.role);
		Assert.assertEquals(1, match.getMatches().size());
		final MatchType matchType = match.getMatches().get(0);
		final String roleRegex = matchType.getAttributeValue().getContent().get(0).toString();
		Assert.assertEquals("(\\QWorker\\E)|(\\QWInspector\\E)", roleRegex);
		Assert.assertEquals(XACML3.ID_FUNCTION_STRING_REGEXP_MATCH.toString(), matchType.getMatchId());
	}
	
	/**
	 * Tests an organisation match.
	 */
	@Test
	public void organisationTest() {
		final RegexMatchingMatch match = new RegexMatchingMatch(this.organisation);
		Assert.assertEquals(1, match.getMatches().size());
		final MatchType matchType = match.getMatches().get(0);
		final String organisationRegex = matchType.getAttributeValue().getContent().get(0).toString();
		Assert.assertEquals("(\\QA\\E)|(\\QASub\\E)", organisationRegex);
		Assert.assertEquals(XACML3.ID_FUNCTION_STRING_REGEXP_MATCH.toString(), matchType.getMatchId());
	}
	
	/**
	 * Tests a privacy level match.
	 */
	@Test
	public void privacyTest() {
		final RegexMatchingMatch match = new RegexMatchingMatch(this.privacy);
		Assert.assertEquals(1, match.getMatches().size());
		final MatchType matchType = match.getMatches().get(0);
		Assert.assertEquals("(PUBLIC)|(RESTRICTED)|(SECRET)|(UNDEFINED)", matchType.getAttributeValue().getContent().get(0));
		Assert.assertEquals(XACML3.ID_FUNCTION_STRING_REGEXP_MATCH.toString(), matchType.getMatchId());
	}
}
