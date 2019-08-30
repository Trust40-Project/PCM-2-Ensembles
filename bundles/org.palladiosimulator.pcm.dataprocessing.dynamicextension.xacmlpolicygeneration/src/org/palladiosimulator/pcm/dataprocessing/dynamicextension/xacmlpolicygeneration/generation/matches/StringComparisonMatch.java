package org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.generation.matches;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.palladiosimulator.pcm.dataprocessing.dataprocessing.characteristics.RelatedCharacteristics;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.context.InternalStateContext;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.xacmlpolicygeneration.handlers.MainHandler;

import com.att.research.xacml.api.XACML3;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;

/**
 * Represents a match which uses a string comparison. Standard implementation uses string equality.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class StringComparisonMatch extends Match {
    static {
        MatchRegistry.getInstance().put(InternalStateContext.class, StringComparisonMatch.class);
    }
    
    private static final String NAME_ENTITY = "entity:name";
    private static final String CONTEXT_INTERNAL_STATE = "context:internalstate";

    private final String value;

    /**
     * Creates a new StringComparisonMatch for the entity name.
     * 
     * @param entity
     *            - the related characteristics representing the entity
     */
    public StringComparisonMatch(final RelatedCharacteristics entity) {
        super(ID_CATEGORY_ACTION, NAME_ENTITY);
        final boolean entityNameExists = entity != null && entity.getEntityName() != null;
        this.value = entityNameExists ? entity.getEntityName() : "NO_ENTITY";
        if (!entityNameExists) {
            MainHandler.LOGGER.warn("model error: entity without name!");
        }
    }

    /**
     * Creates a new StringComparisonMatch for an internal state.
     * 
     * @param context
     *            - the internal state context
     */
    public StringComparisonMatch(final InternalStateContext context) {
        super(ID_CATEGORY_RESOURCE, CONTEXT_INTERNAL_STATE);
        Objects.requireNonNull(context);
        Objects.requireNonNull(context.getState());
        this.value = Objects.requireNonNull(context.getState().getEntityName());
    }

    /**
     * Constructor for the string comparison match.
     * 
     * @param categoryId - the category id
     * @param contextId - the context id
     * @param value - the string value
     */
    protected StringComparisonMatch(final String categoryId, final String contextId, final String value) {
        super(Objects.requireNonNull(categoryId), Objects.requireNonNull(contextId));
        this.value = Objects.requireNonNull(value);
    }

    /**
     * Gets the value.
     * 
     * @return the value
     */
    protected String getValue() {
        return this.value;
    }

    @Override
    public List<MatchType> getMatches() {
        final MatchType match = getEmptyMatch();
        // Attribute value
        match.getAttributeValue().getContent().add(this.value);
        match.getAttributeValue().setDataType(XACML3.ID_DATATYPE_STRING.stringValue());

        // Attribute designator
        match.getAttributeDesignator().setDataType(XACML3.ID_DATATYPE_STRING.stringValue());

        // Match
        match.setMatchId(XACML3.ID_FUNCTION_STRING_EQUAL.stringValue());
        return Arrays.asList(match);
    }
}
