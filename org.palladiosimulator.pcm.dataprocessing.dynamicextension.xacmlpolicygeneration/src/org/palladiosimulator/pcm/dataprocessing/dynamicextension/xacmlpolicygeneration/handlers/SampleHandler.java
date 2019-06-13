package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.ContextHandler;

public class SampleHandler extends AbstractHandler {
	private static final String PATH_DYNAMIC = "/home/majuwa/git/Trust4.0/UC-Shift/uc-combined.dynamicextension";
	private static final String PATH_DATA = "/home/majuwa/git/Trust4.0/UC-Shift/uc-combined.dataprocessing";
//	private static final String PATH_DYNAMIC = "/home/majuwa/git/Trust4.0/UC3/uc3.dynamicextension";
//	private static final String PATH_DATA = "/home/majuwa/git/Trust4.0/UC3/uc3.dataprocessing";
//	private static final String PATH_DYNAMIC = "/home/majuwa/git/Trust4.0/UC-Shift/uc-combined.dynamicextension";
//	private static final String PATH_DATA = "/home/majuwa/git/Trust4.0/UC-Shift/uc-combined.dataprocessing";
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		var x = new ContextHandler(PATH_DYNAMIC, PATH_DATA);
		x.createContext();
		return null;
	}
}
