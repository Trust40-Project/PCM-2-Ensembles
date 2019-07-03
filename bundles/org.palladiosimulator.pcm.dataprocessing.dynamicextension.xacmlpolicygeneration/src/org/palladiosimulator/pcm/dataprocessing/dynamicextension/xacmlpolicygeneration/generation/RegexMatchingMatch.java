package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation;

import java.util.List;
import java.util.regex.Pattern;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.LocationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.OrganisationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.RoleContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.helperattributes.Location;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.helperattributes.Role;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.subject.Organisation;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.subject.Subject;

import com.att.research.xacml.api.XACML3;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;

//TODO frage: regex matching mit oder ok?, einfacher, da domain matching erst namensaenderung erfordern wuerde

public class RegexMatchingMatch extends StringComparisonMatch {
	private static final String CONTEXT_LOCATION = "context:location";
	private static final String CONTEXT_ROLE = "context:role";
	private static final String CONTEXT_ORGANISATION = "context:organisation";
	
	public RegexMatchingMatch(final LocationContext context) {
		super(CONTEXT_LOCATION, createRegexLocation(context.getCurrentLocation()).toString());
	}
	
	public RegexMatchingMatch(final RoleContext context) {
		super(CONTEXT_ROLE, createRegexRole(context.getRole()).toString());
	}
	
	public RegexMatchingMatch(final OrganisationContext context) {
		super(CONTEXT_ORGANISATION, createRegexOrganisation(context.getOrganisation()).toString());
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
			if (ownedSubject.getClass().equals(Organisation.class)) {
				regex.append("|").append(createRegexOrganisation((Organisation) ownedSubject));
			} else {
				regex.append("|(").append(toRegex(ownedSubject.getEntityName())).append(")");
			}
		}
		
		return regex;
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
