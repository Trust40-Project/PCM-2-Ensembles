package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.RelatedCharacteristics;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.Context;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ContextCharacteristic;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.FloatingComparisonContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.IntegralComparisonContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.InternalStateContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.LocationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.OrganisationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.PrivacyLevelContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.RoleContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ShiftContext;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;

/**
 * Extracts the matches from the context information of a related characteristics.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class MatchExtractor {
	private final RelatedCharacteristics relatedCharacteristics;

	/**
	 * Creates a new match extractor. 
	 * 
	 * @param relatedCharacteristics - the characteristics
	 */
	public MatchExtractor(final RelatedCharacteristics relatedCharacteristics) {
		this.relatedCharacteristics = relatedCharacteristics;
	}

	/**
	 * Extracts a list of lists of matches from the given characteristic. The matches in a list are to be combinde with AND.
	 * The different lists of matches are to be combined with AND.
	 * 
	 * @return a list of lists which are to be combined with OR
	 */
	public List<List<MatchType>> getMatches() {
		final List<List<MatchType>> list = new ArrayList<>();
		for (int i = 0; i < getCharacteristicsList(this.relatedCharacteristics).size(); i++) {
			list.add(getMatches(i));
		}
		return list;
	}
	
	private List<MatchType> getMatches(final int index) {
		List<MatchType> list = new ArrayList<>();
		// entity
		list.addAll(new StringComparisonMatch(this.relatedCharacteristics).getMatches());
		
		// privacy level
		for (var privacyContext : getContextList(PrivacyLevelContext.class, index)) {
			list.addAll(new StringComparisonMatch((PrivacyLevelContext) privacyContext).getMatches());
		}
		
		// internal state
		for (var stateContext : getContextList(InternalStateContext.class, index)) {
			list.addAll(new StringComparisonMatch((InternalStateContext) stateContext).getMatches());
		}
		
		// roles
		for (var roleContext : getContextList(RoleContext.class, index)) {
			list.addAll(new RegexMatchingMatch((RoleContext) roleContext).getMatches());
		}
			
		// locations
		for (var locationContext : getContextList(LocationContext.class, index)) {
			list.addAll(new RegexMatchingMatch((LocationContext) locationContext).getMatches());
		}
		
		// organisations
		for (var organisationContext :  getContextList(OrganisationContext.class, index)) {
			list.addAll(new RegexMatchingMatch((OrganisationContext) organisationContext).getMatches());
		}
		
		// comparison
		for (var comparisonContext : getContextList(IntegralComparisonContext.class, index)) {
			list.addAll(new ComparisonMatch((IntegralComparisonContext) comparisonContext).getMatches());
		}
		for (var comparisonContext : getContextList(FloatingComparisonContext.class, index)) {
			list.addAll(new ComparisonMatch((FloatingComparisonContext) comparisonContext).getMatches());
		}
		
		// shift
		for (var shiftContext : getContextList(ShiftContext.class, index)) {
			list.addAll(new ShiftMatch((ShiftContext) shiftContext).getMatches());
		}
		
		return list;
	}
	
	private List<? extends Context> getContextList(final Class<? extends Context> contextClass, final int index) {
		return getContexts(this.relatedCharacteristics, index).filter(contextClass::isInstance)
				.map(contextClass::cast).collect(Collectors.toList());
	}
	
	private List<ContextCharacteristic> getCharacteristicsList(final RelatedCharacteristics e) {
		return e.getCharacteristics().getOwnedCharacteristics().stream().filter(ContextCharacteristic.class::isInstance)
				.map(ContextCharacteristic.class::cast).collect(Collectors.toList());
	}

	private Stream<Context> getContexts(final RelatedCharacteristics e, final int index) {
		return getCharacteristicsList(e).get(index).getContext().stream();
	}

}
