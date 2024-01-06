package de.teamlapen.vampirism.entity.player.tasks.req;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.core.ModTasks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public record StatRequirement(@NotNull ResourceLocation id, @NotNull ResourceLocation stat, @Range(from = 0, to = Integer.MAX_VALUE) int amount, @NotNull Component description) implements TaskRequirement.Requirement<ResourceLocation> {

    public static final Codec<StatRequirement> CODEC = RecordCodecBuilder.create(inst -> {
        return inst.group(
                ResourceLocation.CODEC.optionalFieldOf("id").forGetter(s -> java.util.Optional.of(s.id)),
                BuiltInRegistries.CUSTOM_STAT.byNameCodec().fieldOf("stat").forGetter(i -> i.stat),
                Codec.INT.fieldOf("amount").forGetter(s -> s.amount),
                ComponentSerialization.CODEC.fieldOf("description").forGetter(s -> s.description)
        ).apply(inst, (id, statId, amount, description) -> {
            return new StatRequirement(statId, statId, amount, description);
        });
    });

    public StatRequirement(@NotNull ResourceLocation id, @NotNull ResourceLocation stat, int amount, Component description) {
        this.id = id;
        this.stat = stat;
        this.amount = amount;
        this.description = description;
    }

    public StatRequirement(@NotNull ResourceLocation stat, int amount, Component description) {
        this(stat, stat, amount, description);
    }

    @Override
    public int getAmount(IFactionPlayer<?> player) {
        return amount;
    }

    @NotNull
    @Override
    public ResourceLocation getStat(IFactionPlayer<?> player) {
        return stat;
    }

    @NotNull
    @Override
    public TaskRequirement.Type getType() {
        return TaskRequirement.Type.STATS;
    }

    @Override
    public Codec<? extends TaskRequirement.Requirement<?>> codec() {
        return ModTasks.STAT_REQUIREMENT.get();
    }
}