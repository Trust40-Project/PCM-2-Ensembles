package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.matches;

import java.util.List;
import java.util.regex.Pattern;

import com.att.research.xacml.api.XACML3;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;

/**
 * Represents a match which uses regex matching.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public abstract class RegexMatchingMatch extends StringComparisonMatch {
    /**
     * Constructor for the regex matching match.
     * 
     * @param categoryId - the category id
     * @param contextId - the context id
     * @param regex - the regular expression
     */
    protected RegexMatchingMatch(final String categoryId, final String contextId, final String regex) {
        super(categoryId, contextId, regex);
    }

    /**
     * Quotes the given literal.
     * 
     * @param literal
     *            - the given literal
     * @return a regex quote of the literal
     */
    protected static String toRegex(final String literal) {
        return Pattern.quote(literal);
    }

    @Override
    public List<MatchType> getMatches() {
        final List<MatchType> matchList = super.getMatches();
        matchList.get(0).setMatchId(XACML3.ID_FUNCTION_STRING_REGEXP_MATCH.stringValue());
        return matchList;
    }
}
