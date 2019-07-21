package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.matches;

import java.util.List;
import java.util.regex.Pattern;

import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.EnumCharacteristicLiteral;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.LocationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.OrganisationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.PrivacyLevelContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.RoleContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.helperattributes.Location;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.helperattributes.Role;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.subject.Organisation;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.subject.Subject;

import com.att.research.xacml.api.XACML3;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;

/**
 * Represents a match which uses regex matching.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class RegexMatchingMatch extends StringComparisonMatch {
	private static final String CONTEXT_LOCATION = "context:location";
	private static final String CONTEXT_ROLE = "context:role";
	private static final String CONTEXT_ORGANISATION = "context:organisation";
	private static final String CONTEXT_PRIVACY_LEVEL = "context:privacylevel";
	
	/**
	 * Creates a new RegexMatchingMatch with a location context and all its sub-locations.
	 * 
	 * @param context - a location context
	 */
	public RegexMatchingMatch(final LocationContext context) {
		super(ID_CATEGORY_SUBJECT, CONTEXT_LOCATION, createRegexLocation(context.getCurrentLocation()).toString());
	}
	
	/**
	 * Creates a new RegexMatchingMatch with a role context and all its sub-roles.
	 * 
	 * @param context - a role context
	 */
	public RegexMatchingMatch(final RoleContext context) {
		super(ID_CATEGORY_SUBJECT, CONTEXT_ROLE, createRegexRole(context.getRole()).toString());
	}
	
	/**
	 * Creates a new RegexMatchingMatch with an organisation context and all its sub-organisations.
	 * 
	 * @param context - an organisation context
	 */
	public RegexMatchingMatch(final OrganisationContext context) {
		super(ID_CATEGORY_SUBJECT, CONTEXT_ORGANISATION, createRegexOrganisation(context.getOrganisation()).toString());
	}
	
	/**
	 * Creates a new RegexMatchingMatch for a privacy level and its contained privacy level.
	 * 
	 * @param context - the privacy level context representing the minimal privacy level
	 */
	public RegexMatchingMatch(final PrivacyLevelContext context) {
		super(ID_CATEGORY_RESOURCE, CONTEXT_PRIVACY_LEVEL, createRegexPrivacyLevel(context.getLevel()));
	}
	
	private static StringBuilder createRegexLocation(final Location location) {
		final StringBuilder regex = new StringBuilder("(");
		regex.append(toRegex(location.getEntityName())).append(")");
		
		for (final Location subLocation : location.getIncludes()) {
			regex.append("|").append(createRegexLocation(subLocation));
		}
		
		return regex;
	}
	
	private static StringBuilder createRegexRole(final Role role) {
		final StringBuilder regex = new StringBuilder("(");
		regex.append(toRegex(role.getEntityName())).append(")");
		
		for (final Role subRole : role.getSubordinateroles()) {
			regex.append("|").append(createRegexRole(subRole));
		}
		
		return regex;
	}
	
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
	
	private static String createRegexPrivacyLevel(final EnumCharacteristicLiteral level) {
		final String onlyThisLevel = "(" + level.getEntityName() + ")";
		switch (level.getEntityName()) {
			case "PUBLIC": return onlyThisLevel + "|(" + "RESTRICTED" + ")|(" + "SECRET" + ")|(" + "UNDEFINED" + ")";
			case "RESTRICTED": return onlyThisLevel + "|(" + "SECRET" + ")";
			default: return onlyThisLevel;
		}
	}

	private static String toRegex(final String literal) {
		return Pattern.quote(literal);
	}
	
	@Override
	public List<MatchType> getMatches() {
		final List<MatchType> matchList = super.getMatches();
		matchList.get(0).setMatchId(XACML3.ID_FUNCTION_STRING_REGEXP_MATCH.stringValue());
		return matchList;
	}
}
