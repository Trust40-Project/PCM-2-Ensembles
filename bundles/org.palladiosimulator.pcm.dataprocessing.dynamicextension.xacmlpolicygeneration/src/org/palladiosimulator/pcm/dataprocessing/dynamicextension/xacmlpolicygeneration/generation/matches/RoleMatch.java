package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.matches;

import java.util.Objects;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.RoleContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.helperattributes.Role;

/**
 * Represents a role match.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class RoleMatch extends RegexMatchingMatch {
    static {
        MatchRegistry.getInstance().put(RoleContext.class, RoleMatch.class);
    }
    
    private static final String CONTEXT_ROLE = "context:role";

    /**
     * Creates a new RoleMatch with a role context and all its sub-roles.
     * 
     * @param context
     *            - a role context
     */
    public RoleMatch(final RoleContext context) {
        super(ID_CATEGORY_SUBJECT, CONTEXT_ROLE, createRegexRole(Objects.requireNonNull(context).getRole()).toString());
    }

    /**
     * Creates a regex for the given role. All contained roles are added to the given role's name.
     * 
     * @param role
     *            - the given role
     * @return a regex for the given role
     */
    private static StringBuilder createRegexRole(final Role role) {
        Objects.requireNonNull(role);
        final StringBuilder regex = new StringBuilder("(");
        regex.append(toRegex(role.getEntityName())).append(")");

        for (final Role subRole : role.getSubordinateroles()) {
            regex.append("|").append(createRegexRole(subRole));
        }

        return regex;
    }
}
