package de.teamlapen.vampirism.entity.player.tasks.req;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.core.ModTasks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * the entity tag needs a translation key with format {@code tasks.vampirism.<tagid>}
 */
public record EntityTypeRequirement(@NotNull ResourceLocation id, @NotNull TagKey<EntityType<?>> entityType, @Range(from = 0, to = Integer.MAX_VALUE) int amount,
                                    @NotNull Component description) implements TaskRequirement.Requirement<TagKey<EntityType<?>>> {

    public static final Codec<EntityTypeRequirement> CODEC = RecordCodecBuilder.create(inst -> {
        return inst.group(
                ResourceLocation.CODEC.optionalFieldOf("id").forGetter(s -> java.util.Optional.of(s.id)),
                TagKey.codec(ForgeRegistries.Keys.ENTITY_TYPES).fieldOf("entityType").forGetter(i -> i.entityType),
                Codec.INT.fieldOf("amount").forGetter(s -> s.amount),
                ExtraCodecs.COMPONENT.fieldOf("description").forGetter(s -> s.description)
        ).apply(inst, (id, type, amount, desc) -> new EntityTypeRequirement(id.orElseGet(type::location), type, amount, desc));
    });

    public EntityTypeRequirement(@NotNull TagKey<EntityType<?>> entityType, int amount, Component description) {
        this(entityType.location(), entityType, amount, description);
    }

    @Override
    public int getAmount(IFactionPlayer<?> player) {
        return amount;
    }

    @NotNull
    @Override
    public TagKey<EntityType<?>> getStat(IFactionPlayer<?> player) {
        return entityType;
    }

    @NotNull
    @Override
    public TaskRequirement.Type getType() {
        return TaskRequirement.Type.ENTITY_TAG;
    }

    @Override
    public Codec<? extends TaskRequirement.Requirement<?>> codec() {
        return ModTasks.ENTITY_TYPE_REQUIREMENT.get();
    }
}
