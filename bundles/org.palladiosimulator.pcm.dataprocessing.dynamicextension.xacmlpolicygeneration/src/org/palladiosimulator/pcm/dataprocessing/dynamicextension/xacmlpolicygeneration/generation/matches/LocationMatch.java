package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.matches;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.LocationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.helperattributes.Location;

/**
 * Represents a location match.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class LocationMatch extends RegexMatchingMatch {
    static {
        MatchRegistry.getInstance().put(LocationContext.class, LocationMatch.class);
    }
    
    private static final String CONTEXT_LOCATION = "context:location";

    /**
     * Creates a new LocationMatch with a location context and all its sub-locations.
     * 
     * @param context
     *            - a location context
     */
    public LocationMatch(final LocationContext context) {
        super(ID_CATEGORY_SUBJECT, CONTEXT_LOCATION, createRegexLocation(context.getCurrentLocation()).toString());
    }

    /**
     * Creates a regex for the given location.
     * 
     * @param location
     *            - the given location
     * @return a regex for the given location
     */
    private static StringBuilder createRegexLocation(final Location location) {
        final StringBuilder regex = new StringBuilder("(");
        regex.append(toRegex(location.getEntityName())).append(")");

        for (final Location subLocation : location.getIncludes()) {
            regex.append("|").append(createRegexLocation(subLocation));
        }

        return regex;
    }
}
