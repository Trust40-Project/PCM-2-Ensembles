package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.util;

import org.eclipse.emf.codegen.util.CodeGenUtil;


/**
 * Helper Class for creating Scala 
 * @author majuwa
 *
 */
public class ScalaHelper {
	public final static String KEYWORD_CLASS = "class";
	public final static String KEYWORD_VAL = "val";
	public final static String KEYWORD_EXTENDS = "extends";
	public final static String KEYWORD_STRING = "String";
	public final static String KEYWORD_BOOLEAN = "Boolean";
	public final static String KEYWORD_LONG = "Long";
	public final static String KEYWORD_INTEGER = "Integer";
	public final static String KEYWORD_COMPONENT = "Component";
	public final static String KEYWORD_ENSEMBLE = "Ensemble";
	public final static String KEYWORD_CASE = "case";
	public final static String KEYWORD_MEMBERSHIP = "membership";
	public final static String KEYWORD_ALLOW = "allow";
	public final static String KEYWORD_ENSEMBLE_ROOT = "RootEnsemble";
	public final static String KEYWORD_DEF = "def";
	public final static String SCENARIO_NAME = "scenario";
	public final static String NEW_VARIABLE = " = new " + SCENARIO_NAME + ".";
	
	private ScalaHelper() {
		
	}
	/**
	 * Creates an correct Java Identifier
	 * @param s
	 * @return {@link String} with Java Identifier
	 */
	public static String createIdentifier(String s) {
		if(s== null)
			throw new NullPointerException("Tried to create identifier from null");
		return CodeGenUtil.validJavaIdentifier(s);
		
	}

}
