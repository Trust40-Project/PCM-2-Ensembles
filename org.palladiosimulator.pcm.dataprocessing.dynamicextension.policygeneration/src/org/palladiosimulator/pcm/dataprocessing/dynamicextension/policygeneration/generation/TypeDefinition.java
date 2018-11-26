package org.palladiosimulator.pcm.dataprocessing.dynamicextension.policygeneration.generation;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataSpecification;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.Enumeration;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.DynamicSpecification;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ContextCharacteristicType;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.InternalStateContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.policygeneration.util.ScalaHelper;

/**
 * Generation of the type defintion of the ensembles
 * 
 * @author majuwa
 *
 */
public class TypeDefinition {
	private static final String NAME_COMPANY = "Company";

	public TypeDefinition() {

	}

	public void createRootEnsemble(PrintWriter writer, List<String> listEnsembleNames) {
		writer.println("  class System extends RootEnsemble {");
		writer.println(generateRootEnsemble(listEnsembleNames));
		writer.println("  }");
		writer.println("  val rootEnsemble = root(new System)");
		writer.println("}");
	}

	/**
	 * @param rootDynamic
	 * @param rootData
	 * @param writer
	 * @throws IOException
	 */
	public void createTypes(DynamicSpecification rootDynamic, DataSpecification rootData, PrintWriter writer)
			throws IOException {
		writer.println("package scenarios");
		writer.println("import tcof.{Component, _}");
		writer.println("class RunningExample extends Model {");
		writer.println(generateRoles(rootDynamic));
		writer.println(generateStatus(rootData));
		writer.println(generateCompany(rootDynamic));
		writer.println(generatePerson(rootDynamic));
		writer.println(generateFile());
		writer.println(generateShift());
	}

	private String generateRootEnsemble(List<String> listEnsembleNames) {
		var builder = new StringBuilder();
		for (String ensembleName : listEnsembleNames) {
			builder.append(ScalaHelper.KEYWORD_VAL);
			builder.append(" ");
			if (ensembleName.endsWith("-S"))
				builder.append(ensembleName.toLowerCase().substring(0, ensembleName.length() - 2));
			else
				builder.append(ensembleName.toLowerCase());
			builder.append(" = ");
			builder.append("ensembles");
			builder.append("(\n");
			if (ensembleName.endsWith("-S")) {
				builder.append("components.collect{case shift: Shift => shift}.map(");
				builder.append("new ");
				builder.append(ensembleName.substring(0, ensembleName.length() - 2));
				builder.append("(_))\n)\n");
			} else {
				builder.append("new ");
				builder.append(ensembleName);
				builder.append("\n)\n");
			}

		}
		return builder.toString();
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

	private String generateFile() {
		return "class File(val level: PrivacyLevel) extends  Component";
	}

	private String generatePerson(DynamicSpecification rootDynamic) {
		var t = new StringBuilder(200);
		t.append("\n");
		t.append(String.format(
				"%s %s(val id: String, val company: Company, val location:String, val shift: Shift, val roles:Role*) extends %s {",
				ScalaHelper.KEYWORD_CLASS, "Person", ScalaHelper.KEYWORD_COMPONENT));
		t.append("\n name(s\"Person $id\")\n");
		// not necessary for the current stand of the implementation of contexts
//		t.append(" def hasRole(check: PartialFunction[Role, Boolean]): Boolean = {\n");
//		t.append("  roles.exists(role => check.applyOrElse(role, (role: Role) => false))");
//		t.append("\n}\n");
		t.append("}\n");
		return t.toString();
	}

	private String generateRoles(DynamicSpecification rootDynamic) {
		var t = new StringBuilder(200);
		t.append(rootDynamic.getHelperContainer().getRolecontainer().parallelStream()
				.flatMap(e -> e.getRole().parallelStream())
				.map(e -> String.format("\t case class %s() extends Role",
						ScalaHelper.createIdentifier(e.getEntityName())))
				.collect(Collectors.joining("\n", "\t abstract class Role \n", "\n")));
		return t.toString();
	}

	private String generateShift() {
		return "class Shift(val nameNew: String) extends Component{name(s\"$nameNew\")}";
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
}
