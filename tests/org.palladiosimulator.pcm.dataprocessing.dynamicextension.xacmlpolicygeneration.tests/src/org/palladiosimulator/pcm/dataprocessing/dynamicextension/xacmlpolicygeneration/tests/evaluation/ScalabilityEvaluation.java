package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests.evaluation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataSpecification;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.Characteristic;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.Context;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ContextCharacteristic;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ShiftContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.ContextHandler;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.tests.scenarios.TestScenario;

import com.att.research.xacml.api.Decision;
import com.att.research.xacml.util.XACMLPolicyWriter;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;

/**
 * Class for evaluating scalability of the PCM-2-XACML transformation.
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
    private static final int FIVE_HUNDRED_THOUSAND = 500000;

    private static final String[] BASE_MODELS = { "UC-Scale/", "UC-Test/" };

    private static final int[] COPY_NUMS = { ONE, TEN, HUNDRED, THOUSAND, TEN_THOUSAND, HUNDRED_THOUSAND,
            FIVE_HUNDRED_THOUSAND };
    
    private static final Map<Pair<String, Integer>, List<Long>> RESULTS_OR = new HashMap<>();
    private static final Map<Pair<String, Integer>, List<Long>> RESULTS_AND = new HashMap<>();
    private static final int NUM_WARM_UP = 1;
    private static final int NUM_REPETITIONS = 10;
    
    static {
        for (final String uc : BASE_MODELS) {
            for (final int copyNum : COPY_NUMS) {
                final var pair = new Pair<>(uc, copyNum);
                RESULTS_OR.put(pair, new ArrayList<>());
                if (!uc.equals("UC-Test/")) {
                    RESULTS_AND.put(pair, new ArrayList<>());
                }
            }
        }
    }

    // printing needs at least 6.7GB free disk space
    private static final boolean IS_PRINTING = false;

    public static void main(String[] args) {
        for (int i = 0; i < NUM_WARM_UP + NUM_REPETITIONS; i++) {
            final int index = i - NUM_WARM_UP;
            final boolean isWarmUp = index < 0;
            System.out.println("index= " + index + "\nOR\n---------------------------------------------------------------\n");
            test(true, isWarmUp);
            System.out.println("AND\n---------------------------------------------------------------\n");
            test(false, isWarmUp);
        }
        System.out.println("\n\n");
        for (final String uc : BASE_MODELS) {
            for (final int copyNum : COPY_NUMS) {
                final var pair = new Pair<>(uc, copyNum);
                final var orList = RESULTS_OR.get(pair);
                System.out.println(pair + " OR: average= " + orList.stream().mapToLong(x -> x).average() + " | " + orList);
                if (!uc.equals("UC-Test/")) {
                    final var andList = RESULTS_AND.get(pair);
                    System.out.println(pair + " AND: average= " + andList.stream().mapToLong(x -> x).average() + " | " + andList);
                }
            }
        }
    }

    private static final void test(final boolean isOr, final boolean isWarmUp) {
        for (final String uc : BASE_MODELS) {
            System.out.println(uc + "\n");
            for (final int copyNum : COPY_NUMS) {
                final var model = new TestScenario(uc, "", Decision.PERMIT).load();
                setCharacterisicList(model, copyNum, isOr);

                final int num = isOr
                        ? model.getRelatedCharacteristics().get(0).getCharacteristics().getOwnedCharacteristics().size()
                        : ContextHandler.getCharacteristicsList(model.getRelatedCharacteristics().get(0)).get(0)
                                .getContext().size();
                System.out.println("context " + (isOr ? "||" : "&&") + " num. = " + num);
                final long before = System.currentTimeMillis();
                final var policySet = new ContextHandler("scalability:eval:" + uc.substring(0, uc.length() - 1) + num + isOr
                                            , model).createPolicySet();
                final long after = System.currentTimeMillis();
                final long time = after - before;
                System.out.println("time consumed = " + time + "ms\n");
                print(uc.substring(0, uc.length() - 1), policySet, isOr, copyNum);
                if (!isWarmUp) {
                    if (isOr) {
                        RESULTS_OR.get(new Pair<>(uc, copyNum)).add(time);
                    } else {
                        RESULTS_AND.get(new Pair<>(uc, copyNum)).add(time);
                    }
                }
            }
            System.out.println("---------------------------------------------------------------\n");
            if (!isOr) {
                // TODO change to continue and other condition in if, if other base-models are
                // checked
                // break in AND test because tests resulting from UC-Test and UC-Scale are
                // equivalent
                break;
            }
        }
    }

    private static final void setCharacterisicList(final DataSpecification model, final int copyNum,
            final boolean isOr) {
        if (isOr) {
            // OR
            final ContextCharacteristic toCopy = ContextHandler
                    .getCharacteristicsList(model.getRelatedCharacteristics().get(0)).get(0);
            final EList<Characteristic> list = new BasicEList<>();
            for (int i = 0; i < copyNum; i++) {
                final Characteristic copied = (Characteristic) new EcoreUtil.Copier().copy(toCopy);
                final var contextListFeature = getFeature(copied, "context");
                copied.eSet(contextListFeature, toCopy.getContext()); // list is not copied, so it
                                                                      // is set here
                list.add(copied);
            }
            final var listFeature = getFeature(model.getRelatedCharacteristics().get(0).getCharacteristics(),
                    "ownedCharacteristics");
            model.getRelatedCharacteristics().get(0).getCharacteristics().eSet(listFeature, list);
        } else {
            // AND
            final ShiftContext toCopy = (ShiftContext) ContextHandler
                    .getCharacteristicsList(model.getRelatedCharacteristics().get(0)).get(0).getContext().get(0);
            final EList<Context> list = new BasicEList<>();
            for (int i = 0; i < copyNum; i++) {
                final ShiftContext copied = (ShiftContext) new EcoreUtil.Copier().copy(toCopy);
                copied.setShift(toCopy.getShift()); // shift is not copied, so
                                                    // it is set here
                list.add(copied);
            }
            final var listFeature = getFeature(model.getRelatedCharacteristics().get(0).getCharacteristics()
                    .getOwnedCharacteristics().get(0), "context");
            model.getRelatedCharacteristics().get(0).getCharacteristics().getOwnedCharacteristics().get(0)
                    .eSet(listFeature, list);
        }
    }

    private static EStructuralFeature getFeature(final EObject object, final String featureName) {
        return object.eClass().getEAllStructuralFeatures().stream().filter(x -> x.getName().equals(featureName))
                .collect(Collectors.toList()).get(0);
    }

    private static void print(final String name, final PolicySetType policySet, final boolean isOr, final int copyNum) {
        if (IS_PRINTING) {
            final String filename = TestScenario.PATH_OUT_DIR + name + (isOr ? "OR" : "AND") + copyNum + ".xml";
            System.out.println("printing to " + filename + " ...");
            XACMLPolicyWriter.writePolicyFile(Path.of(filename), policySet);
            System.out.println("printing done.");
        }
    }
}
