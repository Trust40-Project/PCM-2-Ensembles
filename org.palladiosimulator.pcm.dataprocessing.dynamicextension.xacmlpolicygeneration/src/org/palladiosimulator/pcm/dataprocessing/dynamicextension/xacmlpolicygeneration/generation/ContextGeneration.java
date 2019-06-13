package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.palladiosimulator.pcm.dataprocessing.dataprocessing.DataSpecification;
import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.RelatedCharacteristics;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.DynamicSpecification;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.Context;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ContextCharacteristic;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.InternalStateContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.LocationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.OrganisationContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.PrivacyLevelContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.RoleContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ShiftCheckContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.ShiftContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.util.ScalaHelper;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.helperattributes.Location;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.helperattributes.Role;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.subject.Organisation;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.util.subject.Resource;

public class ContextGeneration {
	private DataSpecification dataContainer;
	private DynamicSpecification dynamicContainer;

	public ContextGeneration(DataSpecification dataContainer, DynamicSpecification dynamicContainer) {
		this.dataContainer = dataContainer;
		this.dynamicContainer = dynamicContainer;
		dataContainer.getCharacteristicTypeContainers();
	}

	public Map<RelatedCharacteristics, Role> getRelatedRoleContexts(DataSpecification rootData) throws IOException {
		var map = new HashMap<RelatedCharacteristics, Role>();
		rootData.getRelatedCharacteristics().stream().forEach(e -> {
			//System.out.println(e.getEntityName());
			List<RoleContext> listRolesContext = getContexts(e).filter(RoleContext.class::isInstance)
					.map(RoleContext.class::cast).collect(Collectors.toList());
			final RoleContext roleContext = listRolesContext.get(0); //TODO noch fuer mehrere Rollen
			map.put(e, roleContext.getRole());
			/*for (RoleContext c : listRolesContext) {
				System.out.println(c.getRole().getEntityName());
			}*/
		});
		return map;
	}

	private Stream<Context> getContexts(RelatedCharacteristics e) {
		return e.getCharacteristics().getOwnedCharacteristics().stream().filter(ContextCharacteristic.class::isInstance)
				.map(ContextCharacteristic.class::cast).flatMap(i -> i.getContext().stream());
	}

	/**
	 * @return the dataContainer
	 */
	public final DataSpecification getDataContainer() {
		return dataContainer;
	}

	/**
	 * @return the dynamicContainer
	 */
	public final DynamicSpecification getDynamicContainer() {
		return dynamicContainer;
	}

}
