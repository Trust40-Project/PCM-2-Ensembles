package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.RelatedCharacteristics;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.Context;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ContextCharacteristic;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.InternalStateContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.LocationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.OrganisationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.PrivacyLevelContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.RoleContext;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;

public class MatchExtractor {
	private final RelatedCharacteristics relatedCharacteristics;

	public MatchExtractor(final RelatedCharacteristics relatedCharacteristics) {
		this.relatedCharacteristics = relatedCharacteristics;
	}

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
