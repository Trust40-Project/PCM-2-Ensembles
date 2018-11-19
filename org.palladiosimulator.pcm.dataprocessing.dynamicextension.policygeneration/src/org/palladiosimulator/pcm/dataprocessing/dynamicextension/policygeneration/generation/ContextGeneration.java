package org.palladiosimulator.pcm.dataprocessing.dynamicextension.policygeneration.generation;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataSpecification;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.RelatedCharacteristics;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.DynamicSpecification;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.Context;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ContextCharacteristic;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.IntegerThresholdContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.InternalStateContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.LocationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.OrganisationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.PrivacyLevelContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.RoleContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ShiftContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.policygeneration.util.ScalaHelper;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.helperattributes.Location;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.helperattributes.Role;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.helperattributes.RoleContainer;

public class ContextGeneration {
	private DataSpecification dataContainer;
	private DynamicSpecification dynamicContainer;

	public ContextGeneration(DataSpecification dataContainer, DynamicSpecification dynamicContainer) {
		this.dataContainer = dataContainer;
		this.dynamicContainer = dynamicContainer;
		dataContainer.getCharacteristicTypeContainers();
	}

	public List<String> generateRelatedContexts(DataSpecification rootData, PrintWriter writer) throws IOException {
		var list = new ArrayList<String>(rootData.getRelatedCharacteristics().size());
		rootData.getRelatedCharacteristics().stream().forEach(e -> {
			writer.append("class ");
			String tmp = ScalaHelper
					.createIdentifier(e.getRelatedEntity().getEntityName() + e.getRelatedEntity().getId());
			writer.append(tmp);
			list.add(tmp);
			writer.append("() ");
			writer.append(ScalaHelper.KEYWORD_EXTENDS);
			writer.append(" ");
			writer.append(ScalaHelper.KEYWORD_ENSEMBLE);
			writer.append("{\n");
			var listRoles = new ArrayList<String>();
			List<InternalStateContext> listStates = getContexts(e).filter(InternalStateContext.class::isInstance)
					.map(InternalStateContext.class::cast).collect(Collectors.toList());
			List<OrganisationContext> listOrganisationContext = getContexts(e)
					.filter(OrganisationContext.class::isInstance).map(OrganisationContext.class::cast)
					.collect(Collectors.toList());
			if (!listOrganisationContext.isEmpty()) {
				writeOrganisationContext(writer, listOrganisationContext, listStates);
				listRoles.add("companies");
			}
			List<LocationContext> listLocationContext = getContexts(e).filter(LocationContext.class::isInstance)
					.map(LocationContext.class::cast).collect(Collectors.toList());
			List<RoleContext> listRolesContext = getContexts(e).filter(RoleContext.class::isInstance)
					.map(RoleContext.class::cast).collect(Collectors.toList());
			if (!listLocationContext.isEmpty() || !listRolesContext.isEmpty())
				writePersonalContext(writer, listLocationContext, listRolesContext);
			List<PrivacyLevelContext> listPrivacyContext = getContexts(e).filter(PrivacyLevelContext.class::isInstance)
					.map(PrivacyLevelContext.class::cast).collect(Collectors.toList());
			if (!listPrivacyContext.isEmpty())
				writePrivacyContext(writer, listPrivacyContext);
			if (listRoles.size() == 1) {
				writer.println(writeAllow(tmp, listRoles.get(0), listRoles.get(0)));
			}
			writer.append("\n}\n");

		});
		return list;
	}

	private void writePrivacyContext(PrintWriter writer, List<PrivacyLevelContext> listPrivacyContext) {
		writer.println(listPrivacyContext.stream()
				.map(e -> String.format("x.level == PrivacyLevel.%s", e.getLevel().getEntityName()))
				.collect(Collectors.joining(" || ",
						"val levels = role(\"levels\", components.select[File].filter ( x => (", ")))")));

	}

