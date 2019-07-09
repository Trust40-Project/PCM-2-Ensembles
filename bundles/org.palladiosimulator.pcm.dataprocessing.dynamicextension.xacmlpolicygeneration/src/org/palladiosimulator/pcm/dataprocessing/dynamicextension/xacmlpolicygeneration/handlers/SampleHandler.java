package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.handlers;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.ContextHandler;

import com.att.research.xacml.api.Request;
import com.att.research.xacml.api.pdp.PDPEngine;
import com.att.research.xacml.api.pdp.PDPEngineFactory;
import com.att.research.xacml.api.pdp.PDPException;
import com.att.research.xacml.std.dom.DOMRequest;
import com.att.research.xacml.std.dom.DOMStructureException;
import com.att.research.xacml.util.FactoryException;

public class SampleHandler extends AbstractHandler {
	// SETTINGS ////////////////////////////////////////////////////////////////////////////////
	public static final String PATH_PREFIX = "/home/jojo/Schreibtisch/KIT/Bachelorarbeit/";
	private static final String PATH_INPUT_MODELS = "models/UseCasesTechnicalReport/";
	private static final String PATH_USECASE = "UC-Test/uc-test";
	
	private static final String PATH_TEST_REQUESTS_DIRECTORY = ""; //TODO
	private static final String FILENAME_TEST_REQUEST = "testTime.xml";
	
	////////////////////////////////////////////////////////////////////////////////////////////
	
	public static final String PATH_DYNAMIC = PATH_PREFIX + PATH_INPUT_MODELS + PATH_USECASE + ".dynamicextension";
	public static final String PATH_DATA = PATH_PREFIX + PATH_INPUT_MODELS + PATH_USECASE + ".dataprocessing";

	private static final String PATH_TEST_REQUEST = PATH_PREFIX + PATH_TEST_REQUESTS_DIRECTORY + FILENAME_TEST_REQUEST; 
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ContextHandler ch = new ContextHandler(PATH_DYNAMIC, PATH_DATA);
		try {
			ch.createPolicySet();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Request-Test
		try {
			testRequest(event);
		} catch (DOMStructureException e) {
			e.printStackTrace();
		} catch (FactoryException e) {
			e.printStackTrace();
		} catch (PDPException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void testRequest(ExecutionEvent event) throws DOMStructureException, PDPException, FactoryException, ExecutionException {
		Request request =  DOMRequest.load(new File(PATH_TEST_REQUEST));
		
		PDPEngine pdp = PDPEngineFactory.newInstance().newEngine();
		final var result = pdp.decide(request);
		//System.out.println(result);
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(
				window.getShell(),
				"xacml-policygeneration",
				result.toString());
	}
}
