package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;


public class ProjectHelper {
	private final static String NAME_SBT = "build.sbt";
	/*
	 * Scala for ensembles
	 */
	private final static String SCALA_VERSION = "2.12.4";
	private final static String SCALA_NAME = "scala-reflect";
	private final static String SCALA_NAME_QUALIFIER = "org.scala-lang";
	/*
	 * Solver for ensembles
	 */
	private final static String SOLVER_VERSION = "4.0.0";
	private final static String SOLVER_NAME = "choco-solver";
	private final static String SOLVER_NAME_QUALIFIER = "org.choco-solver";
	/*
	 * Map for ensembles
	 */
	private final static String SCALA_MAP_VERSION = "1.0.0";
	private final static String SCALA_MAP_NAME = "scala-prioritymap";
	private final static String SCALA_MAP_QUALIFIER = "de.ummels";
	/*
	 * Math for ensembles
	 */
	private final static String APACHE_MATH_VERSION = "3.6.1";
	private final static String APACHE_MATH_NAME = "commons-math3";
	private final static String APACHE_MATH_QUALIFIER = "org.apache.commons";
	
	private final static String SEPARATOR = " % ";

	public static boolean buildProjectStructure(String path, String projectName) {
		try (var writer = new PrintWriter(new File(path + File.pathSeparator + NAME_SBT),
				Charset.forName("UTF-8"))) {
			/*
			 * write preamble 
			 */
			writer.println("name := "+ wrapQuotes(projectName));
			writer.println("version := " + wrapQuotes("1.0"));
			writer.println("scalaVersion := " + wrapQuotes(SCALA_VERSION));
			writer.println("libraryDependencies ++= Seq(");
			/*
			 * Write dependencies
			 */
			writeComma(writer, wrapQuotes(SCALA_NAME_QUALIFIER) + SEPARATOR + wrapQuotes(SCALA_NAME) + SEPARATOR + wrapQuotes(SCALA_VERSION));
			writeComma(writer, wrapQuotes(SOLVER_NAME_QUALIFIER) + SEPARATOR + wrapQuotes(SOLVER_NAME) + SEPARATOR + wrapQuotes(SOLVER_VERSION));
			writeComma(writer, wrapQuotes(SCALA_MAP_QUALIFIER) + SEPARATOR + wrapQuotes(SCALA_MAP_NAME) + SEPARATOR + wrapQuotes(SCALA_MAP_VERSION));
			writeComma(writer, wrapQuotes(APACHE_MATH_QUALIFIER) + SEPARATOR + wrapQuotes(APACHE_MATH_NAME) + SEPARATOR + wrapQuotes(APACHE_MATH_VERSION));
			
			writer.println("}");
			
			
			
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	private static String wrapQuotes(String s) {
		return "\"" + s + "\"";
	}
	private static void writeComma(PrintWriter writer, String s) throws IOException{
		writer.println(s + ",");
	}

}
