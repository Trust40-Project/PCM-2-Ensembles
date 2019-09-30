package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.matches;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.RelatedCharacteristics;
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
        super(Objects.requireNonNull(relatedCharacteristics));
    }
    
    @Override
    public List<List<MatchType>> extract() {
        final var extractionResult = super.extract();
        
        for (var list : extractionResult) {
            if (list.isEmpty()) {
                addActionNameMatch(list);
            }
        }
        
        if (extractionResult.isEmpty()) {
            extractionResult.add(addActionNameMatch(new ArrayList<MatchType>()));
        }
        
        return extractionResult;
    }

    @Override
    protected List<MatchType> extractOneElement(final int index) {
        final List<MatchType> list = new ArrayList<>();
        // entity
        addActionNameMatch(list);

        // contexts
        final Stream<Match> matches = MatchRegistry.getInstance()
                .getAll(getCharacteristicsList().get(index).getContext()).stream();
        // adding all matches representing the different contexts to the match list
        list.addAll(matches.map(Match::getMatches).flatMap(List::stream).collect(Collectors.toList()));

        return list;
    }

    /**
     * Adds the action name to the matches list.
     * 
     * @param list - the matches list
     * @return the matches list
     */
    private List<MatchType> addActionNameMatch(final List<MatchType> list) {
        list.addAll(new StringComparisonMatch(getRelatedCharacteristics()).getMatches());
        return list;
    }
}
