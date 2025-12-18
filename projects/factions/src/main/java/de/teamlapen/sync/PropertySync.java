package de.teamlapen.sync;

import de.teamlapen.sync.api.ISyncable;
import de.teamlapen.sync.properties.IProperty;
import de.teamlapen.sync.properties.Property;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

/**
 * Base class for sync-able properties.
 * <p>
 * Handles registering, syncing ob properties
 * <p>
 * To make use of this class, you need to override {@link #registerProperties()}. In the implementation you can register properties.
 * <p>
 * Properties are registered using {@link #registerProperty(ResourceLocation)}.
 * <p>
 * To handle property changes, override {@link #onPropertyChanged()}.
 */
public abstract class PropertySync implements ValueIOSerializable, ISyncable, IProperty {

    private final Map<ResourceLocation, Property> propertiesMap = new HashMap<>();
    protected final Collection<Property> properties = Collections.unmodifiableCollection(propertiesMap.values());

    public PropertySync() {
        this.registerProperties();
    }

    /**
     * This method is called in the constructor of this class.
     * It should be used to register properties using {@link #registerProperty(ResourceLocation)}
     */
    @ApiStatus.OverrideOnly
    protected void registerProperties() {

    }

    //<editor-fold desc="IProperty">

    @Override
    public int getStatus() {
        return Objects.hash(this.properties.stream().map(Property::getStatus).toList());
    }

    @Override
    public boolean hasClientSync() {
        return this.properties.stream().anyMatch(Property::hasClientSync);
    }

    @Override
    public boolean hasServerLoad() {
        return this.properties.stream().anyMatch(Property::hasServerLoad);
    }

    //</editor-fold>

    //<editor-fold desc="Properties">

    @ApiStatus.OverrideOnly
    protected void onPropertyChanged() {

    }

    public final void registerProperty(Property property) {
        this.propertiesMap.put(property.key(), property);
    }

    protected final Property.PropertyBuilder registerProperty(ResourceLocation key) {
        return new Property.PropertyBuilder(this, key);
    }

    //</editor-fold>

    //<editor-fold desc="Serialization">

    @Override
    public void serialize(ValueOutput output) {
        for (Property syncProperty : this.properties) {
            if (syncProperty.hasServerLoad()) {
                syncProperty.storeValue(output, Property.StoreMode.FULL);
            }
        }
    }

    @Override
    public void deserialize(ValueInput input) {
        for (Property syncProperty : this.properties) {
            if (!syncProperty.hasServerLoad()) continue;
            syncProperty.loadServer(input);
        }

        onPropertyChanged();
    }

    @Override
    public final void serializeFullUpdate(ValueOutput output) {
        for (Property property : this.properties) {
            if (property.hasClientSync()) {
                property.storeValue(output, Property.StoreMode.FULL_UPDATE);
            }
        }
    }

    @Override
    public final void serializeUpdate(ValueOutput output) {
        for (Property property : this.properties) {
            if (property.hasClientSync() && property.hasChanged()) {
                property.store(output, Property.StoreMode.UPDATE);
            }
        }
    }

    @Override
    public final boolean deserializeUpdate(ValueInput input) {
        boolean changed = false;
        for (Property property : this.properties) {
            if (property.hasClientSync()) {
                changed = property.loadClient(input) || changed;
            }
        }

        if (changed) {
            onPropertyChanged();
            return true;
        }
        return false;
    }

    //</editor-fold>
}
