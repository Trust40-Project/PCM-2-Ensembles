package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.matches;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.OrganisationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.subject.Organisation;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.subject.Subject;

/**
 * Represents an organisation match.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class OrganisationMatch extends RegexMatchingMatch { 
    static {
        MatchRegistry.getInstance().put(OrganisationContext.class, OrganisationMatch.class);
    }
    
    private static final String CONTEXT_ORGANISATION = "context:organisation";

    /**
     * Creates a new OrganisationMatch with an organisation context and all its sub-organisations.
     * 
     * @param context
     *            - an organisation context
     */
    public OrganisationMatch(final OrganisationContext context) {
        super(ID_CATEGORY_SUBJECT, CONTEXT_ORGANISATION, createRegexOrganisation(context.getOrganisation()).toString());
    }

    /**
     * Creates a regex for the given organisation. Only contained organisations are added to the
     * given organisation's name.
     * 
     * @param organisation
     *            - the given organisation
     * @return a regex for the given organisation
     */
    private static StringBuilder createRegexOrganisation(final Organisation organisation) {
        final StringBuilder regex = new StringBuilder("(");
        regex.append(toRegex(organisation.getEntityName())).append(")");

        for (final Subject ownedSubject : organisation.getOwnedSubjects()) {
            if (Organisation.class.isInstance(ownedSubject)) {
                regex.append("|").append(createRegexOrganisation((Organisation) ownedSubject));
            }
        }

        return regex;
    }
}
