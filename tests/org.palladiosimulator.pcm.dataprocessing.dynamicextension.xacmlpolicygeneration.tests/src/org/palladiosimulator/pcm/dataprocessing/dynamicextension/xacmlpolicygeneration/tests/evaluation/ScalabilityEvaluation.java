package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests.evaluation;

import java.util.stream.Collectors;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.Characteristic;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.ContextHandler;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests.scenarios.TestScenario;

import com.att.research.xacml.api.Decision;

/**
 * Class for evaluating scalability of the PCM-2-XACML trandformation.
 * 
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class ScalabilityEvaluation {

    private static final int ONE = 1;
    private static final int TEN = 10;
    private static final int HUNDRED = 100;
    private static final int THOUSAND = 1000;
    private static final int TEN_THOUSAND = 10000;
    private static final int HUNDRED_THOUSAND = 100000;
    private static final int MILLION = 1000000;
    private static final int TWO_MILLION = 2000000;

    private static final int[] COPY_NUMS = 
        {ONE, TEN, HUNDRED, THOUSAND, TEN_THOUSAND, HUNDRED_THOUSAND, MILLION, TWO_MILLION};

    public static void main(String[] args) {
        final String ucScale = "UC-Scale/";

        for (final int copyNum : COPY_NUMS) {
            final var model = new TestScenario(ucScale, "", Decision.PERMIT).load();
            final Characteristic toCopy = ContextHandler
                    .getCharacteristicsList(model.getRelatedCharacteristics().get(0)).get(0);
            final EList<Characteristic> list = new BasicEList<>();
            for (int i = 0; i < copyNum; i++) {
                final Characteristic copied = (Characteristic) new EcoreUtil.Copier().copy(toCopy);
                list.add(copied);
            }
            final var listFeature = model.getRelatedCharacteristics().get(0).getCharacteristics().eClass()
                    .getEAllStructuralFeatures().stream().filter(x -> x.getName().equals("ownedCharacteristics"))
                    .collect(Collectors.toList()).get(0);
            model.getRelatedCharacteristics().get(0).getCharacteristics().eSet(listFeature, list);

            System.out.println("context || num. = "
                    + model.getRelatedCharacteristics().get(0).getCharacteristics().getOwnedCharacteristics().size());
            final long before = System.currentTimeMillis();
            final var policyset = new ContextHandler(model).createPolicySet();
            final long after = System.currentTimeMillis();
            System.out.println("time consumed = " + (after - before) + "ms\n");
        }
    }

}
