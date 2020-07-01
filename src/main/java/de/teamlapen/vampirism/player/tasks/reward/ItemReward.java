package de.teamlapen.vampirism.player.tasks.reward;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskReward;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemReward implements TaskReward {

    private final @Nonnull ItemStack reward;

    public ItemReward(@Nonnull ItemStack reward) {
        this.reward = reward;
    }

    @Override
    public void applyReward(IFactionPlayer<?> player) {
        if (!player.getRepresentingPlayer().addItemStackToInventory(this.reward.copy())) {
            player.getRepresentingPlayer().dropItem(this.reward.copy(), true);
        }
    }

    @Nonnull
    public ItemStack getReward() {
        return reward.copy();
    }
}
