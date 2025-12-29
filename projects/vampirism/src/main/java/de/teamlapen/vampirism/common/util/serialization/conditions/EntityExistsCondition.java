package de.teamlapen.vampirism.common.util.serialization.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.NotNull;

public class EntityExistsCondition implements ICondition {
    public static MapCodec<EntityExistsCondition> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder
                    .group(
                            Identifier.CODEC.fieldOf("entity_type").forGetter(EntityExistsCondition::getEntityType))
                    .apply(builder, EntityExistsCondition::new));

    private final Identifier entity_type;

    public EntityExistsCondition(String location) {
        this(Identifier.parse(location));
    }

    public EntityExistsCondition(String namespace, String path) {
        this(VResourceLocation.loc(namespace, path));
    }

    public EntityExistsCondition(Identifier entity_type) {
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

    public Identifier getEntityType() {
        return this.entity_type;
    }

    @Override
    public String toString() {
        return "entity_type_exists(\"" + this.entity_type + "\")";
    }
}
