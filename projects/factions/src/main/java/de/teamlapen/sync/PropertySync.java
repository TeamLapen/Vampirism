package de.teamlapen.sync;

import com.mojang.serialization.Codec;
import de.teamlapen.sync.api.IPropertySync;
import de.teamlapen.sync.api.IStatusProvider;
import de.teamlapen.sync.api.ISyncable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class PropertySync implements ValueIOSerializable, ISyncable, IStatusProvider, IPropertySync {

    private final Map<ResourceLocation, Property> propertiesMap = new HashMap<>();
    protected final Collection<Property> properties = Collections.unmodifiableCollection(propertiesMap.values());

    public PropertySync() {
        this.registerProperties();
    }

    @Override
    public abstract void sync();

    @Override
    public int getStatus() {
        return Objects.hash(this.properties.stream().map(Property::getStatus).toList());
    }

    @ApiStatus.OverrideOnly
    protected void registerProperties() {

    }

    @Override
    public boolean hasClientSync() {
        return this.properties.stream().anyMatch(Property::hasClientSync);
    }

    @Override
    public boolean hasServerLoad() {
        return this.properties.stream().anyMatch(Property::hasServerLoad);
    }

    protected final void registerProperty(Property property) {
        this.propertiesMap.put(property.key(), property);
    }

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
    public void serializeFullUpdate(ValueOutput output) {
        for (Property property : this.properties) {
            if (property.hasClientSync()) {
                property.storeValue(output, Property.StoreMode.FULL_UPDATE);
            }
        }
    }

    @Override
    public void serializeUpdate(ValueOutput output) {
        for (Property property : this.properties) {
            if (property.hasClientSync() && property.hasChanged()) {
                property.store(output, Property.StoreMode.UPDATE);
            }
        }
    }

    @Override
    public boolean deserializeUpdate(ValueInput input) {
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

    @ApiStatus.OverrideOnly
    protected void onPropertyChanged() {

    }

    protected final Property.PropertyBuilder registerProperty(ResourceLocation key) {
        return new Property.PropertyBuilder(this, key);
    }
}
