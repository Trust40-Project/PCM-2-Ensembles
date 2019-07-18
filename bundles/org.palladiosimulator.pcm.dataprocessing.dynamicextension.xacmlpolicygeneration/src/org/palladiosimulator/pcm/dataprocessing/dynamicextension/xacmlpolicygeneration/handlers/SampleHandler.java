package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.handlers;

import java.nio.file.Path;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.ContextHandler;

import com.att.research.xacml.util.XACMLPolicyWriter;

public class SampleHandler extends AbstractHandler {
	// SETTINGS ////////////////////////////////////////////////////////////////////////////////
	public static final String PATH_PREFIX = "/home/jojo/Schreibtisch/KIT/Bachelorarbeit/";
	private static final String PATH_INPUT_MODELS = "models/UseCasesTechnicalReport/";
	private static final String PATH_USECASE = "UC-Test/uc-test";
	
	private static final String FILENAME_OUTPUT_POLICYSET = "outSet.xml";
	////////////////////////////////////////////////////////////////////////////////////////////
	
	public static final String PATH_DYNAMIC = PATH_PREFIX + PATH_INPUT_MODELS + PATH_USECASE + ".dynamicextension";
	public static final String PATH_DATA = PATH_PREFIX + PATH_INPUT_MODELS + PATH_USECASE + ".dataprocessing";

	public static final String PATH_OUTPUT_POLICYSET = PATH_PREFIX + FILENAME_OUTPUT_POLICYSET;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// generate policy set
		ContextHandler ch = new ContextHandler(PATH_DYNAMIC, PATH_DATA);
		var policySet = ch.createPolicySet();
		
		// write policy set
		final Path filenamePolicySet = Path.of(PATH_OUTPUT_POLICYSET);
		final Path okPath = XACMLPolicyWriter.writePolicyFile(filenamePolicySet, policySet);
		
		// inform user
		final String error = okPath != null ? null : "an error uccured";
		final String result = okPath != null ? "policy set sucessfully written to " + okPath : error;
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(
				window.getShell(),
				"xacml-policygeneration" + (okPath != null ? "" : " ERROR"),
				result);
		
		return null;
	}
}
