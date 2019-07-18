package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests;

import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataSpecification;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ShiftContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.MainLoader.ModelLoader;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.matches.ShiftMatch;

import com.att.research.xacml.api.XACML3;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests.TestUnitHandler.getContexts;

public class ShiftMatchTest {
	private DataSpecification data;
	private ShiftContext shiftContext;
	
	@Before
	public void setUp() {
		ModelLoader loader = new ModelLoader(TestUnitHandler.PATH_DYNAMIC, TestUnitHandler.PATH_DATA);
		this.data = loader.loadDataSpecification();
		this.shiftContext = getContexts(data.getRelatedCharacteristics().get(0)).filter(ShiftContext.class::isInstance)
				.map(ShiftContext.class::cast).collect(Collectors.toList()).get(0);
	}
	
	@Test
	public void shiftTest() {
		final ShiftMatch match = new ShiftMatch(this.shiftContext);
		Assert.assertEquals(3, match.getMatches().size());
		
		final MatchType start = match.getMatches().get(0);
		assertEquals("06:00:00Z", start.getAttributeValue().getContent().get(0));
		assertEquals(XACML3.ID_DATATYPE_TIME.stringValue(), start.getAttributeValue().getDataType());
		assertEquals(XACML3.ID_FUNCTION_TIME_LESS_THAN_OR_EQUAL.stringValue(), start.getMatchId());
		
		final MatchType end = match.getMatches().get(1);
		assertEquals("14:00:00Z", end.getAttributeValue().getContent().get(0));
		assertEquals(XACML3.ID_DATATYPE_TIME.stringValue(), end.getAttributeValue().getDataType());
		assertEquals(XACML3.ID_FUNCTION_TIME_GREATER_THAN_OR_EQUAL.stringValue(), end.getMatchId());
		
		final MatchType name = match.getMatches().get(2);
		assertEquals("Shift 1", name.getAttributeValue().getContent().get(0));
		Assert.assertEquals(XACML3.ID_FUNCTION_STRING_EQUAL.toString(), name.getMatchId());
	}
}
