package org.palladiosimulator.pcm.dataprocessing.dynamicextension.policygeneration.MainLoader;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.pcm.core.entity.NamedElement;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataSpecification;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataprocessingPackage;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.Enumeration;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.RelatedCharacteristics;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.DynamicSpecification;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.DynamicextensionPackage;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.Context;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ContextCharacteristic;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.IntegerThresholdContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.InternalStateContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.LocationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.OrganisationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.RoleContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ShiftContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ContextCharacteristicType;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.policygeneration.contexts.ContextHandler;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.policygeneration.util.ScalaHelper;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.helperattributes.Location;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.helperattributes.Role;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.subject.Organisation;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.helperattributes.RoleContainer;

public class MainClass {
	private static final String PATH_DYNAMIC = "/home/majuwa/git/Trust4.0/UC-Combined/uc-combined.dynamicextension";
	private static final String PATH_DATAPROCESSING = "/home/majuwa/git/Trust4.0/UC-Combined/uc-combined.dataprocessing";
	private static final String NAME_COMPANY = "Company";

	public MainClass() {
		// here start loading
		// Datap
		DataprocessingPackage.eINSTANCE.eClass();
		DynamicextensionPackage.eINSTANCE.eClass();
		var resourceSet = new ResourceSetImpl();
		var resourceRegistry = Resource.Factory.Registry.INSTANCE;
		final Map<String, Object> map = resourceRegistry.getExtensionToFactoryMap();
		map.put("*", new XMIResourceFactoryImpl());
		resourceSet.setResourceFactoryRegistry(resourceRegistry);
		var rootDynamic = loadDynmicModel(resourceSet);
		var rootData = loadDataSpecification(resourceSet);
		/*
		 * 
		 */
//		rootDynamic.getSubjectContainer().getSubject().stream().forEach(e -> System.out.println(e.getEntityName()));
//		rootData.getDataProcessingContainers().stream().forEach(e-> System.out.println(e.getEntityName()));
//		var generation = new ContextHandler(rootData, rootDynamic);
//		generation.createContext();

		try (var writer = new PrintWriter(new File("/home/majuwa/RunningExample.scala"), Charset.forName("UTF-8"))) {
			writer.println("package scenarios");
			writer.println("import tcof.{Component, _}");
			writer.println("class RunningExample extends Model {");
			writer.println(generateRoles(rootDynamic));
			writer.println(generateStatus(rootData));
			writer.println(generateCompany(rootDynamic));
			writer.println(generatePerson(rootDynamic));
			List<String> listEnsembleNames = generateRelatedContexts(rootData, writer);
			writer.println("  class System extends RootEnsemble {");
			writer.println(createRootEnsemble(listEnsembleNames));
			writer.println("  }");
			writer.println("  val rootEnsemble = root(new System)");
			writer.println("}");

			writeScenario(writer, rootDynamic.getSubjectContainer().getSubject().parallelStream()
					.filter(Organisation.class::isInstance).map(Organisation.class::cast).collect(Collectors.toList()));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String createRootEnsemble(List<String> listEnsembleNames) {
		var builder = new StringBuilder();
		for (String ensembleName : listEnsembleNames) {
			builder.append(ScalaHelper.KEYWORD_VAL);
			builder.append(" ");
			builder.append(ensembleName.toLowerCase());
			builder.append(" = ");
			builder.append("ensembles");
			builder.append("(\n");
			builder.append("new ");
			builder.append(ensembleName);
			builder.append("\n)\n");

		}
		return builder.toString();
	}

	private void writeScenario(PrintWriter writer, List<Organisation> organisations) {
		writer.println("object RunningExample {\n" + "  def main(args: Array[String]): Unit = {\n" + "    val "
				+ ScalaHelper.SCENARIO_NAME + " = new RunningExample\n" + "    scenario.init()");

		// Creation of companies
		var variableNames = new ArrayList<String>();
		for (Organisation o : organisations) {
			writer.println(createScenarioVariables(variableNames, "Company", o));
		}
		writer.println(variableNames.parallelStream()
				.collect(Collectors.joining(",", ScalaHelper.SCENARIO_NAME + ".components = List(", ")")));
		writer.println("        scenario.rootEnsemble.resolve()");
		writer.println("  }");
		writer.println("}");
	}

	private String createScenarioVariables(ArrayList<String> variableNames, String className, NamedElement o) {
		var variableName = className.toLowerCase() + o.getEntityName().replaceAll("\\s+", "");
		variableNames.add(variableName);
		var t = new StringBuilder();
		t.append(ScalaHelper.KEYWORD_VAL);
		t.append(" ");
		t.append(variableName);
		t.append(ScalaHelper.NEW_VARIABLE);
		t.append(String.format("%s(\"%s\")", className, o.getEntityName()));
		return t.toString();
	}

	private List<String> generateRelatedContexts(DataSpecification rootData, PrintWriter writer) throws IOException {
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
			if (listRoles.size() == 1) {
				writer.println(writeAllow(tmp, listRoles.get(0), listRoles.get(0)));
			}
			writer.append("\n}\n");

		});
		return list;
	}

