package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.matches;

import java.util.Objects;

import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.EnumCharacteristicLiteral;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.PrivacyLevelContext;

/**
 * Represents a privacylevel match.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class PrivacylevelMatch extends RegexMatchingMatch {
    static {
        MatchRegistry.getInstance().put(PrivacyLevelContext.class, PrivacylevelMatch.class);
    }
    
    private static final String CONTEXT_PRIVACY_LEVEL = "context:privacylevel";

    /**
     * Creates a new PrivacylevelMatch for a privacy level and its contained privacy level.
     * 
     * @param context
     *            - the privacy level context representing the minimal privacy level
     */
    public PrivacylevelMatch(final PrivacyLevelContext context) {
        super(ID_CATEGORY_RESOURCE, CONTEXT_PRIVACY_LEVEL, 
                createRegexPrivacyLevel(Objects.requireNonNull(context).getLevel()));
    }

    /**
     * Creates a regex for the given privacylevel. All higher restriction levels are added to the
     * given privacylevel. Only if the level is "PUBLIC", "UNDEFINED" is also allowed. If the level
     * is "UNDEFINED", only "UNDEFINED" is allowed.
     * 
     * @param level
     *            - the given privacylevel
     * @return a regex for the given privacylevel
     */
    private static String createRegexPrivacyLevel(final EnumCharacteristicLiteral level) {
        Objects.requireNonNull(level);
        final String onlyThisLevel = "(" + level.getEntityName() + ")";
        switch (level.getEntityName()) {
        case "SECRET":
            return onlyThisLevel + "|(" + "RESTRICTED" + ")|(" + "PUBLIC" + ")|(" + "UNDEFINED" + ")";
        case "RESTRICTED":
            return onlyThisLevel + "|(" + "PUBLIC" + ")";
        default:
            return onlyThisLevel;
        }
    }
}
