package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.matches;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.RelatedCharacteristics;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.ContextHandler;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.Extractor;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;

/**
 * Extracts the matches from the context information of a related characteristics.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class MatchExtractor extends Extractor<List<MatchType>> {

    /**
     * Creates a new match extractor.
     * 
     * @param relatedCharacteristics
     *            - the characteristics
     */
    public MatchExtractor(final RelatedCharacteristics relatedCharacteristics) {
        super(relatedCharacteristics);
    }

    @Override
    protected List<MatchType> extractOneElement(final int index) {
        final List<MatchType> list = new ArrayList<>();
        // entity
        list.addAll(new StringComparisonMatch(getRelatedCharacteristics()).getMatches());

        // contexts
        final Stream<Match> matches = MatchRegistry.getInstance()
                .getAll(ContextHandler.getContexts(getRelatedCharacteristics(), index)).stream();
        // adding all matches representing the different contexts to the match list
        list.addAll(matches.map(Match::getMatches).flatMap(List::stream).collect(Collectors.toList()));

        return list;
    }

}
