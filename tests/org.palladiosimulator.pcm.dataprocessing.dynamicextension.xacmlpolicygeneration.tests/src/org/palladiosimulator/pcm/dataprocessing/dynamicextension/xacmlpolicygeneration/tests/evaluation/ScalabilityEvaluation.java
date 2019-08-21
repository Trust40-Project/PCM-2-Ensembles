package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests.evaluation;

import java.util.stream.Collectors;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.Characteristic;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ContextCharacteristic;
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

    private static final int TEN = 10;
    private static final int HUNDRED = 100;
    private static final int THOUSAND = 1000;
    private static final int TEN_THOUSAND = 10000;
    private static final int HUNDRED_THOUSAND = 100000;
    private static final int MILLION = 1000000;
    
    private static final int[] COPIES = {TEN, HUNDRED, THOUSAND, TEN_THOUSAND, HUNDRED_THOUSAND, MILLION};
    
    public static void main(String[] args) {
        final String ucTest = "UC-Test/";
        final var model = new TestScenario(ucTest, "", Decision.PERMIT).load();
        final Characteristic toCopy = ContextHandler.getCharacteristicsList(model.getRelatedCharacteristics().get(0)).get(0);
        final EList<Characteristic> list = new BasicEList<>(model.getRelatedCharacteristics().get(0).getCharacteristics().getOwnedCharacteristics());
        for (int i = 0; i < TEN; i++) {
            final Characteristic copied; //TODO copy
            //list.add(copied);
        }
        System.out.println(list.size());
        final var listFeature = 
                model.getRelatedCharacteristics().get(0).getCharacteristics()
                .eClass().getEAllStructuralFeatures()
                .stream().filter(x -> x.getName().equals("ownedCharacteristics"))
                .collect(Collectors.toList()).get(0);
        model.getRelatedCharacteristics().get(0).getCharacteristics().eSet(listFeature, new BasicEList<>());
        //model.getRelatedCharacteristics().get(0).getCharacteristics().eSet(listFeature, list);
        
        System.out.println(model.getRelatedCharacteristics().get(0).getCharacteristics().getOwnedCharacteristics().size());
    }

}
