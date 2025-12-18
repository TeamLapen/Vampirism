package de.teamlapen.factions.common.factions.tasks.requirements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.factions.api.factions.tasks.TaskRequirement;
import de.teamlapen.factions.api.world.entities.player.IFactionPlayer;
import de.teamlapen.factions.common.core.FactionTasks;
import de.teamlapen.factions.common.util.RegUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Range;

public record EntityRequirement(ResourceLocation id, EntityType<?> entityType, @Range(from = 0, to = Integer.MAX_VALUE) int amount, Component description) implements TaskRequirement.Requirement<EntityType<?>> {

    public static final MapCodec<EntityRequirement> CODEC = RecordCodecBuilder.mapCodec(inst -> {
        return inst.group(
                ResourceLocation.CODEC.optionalFieldOf("id").forGetter(s -> java.util.Optional.of(s.id)),
                BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entityType").forGetter(i -> i.entityType),
                Codec.INT.fieldOf("amount").forGetter(s -> s.amount),
                ComponentSerialization.CODEC.fieldOf("description").forGetter(s -> s.description)
        ).apply(inst, (id, type, amount, desc) -> new EntityRequirement(id.orElseGet(() -> RegUtil.id(type)), type, amount, desc));
    });

    public EntityRequirement(EntityType<?> entityType, int amount, Component component) {
        this(RegUtil.id(entityType), entityType, amount, component);
    }

    @Override
    public int getAmount(IFactionPlayer<?> player) {
        return amount;
    }

    @Override
    public EntityType<?> getStat(IFactionPlayer<?> player) {
        return entityType;
    }

    @Override
    public TaskRequirement.Type getType() {
        return TaskRequirement.Type.ENTITY;
    }

    @Override
    public MapCodec<? extends TaskRequirement.Requirement<?>> codec() {
        return FactionTasks.ENTITY_REQUIREMENT.get();
    }
}
