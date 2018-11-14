package org.palladiosimulator.pcm.dataprocessing.dynamicextension.policygenerationMainLoader;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.pcm.core.entity.NamedElement;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataSpecification;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataprocessingPackage;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.DynamicSpecification;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.DynamicextensionPackage;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ContextCharacteristic;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.OrganisationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.RoleContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.policygeneration.contexts.ContextHandler;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.policygeneration.util.ScalaHelper;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.subject.Organisation;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.subject.User;

public class MainClass {
	private static final String PATH_DYNAMIC = "/home/majuwa/git/Trust4.0/UC3/uc3.dynamicextension";
	private static final String PATH_DATAPROCESSING = "/home/majuwa/git/Trust4.0/UC3/uc3.dataprocessing";
	private static final String NAME_COMPANY = "Company";

	public MainClass() {
		// here start loading
		// Datap
		DataprocessingPackage.eINSTANCE.eClass();
		DynamicextensionPackage.eINSTANCE.eClass();
		var resourceSet = new ResourceSetImpl();
		var resourceRegistry = Resource.Factory.Registry.INSTANCE;
		final Map<String, Object> map = resourceRegistry.getExtensionToFactoryMap();
		map.put("*", new XMIResourceFactoryImpl());
		resourceSet.setResourceFactoryRegistry(resourceRegistry);
		var rootDynamic = loadDynmicModel(resourceSet);
		var rootData = loadDataSpecification(resourceSet);
//		rootDynamic.getSubjectContainer().getSubject().stream().forEach(e -> System.out.println(e.getEntityName()));
//		rootData.getDataProcessingContainers().stream().forEach(e-> System.out.println(e.getEntityName()));
		var generation = new ContextHandler(rootData, rootDynamic);
		generation.createContext();
		var t = new StringBuilder();
		t.append("\t abstract class Role \n");
		t.append(rootDynamic.getHelperContainer().getRolecontainer().parallelStream()
				.flatMap(e -> e.getRole().parallelStream()).map(NamedElement::getEntityName).collect(Collectors
						.joining("\n", "\t case class ", String.format("(company: %s) extends Role", NAME_COMPANY))));
		if (rootDynamic.getSubjectContainer().getSubject().parallelStream().anyMatch(Organisation.class::isInstance)) {
			t.append("\n");
			t.append(String.format("%s %s(val name: String, val parentCompany: Company = null) extends %s {",
					ScalaHelper.KEYWORD_CLASS, NAME_COMPANY, ScalaHelper.KEYWORD_COMPONENT));
			t.append("\n name(s\"Company $name\"");
			t.append("\n}");
		}
		if (rootDynamic.getSubjectContainer().getSubject().parallelStream().anyMatch(User.class::isInstance)) {
			t.append("\n");
			t.append(String.format(
					"%s %s(val name: String, val company: Company, val location:String, val roles:Role*) extends %s {",
					ScalaHelper.KEYWORD_CLASS, "Person", ScalaHelper.KEYWORD_COMPONENT));
			t.append("\n name(s\"Person $name\"\n");
			t.append(" def hasRole(check: PartialFunction[Role, Boolean]): Boolean = {\n");
			t.append("  roles.exists(role => check.applyOrElse(role, (role: Role) => false))");
			t.append("\n}\n");
		}

		rootData.getRelatedCharacteristics().stream().forEach(e -> {
			t.append("class ");
			t.append(e.getRelatedEntity().getEntityName());
			t.append(e.getRelatedEntity().getId());
			t.append("() ");
			t.append(ScalaHelper.KEYWORD_EXTENDS);
			t.append(" ");
			t.append(ScalaHelper.KEYWORD_ENSEMBLE);
			t.append("{\n");
			writeOrganisationContext(t,e.getCharacteristics().getOwnedCharacteristics().stream()
					.filter(ContextCharacteristic.class::isInstance).map(ContextCharacteristic.class::cast).flatMap(i -> i.getContext().stream()).filter(OrganisationContext.class::isInstance)
					.map(OrganisationContext.class::cast).collect(Collectors.toList()));
			t.append("\n}");

		});

		System.out.println(t.toString());

	}

	private void writeOrganisationContext(StringBuilder writer, List<OrganisationContext> contexts) {
		writer.append("\n");
		writer.append("val companies = role(\"companies\",components.select[Company].filter( x => (");
		for (OrganisationContext context : contexts) {
			writer.append("x.name == ");
			writer.append("\"");
			writer.append(context.getEntityName());
			writer.append("\"");
			writer.append(" || ");
		}
		writer.delete(writer.length() - 4, writer.length());
		writer.append(")))");

	}
	private void writeLocationContext(StringBuilder writer, List<OrganisationContext> contexts) {
		writer.append("\n");
		writer.append("val location = role(\"companies\",components.select[Company].filter( x => (");
		for (OrganisationContext context : contexts) {
			writer.append("x.name == ");
			writer.append("\"");
			writer.append(context.getEntityName());
			writer.append("\"");
			writer.append(" || ");
		}
		writer.delete(writer.length() - 4, writer.length());
		writer.append(")))");
	}
	private void writeRoleContex(StringBuilder writer, List<RoleContext> contexts) {
		writer.append("\n");
		writer.append("val location = role(\"companies\",components.select[Company].filter( x => (");
		for (RoleContext context : contexts) {
			writer.append("x.name == ");
			writer.append("\"");
			writer.append(context.getEntityName());
			writer.append("\"");
			writer.append(" || ");
		}
		writer.delete(writer.length() - 4, writer.length());
		writer.append(")))");
	}
	

	private DynamicSpecification loadDynmicModel(ResourceSet resourceSet) {
		var resourceDynamic = loadResource(resourceSet, PATH_DYNAMIC);
		return (DynamicSpecification) resourceDynamic.getContents().get(0);
	}

	private DataSpecification loadDataSpecification(ResourceSet resourceSet) {
		var resourceData = loadResource(resourceSet, PATH_DATAPROCESSING);
		return (DataSpecification) resourceData.getContents().get(0);
	}

	private Resource loadResource(ResourceSet resourceSet, String path) {
		return resourceSet.getResource(URI.createFileURI(path), true);
	}

}
