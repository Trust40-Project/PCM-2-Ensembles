package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.matches;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.RelatedCharacteristics;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.ContextHandler;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;

/**
 * Extracts the matches from the context information of a related
 * characteristics.
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
	 * Extracts a list of lists of matches from the given characteristic. The
	 * matches in a list are to be combinde with AND. The different lists of matches
	 * are to be combined with AND.
	 * 
	 * @return a list of lists which are to be combined with OR
	 */
	public List<List<MatchType>> getMatches() {
		final List<List<MatchType>> list = new ArrayList<>();
		for (int i = 0; i < ContextHandler.getCharacteristicsList(this.relatedCharacteristics).size(); i++) {
			list.add(getMatches(i));
		}
		return list;
	}

	private List<MatchType> getMatches(final int index) {
		List<MatchType> list = new ArrayList<>();
		// entity
		list.addAll(new StringComparisonMatch(this.relatedCharacteristics).getMatches());

		// contexts
		final Stream<Match> matches = MatchRegistry.getInstance()
				.getAllMatches(ContextHandler.getContexts(this.relatedCharacteristics, index)).stream();
		list.addAll(matches.map(Match::getMatches).flatMap(List::stream).collect(Collectors.toList()));

		return list;
	}

}
