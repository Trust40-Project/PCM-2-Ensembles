package org.palladiosimulator.pcm.confidentiality.context.xcamltransformation.policywriter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import com.sun.xml.bind.v2.ContextFactory;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObjectFactory;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;

/**
 * The class is responsible for writing of policies to output files
 * @author vladsolovyev
 * @version 1.0.0
 */

public class XACMLPolicyWriter {

    private static final Logger LOGGER = Logger.getLogger(XACMLPolicyWriter.class.getName());

    public static String writePolicyFile(Path outputFile, PolicySetType policySet) {
        LOGGER.info(String.format("Transformation output will be written to %s", outputFile.toAbsolutePath().toString()));
        String msg;
        try {
            ObjectFactory objectFactory = new ObjectFactory();
            JAXBElement<PolicySetType> policySetElement = objectFactory.createPolicySet(policySet);
            JAXBContext context = ContextFactory.createContext(new Class[] { PolicySetType.class }, null);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(policySetElement, outputFile.toFile());
            if (Files.exists(outputFile)) {
                msg = String.format("Output file %s could be written successfully", outputFile.toAbsolutePath().toString());
                LOGGER.info(msg);
            } else {
                msg = String.format("Output file %s could not be written", outputFile.toAbsolutePath().toString());
                LOGGER.warning(msg);
            }
        } catch (Exception e) {
            msg = String.format("Output file %s could not be written", outputFile.toAbsolutePath().toString());
            LOGGER.log(Level.SEVERE, msg, e);
        }
        return msg;
    }
}
