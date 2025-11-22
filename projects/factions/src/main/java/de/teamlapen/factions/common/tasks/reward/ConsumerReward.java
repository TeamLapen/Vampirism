package de.teamlapen.factions.common.tasks.reward;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.factions.api.entities.player.FactionPlayerConsumer;
import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import de.teamlapen.factions.api.tasks.ITaskRewardInstance;
import de.teamlapen.factions.api.tasks.TaskReward;
import de.teamlapen.factions.common.core.FactionTasks;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

public record ConsumerReward(FactionPlayerConsumer consumer, Component description) implements TaskReward, ITaskRewardInstance {

    public static final MapCodec<ConsumerReward> CODEC = RecordCodecBuilder.mapCodec(inst ->
            inst.group(
                    FactionPlayerConsumer.CODEC.fieldOf("consumer").forGetter(i -> i.consumer),
                    ComponentSerialization.CODEC.fieldOf("description").forGetter(i -> i.description)
            ).apply(inst, ConsumerReward::new));

    public ConsumerReward(FactionPlayerConsumer consumer, Component description) {
        this.consumer = consumer;
        this.description = description;
    }

    @Override
    public ITaskRewardInstance createInstance(IFactionPlayer<?> player) {
        return this;
    }

    @Override
    public MapCodec<ConsumerReward> codec() {
        return FactionTasks.CONSUMER.get();
    }

    @Override
    public void applyReward(IFactionPlayer<?> player) {
        this.consumer.accept(player);
    }
}
