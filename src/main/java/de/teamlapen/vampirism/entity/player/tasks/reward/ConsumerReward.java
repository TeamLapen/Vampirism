package de.teamlapen.vampirism.entity.player.tasks.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.player.FactionPlayerConsumer;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskRewardInstance;
import de.teamlapen.vampirism.api.entity.player.task.TaskReward;
import de.teamlapen.vampirism.core.ModTasks;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.NotNull;

public class ConsumerReward implements TaskReward, ITaskRewardInstance {

    public static final Codec<ConsumerReward> CODEC = RecordCodecBuilder.create(inst -> {
        return inst.group(
                FactionPlayerConsumer.CODEC.fieldOf("consumer").forGetter(i -> i.consumer),
                ExtraCodecs.COMPONENT.fieldOf("description").forGetter(i -> i.description)
        ).apply(inst, ConsumerReward::new);
    });

    private final @NotNull FactionPlayerConsumer consumer;
    private final Component description;

    public ConsumerReward(@NotNull FactionPlayerConsumer consumer, Component description) {
        this.consumer = consumer;
        this.description = description;
    }

    @Override
    public ITaskRewardInstance createInstance(IFactionPlayer<?> player) {
        return this;
    }

    @Override
    public Codec<ConsumerReward> codec() {
        return ModTasks.CONSUMER.get();
    }

    @Override
    public Component description() {
        return this.description;
    }

    @Override
    public void applyReward(IFactionPlayer<?> player) {
        this.consumer.accept(player);
    }
}
