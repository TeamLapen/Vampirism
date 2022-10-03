package de.teamlapen.vampirism.entity.player.tasks.reward;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskReward;
import de.teamlapen.vampirism.api.util.NonnullSupplier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ItemReward implements TaskReward {

    protected final NonnullSupplier<ItemStack> reward;

    public ItemReward(NonnullSupplier<ItemStack> reward) {
        this.reward = reward;
    }

    @Override
    public ItemRewardInstance createInstance(@Nullable IFactionPlayer<?> player) {
        return new ItemRewardInstance(reward.get());
    }

    public List<ItemStack> getAllPossibleRewards() {
        return Collections.singletonList(reward.get());
    }
}
