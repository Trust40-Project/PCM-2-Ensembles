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
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.PrivacyLevelContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.RoleContext;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;

public class MatchExtractor {
	private final RelatedCharacteristics relatedCharacteristics;

	public MatchExtractor(final RelatedCharacteristics relatedCharacteristics) {
		this.relatedCharacteristics = relatedCharacteristics;
	}

	public List<MatchType> getMatches() {
		List<MatchType> list = new ArrayList<>();
		// entity
		list.addAll(new StringComparisonMatch(this.relatedCharacteristics).getMatches());
		
		//TODO: frage ist das && oder ||, momentan &&
		
		// privacy level
		List<PrivacyLevelContext> listPrivacyContext = getContexts(this.relatedCharacteristics).filter(PrivacyLevelContext.class::isInstance)
				.map(PrivacyLevelContext.class::cast).collect(Collectors.toList());
		for (var privacyContext : listPrivacyContext) {
			list.addAll(new StringComparisonMatch(privacyContext).getMatches());
		}
		
		// internal state
		List<InternalStateContext> listStateContext = getContexts(this.relatedCharacteristics).filter(InternalStateContext.class::isInstance)
				.map(InternalStateContext.class::cast).collect(Collectors.toList());
		for (var stateContext : listStateContext) {
			list.addAll(new StringComparisonMatch(stateContext).getMatches());
		}
		
		// roles
		List<RoleContext> listRoleContext = getContexts(this.relatedCharacteristics).filter(RoleContext.class::isInstance)
					.map(RoleContext.class::cast).collect(Collectors.toList());
		for (var roleContext : listRoleContext) {
			list.addAll(new RegexMatchingMatch(roleContext).getMatches());
		}
			
		// locations
		List<LocationContext> listLocationContext = getContexts(this.relatedCharacteristics).filter(LocationContext.class::isInstance)
				.map(LocationContext.class::cast).collect(Collectors.toList());
		for (var locationContext : listLocationContext) {
			list.addAll(new RegexMatchingMatch(locationContext).getMatches());
		}
		
		return list;
	}

	private Stream<Context> getContexts(RelatedCharacteristics e) {
		return e.getCharacteristics().getOwnedCharacteristics().stream().filter(ContextCharacteristic.class::isInstance)
				.map(ContextCharacteristic.class::cast).flatMap(i -> i.getContext().stream());
	}

}