	private Stream<Context> getContexts(RelatedCharacteristics e) {
		return e.getCharacteristics().getOwnedCharacteristics().stream().filter(ContextCharacteristic.class::isInstance)
				.map(ContextCharacteristic.class::cast).flatMap(i -> i.getContext().stream());
	}

	private String writeAllow(String nameAction, String... variableNames) {
		var t = new StringBuilder(variableNames.length * 20);
		t.append("allow(");
		t.append(Arrays.stream(variableNames).collect(Collectors.joining(",")));
		t.append(",\"");
		t.append(nameAction);
		t.append("\")");
		return t.toString();
	}

	private void writeOrganisationContext(PrintWriter printer, List<OrganisationContext> contexts,
			List<InternalStateContext> listStates) {
		var writer = new StringBuilder();
		writer.append("\n");
		writer.append("val companies = role(\"companies\",components.select[Company].filter( x => (");
		for (OrganisationContext context : contexts) {
			writer.append("(x.name == ");
			writer.append("\"");
			writer.append("Company ");
			writer.append(context.getOrganisation().getEntityName());
			writer.append("\"");
			if (!listStates.isEmpty()) {
				writer.append(listStates.stream().filter(e -> e.getSubject().equals(context.getOrganisation()))
						.map(e -> String.format("x.status.isInstanceOf[%s]",
								ScalaHelper.createIdentifier(e.getState().getEntityName() + e.getState().getId())))
						.collect(Collectors.joining(" && ", " && ", "")));
			}
			writer.append(" )|| ");
		}
		writer.delete(writer.length() - 3, writer.length());
		writer.append(")))");
		printer.println(writer.toString());

	}

	private void writePersonalContext(PrintWriter printer, List<LocationContext> locationContexts,
			List<RoleContext> roleContexts) {
		var s = new StringBuilder();
		s.append("\n");
		s.append("val persons = role(\"persons\",components.select[Person].filter( x => ((");
		if (!locationContexts.isEmpty()) {
			for (LocationContext context : locationContexts) {
				generateLocation(s, context.getCurrentLocation());
				List<Location> list = context.getCurrentLocation().getIncludes().stream()
						.flatMap(e -> e.getIncludes().stream()).collect(Collectors.toList());
				for (Location location : list) {
					generateLocation(s, location);
				}
			}
			s.delete(s.length() - 4, s.length());
			if (!roleContexts.isEmpty())
				s.append(") && (");
		}
		if (!roleContexts.isEmpty()) {

			for (RoleContext context : roleContexts) {
				generateRoleCheck(s, context.getRole());
				List<Role> list = context.getRole().getSubordinateroles().stream()
						.flatMap(e -> e.getSubordinateroles().stream()).collect(Collectors.toList());
				for (Role role : list) {
					generateRoleCheck(s, role);
				}
			}
			s.delete(s.length() - 4, s.length());
		}
		s.append("))))");
		printer.println(s.toString());
	}

	private void generateRoleCheck(StringBuilder s, Role role) {
		s.append("x.hasRole{\n\t case ");
		s.append(ScalaHelper.createIdentifier(role.getEntityName()));
		s.append("(organisation) => organisation.name == \"Company ");
		s.append(((RoleContainer) role.eContainer()).getOrganisation().getEntityName());
		s.append("\"} || ");
	}

	private void generateLocation(StringBuilder s, Location location) {
		s.append("x.location == ");
		s.append("\"");
		s.append(location.getEntityName());
		s.append("\"");
		s.append(" || ");
	}

	private void writeShiftContext(StringBuilder writer, List<ShiftContext> contexts) {

	}

	private void writeThresholdContext(StringBuilder writer, List<IntegerThresholdContext> contexts) {

	}

	/**
	 * @return the dataContainer
	 */
	public final DataSpecification getDataContainer() {
		return dataContainer;
	}

	/**
	 * @return the dynamicContainer
	 */
	public final DynamicSpecification getDynamicContainer() {
		return dynamicContainer;
	}

}
