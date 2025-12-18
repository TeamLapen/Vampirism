package de.teamlapen.factions.common.factions.tasks.requirements;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.factions.api.factions.tasks.TaskRequirement;
import de.teamlapen.factions.api.world.entities.player.FactionPlayerBooleanSupplier;
import de.teamlapen.factions.api.world.entities.player.IFactionPlayer;
import de.teamlapen.factions.common.core.FactionTasks;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;

public record BooleanRequirement(Holder<FactionPlayerBooleanSupplier> function, Component description) implements TaskRequirement.Requirement<Boolean> {

    public static final MapCodec<BooleanRequirement> CODEC = RecordCodecBuilder.mapCodec(inst ->
            inst.group(
                    FactionPlayerBooleanSupplier.CODEC.fieldOf("function").forGetter(i -> i.function),
                    ComponentSerialization.CODEC.fieldOf("description").forGetter(s -> s.description)
            ).apply(inst, BooleanRequirement::new));

    public ResourceLocation id() {
        return this.function.getKey().location();
    }

    @Override
    public Boolean getStat(IFactionPlayer<?> player) {
        return this.function.value().apply(player);
    }

    @Override
    public MapCodec<? extends TaskRequirement.Requirement<?>> codec() {
        return FactionTasks.BOOLEAN_REQUIREMENT.get();
    }
}
