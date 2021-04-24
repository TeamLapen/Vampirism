package de.teamlapen.vampirism.player.tasks.reward;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskReward;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class ItemReward implements TaskReward {

    @Nonnull
    protected final ItemStack reward;

    public ItemReward(@Nonnull ItemStack reward) {
        this.reward = reward;
    }

    @Override
    public void applyReward(IFactionPlayer<?> player) {
        if (!player.getRepresentingPlayer().addItemStackToInventory(this.reward.copy())) {
            player.getRepresentingPlayer().dropItem(this.reward.copy(), true);
        }
    }

    public List<ItemStack> getAllPossibleRewards() {
        return Collections.singletonList(reward);
    }

    @Override
    public ItemRewardInstance createInstance(@Nullable IFactionPlayer<?> player) {
        return new ItemRewardInstance(reward);
    }
}