	private Stream<Context> getContexts(RelatedCharacteristics e) {
		return e.getCharacteristics().getOwnedCharacteristics().stream().filter(ContextCharacteristic.class::isInstance)
				.map(ContextCharacteristic.class::cast).flatMap(i -> i.getContext().stream());
	}

	private String generateRoles(DynamicSpecification rootDynamic) {
		var t = new StringBuilder(200);
		t.append(rootDynamic.getHelperContainer().getRolecontainer().parallelStream()
				.flatMap(e -> e.getRole().parallelStream())
				.map(e -> String.format("\t case class %s(company: %s) extends Role",
						ScalaHelper.createIdentifier(e.getEntityName()), NAME_COMPANY))
				.collect(Collectors.joining("\n", "\t abstract class Role \n", "\n")));
		return t.toString();
	}

	private String generateStatus(DataSpecification rootData) {
		// first filter the relevant enumerations
		// (the ones which have a InternalStateContext)
		// then remove the duplicate -> distinct
		// then format String to correct Enumerations format
		// afterwards collect the String
		return rootData.getCharacteristicTypeContainers().stream().flatMap(e -> e.getCharacteristicTypes().stream())
				.filter(ContextCharacteristicType.class::isInstance).map(ContextCharacteristicType.class::cast)
				.flatMap(e -> e.getContext().stream()).filter(InternalStateContext.class::isInstance)
				.map(InternalStateContext.class::cast).map(e -> (Enumeration) e.getState().eContainer()).distinct()
				.flatMap(e -> e.getLiterals().parallelStream())
				.map(e -> String.format("\t case class %s() extends Status",
						ScalaHelper.createIdentifier(e.getEntityName() + e.getId())))
				.collect(Collectors.joining("\n", "abstract class Status\n", "\n"));
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

	private String generateCompany(DynamicSpecification rootDynamic) {
		var t = new StringBuilder(500);
		t.append("\n");
		t.append(String.format(
				"%s %s(val id: String, val parentCompany: Company = null, val status: Status = null) extends %s {",
				ScalaHelper.KEYWORD_CLASS, NAME_COMPANY, ScalaHelper.KEYWORD_COMPONENT));
		t.append("\n name(s\"Company $id\")");
		t.append("\n}");
		return t.toString();
	}

	private String generatePerson(DynamicSpecification rootDynamic) {
		var t = new StringBuilder(200);
		t.append("\n");
		t.append(String.format(
				"%s %s(val id: String, val company: Company, val location:String, val roles:Role*) extends %s {",
				ScalaHelper.KEYWORD_CLASS, "Person", ScalaHelper.KEYWORD_COMPONENT));
		t.append("\n name(s\"Person $id\")\n");
		t.append(" def hasRole(check: PartialFunction[Role, Boolean]): Boolean = {\n");
		t.append("  roles.exists(role => check.applyOrElse(role, (role: Role) => false))");
		t.append("\n}\n");
		t.append("}\n");
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

	private void writeInternalStateContext(StringBuilder writer, List<InternalStateContext> contexts) {

	}

	private void writeShiftContext(StringBuilder writer, List<ShiftContext> contexts) {

	}

	private void writeThresholdContext(StringBuilder writer, List<IntegerThresholdContext> contexts) {

	}

	private DynamicSpecification loadDynmicModel(ResourceSet resourceSet) {
		var resourceDynamic = loadResource(resourceSet, PATH_DYNAMIC);
		return (DynamicSpecification) resourceDynamic.getContents().get(0);
	}

	private DataSpecification loadDataSpecification(ResourceSet resourceSet) {
		var resourceData = loadResource(resourceSet, PATH_DATAPROCESSING);
		return (DataSpecification) resourceData.getContents().get(0);
	}

	private Resource loadResource(ResourceSet resourceSet, String path) {
		return resourceSet.getResource(URI.createFileURI(path), true);
	}

}
