package de.teamlapen.vampirism.entity.player.tasks.req;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.player.FactionPlayerBooleanSupplier;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.core.ModTasks;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record BooleanRequirement(@NotNull ResourceLocation id, @NotNull FactionPlayerBooleanSupplier function, @NotNull Component description) implements TaskRequirement.Requirement<Boolean> {

    public static final MapCodec<BooleanRequirement> CODEC = RecordCodecBuilder.mapCodec(inst ->
            inst.group(
                    ResourceLocation.CODEC.optionalFieldOf("id").forGetter(s -> java.util.Optional.of(s.id)),
                    FactionPlayerBooleanSupplier.CODEC.fieldOf("function").forGetter(i -> i.function),
                    ComponentSerialization.CODEC.fieldOf("description").forGetter(s -> s.description)
            ).apply(inst, (id, supplier, desc) -> new BooleanRequirement(id.orElseGet(() -> FactionPlayerBooleanSupplier.getId(supplier)), supplier, desc)));

    public BooleanRequirement(@NotNull FactionPlayerBooleanSupplier function, Component description) {
        this(FactionPlayerBooleanSupplier.getId(function), function, description);
    }

    @NotNull
    @Override
    public Boolean getStat(IFactionPlayer<?> player) {
        return this.function.apply(player);
    }

    @Override
    public MapCodec<? extends TaskRequirement.Requirement<?>> codec() {
        return ModTasks.BOOLEAN_REQUIREMENT.get();
    }
}
