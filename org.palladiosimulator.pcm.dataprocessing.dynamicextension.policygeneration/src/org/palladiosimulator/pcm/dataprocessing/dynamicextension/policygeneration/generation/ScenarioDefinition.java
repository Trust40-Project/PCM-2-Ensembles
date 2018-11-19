package org.palladiosimulator.pcm.dataprocessing.dynamicextension.policygeneration.generation;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.core.entity.NamedElement;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.policygeneration.util.ScalaHelper;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.subject.Organisation;

public class ScenarioDefinition {
	public void writeScenario(PrintWriter writer, List<Organisation> organisations) {
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
	
}
