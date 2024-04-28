package de.teamlapen.vampirism.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.NotNull;

public class EntityExistsCondition implements ICondition {
    public static MapCodec<EntityExistsCondition> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder
                    .group(
                            ResourceLocation.CODEC.fieldOf("entity_type").forGetter(EntityExistsCondition::getEntityType))
                    .apply(builder, EntityExistsCondition::new));

    private final ResourceLocation entity_type;

    public EntityExistsCondition(String location) {
        this(new ResourceLocation(location));
    }

    public EntityExistsCondition(String namespace, String path) {
        this(new ResourceLocation(namespace, path));
    }

    public EntityExistsCondition(ResourceLocation entity_type) {
        this.entity_type = entity_type;
    }

    @Override
    public boolean test(@NotNull IContext context) {
        return BuiltInRegistries.ENTITY_TYPE.containsKey(this.entity_type);
    }

    @Override
    public @NotNull MapCodec<? extends ICondition> codec() {
        return CODEC;
    }

    public ResourceLocation getEntityType() {
        return this.entity_type;
    }

    @Override
    public String toString() {
        return "entity_type_exists(\"" + this.entity_type + "\")";
    }
}
