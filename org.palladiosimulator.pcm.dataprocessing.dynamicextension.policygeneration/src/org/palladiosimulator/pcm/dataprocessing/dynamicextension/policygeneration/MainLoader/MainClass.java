package org.palladiosimulator.pcm.dataprocessing.dynamicextension.policygeneration.MainLoader;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.pcm.core.entity.NamedElement;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataSpecification;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataprocessingPackage;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.DynamicSpecification;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.DynamicextensionPackage;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ContextCharacteristic;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.IntegerThresholdContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.InternalStateContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.LocationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.OrganisationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.RoleContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ShiftContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.policygeneration.contexts.ContextHandler;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.policygeneration.util.ScalaHelper;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.helperattributes.Location;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.subject.Organisation;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.subject.User;

public class MainClass {
	private static final String PATH_DYNAMIC = "/home/majuwa/git/Trust4.0/UC3/uc3.dynamicextension";
	private static final String PATH_DATAPROCESSING = "/home/majuwa/git/Trust4.0/UC3/uc3.dataprocessing";
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
//		rootDynamic.getSubjectContainer().getSubject().stream().forEach(e -> System.out.println(e.getEntityName()));
//		rootData.getDataProcessingContainers().stream().forEach(e-> System.out.println(e.getEntityName()));
		var generation = new ContextHandler(rootData, rootDynamic);
		generation.createContext();

		try (var writer = new PrintWriter(new File("/home/majuwa/RunningExample.scala"), Charset.forName("UTF-8"))) {
			writer.println("package scenarios");
			writer.println("import tcof.{Component, _}");
			writer.println("class RunningExample extends Model {");
			writer.println(generateRoles(rootDynamic));
			writer.println(generatePerson(rootDynamic));
			List<String> listEnsembleNames = generateRelatedContexts(rootData, writer);
			writer.println("  class System extends RootEnsemble {");
			
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
		
		return builder.toString();
	}
	private void writeScenario(PrintWriter writer, List<Organisation> organisations) {
		writer.println("object RunningExample {\n" + "  def main(args: Array[String]): Unit = {\n" + "    val "
				+ ScalaHelper.SCENARIO_NAME + " = new RunningExample\n" + "    scenario.init()");
		
		// Creation of companies
		var variableNames = new ArrayList<String>();
		for (Organisation o : organisations) {
			writer.println(createScenarioVariables(variableNames,"Company", o));
		}
		writer.println(variableNames.parallelStream()
				.collect(Collectors.joining(",", ScalaHelper.SCENARIO_NAME + ".components = List(", ")")));
		writer.println("        scenario.rootEnsemble.resolve()");
		writer.println("  }");
		writer.println("}");
	}

	private String createScenarioVariables(ArrayList<String> variableNames,String className, NamedElement o) {
		var variableName = className.toLowerCase() +  o.getEntityName().replaceAll("\\s+", "");
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
			String tmp = e.getRelatedEntity().getEntityName() + e.getRelatedEntity().getId();
			writer.append(tmp);
			list.add(tmp);
			writer.append("() ");
			writer.append(ScalaHelper.KEYWORD_EXTENDS);
			writer.append(" ");
			writer.append(ScalaHelper.KEYWORD_ENSEMBLE);
			writer.append("{\n");
			List<OrganisationContext> listOrganisation = e.getCharacteristics().getOwnedCharacteristics().stream()
					.filter(ContextCharacteristic.class::isInstance).map(ContextCharacteristic.class::cast)
					.flatMap(i -> i.getContext().stream()).filter(OrganisationContext.class::isInstance)
					.map(OrganisationContext.class::cast).collect(Collectors.toList());
			if(!listOrganisation.isEmpty()) {
				writeOrganisationContext(writer,listOrganisation);
			}	
			writer.append("\n}\n");
			
		});
		return list;
	}

	private String generateRoles(DynamicSpecification rootDynamic) {
		var t = new StringBuilder(200);
		t.append("\t abstract class Role \n");
		t.append(rootDynamic.getHelperContainer().getRolecontainer().parallelStream()
				.flatMap(e -> e.getRole().parallelStream()).map(NamedElement::getEntityName).collect(Collectors
						.joining("\n", "\t case class ", String.format("(company: %s) extends Role", NAME_COMPANY))));
		if (rootDynamic.getSubjectContainer().getSubject().parallelStream().anyMatch(Organisation.class::isInstance)) {
			t.append("\n");
			t.append(String.format("%s %s(val id: String, val parentCompany: Company = null) extends %s {",
					ScalaHelper.KEYWORD_CLASS, NAME_COMPANY, ScalaHelper.KEYWORD_COMPONENT));
			t.append("\n name(s\"Company $id\")");
			t.append("\n}");
		}
		return t.toString();
	}

	private String generatePerson(DynamicSpecification rootDynamic) {
		var t = new StringBuilder(200);
		if (rootDynamic.getSubjectContainer().getSubject().parallelStream().anyMatch(User.class::isInstance)) {
			t.append("\n");
			t.append(String.format(
					"%s %s(val id: String, val company: Company, val location:String, val roles:Role*) extends %s {",
					ScalaHelper.KEYWORD_CLASS, "Person", ScalaHelper.KEYWORD_COMPONENT));
			t.append("\n name(s\"Person $id\")\n");
			t.append(" def hasRole(check: PartialFunction[Role, Boolean]): Boolean = {\n");
			t.append("  roles.exists(role => check.applyOrElse(role, (role: Role) => false))");
			t.append("\n}\n");
			t.append("}\n");
		}
		return t.toString();
	}

	private void writeOrganisationContext(PrintWriter printer, List<OrganisationContext> contexts) {
		var writer = new StringBuilder();
		writer.append("\n");
		writer.append("val companies = role(\"companies\",components.select[Company].filter( x => (");
		for (OrganisationContext context : contexts) {
			writer.append("x.name == ");
			writer.append("\"");
			writer.append("Company ");
			writer.append(context.getOrganisation().getEntityName());
			writer.append("\"");
			writer.append(" || ");
		}
		writer.delete(writer.length() - 4, writer.length());
		writer.append(")))");
		printer.append(writer.toString());

	}

	private void writeLocationContext(StringBuilder writer, List<LocationContext> contexts) {
		writer.append("\n");
		writer.append("val person = role(\"person\",components.select[Person].filter( x => (");
		for (LocationContext context : contexts) {
			for (Location location : context.getCurrentLocation().getIncludes()) {
				writer.append("x.name == ");
				writer.append("\"");
				writer.append(location.getEntityName());
				writer.append("\"");
				writer.append(" || ");
			}
		}
		writer.delete(writer.length() - 4, writer.length());
		writer.append(")))");
	}

	private void writeRoleContex(StringBuilder writer, List<RoleContext> contexts) {
		writer.append("\n");
		writer.append("val location = role(\"companies\",components.select[Person].filter( x => (");
		for (RoleContext context : contexts) {
			writer.append("x.name == ");
			writer.append("\"");
			writer.append(context.getEntityName());
			writer.append("\"");
			writer.append(" || ");
		}
		writer.delete(writer.length() - 4, writer.length());
		writer.append(")))");
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
