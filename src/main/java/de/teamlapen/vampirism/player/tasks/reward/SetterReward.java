package de.teamlapen.vampirism.player.tasks.reward;

import de.teamlapen.vampirism.api.entity.player.task.TaskReward;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class SetterReward implements TaskReward {

    private final @Nonnull Consumer<PlayerEntity> consumer;

    public SetterReward(@Nonnull Consumer<PlayerEntity> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void applyReward(PlayerEntity player) {
        this.consumer.accept(player);
    }
}
