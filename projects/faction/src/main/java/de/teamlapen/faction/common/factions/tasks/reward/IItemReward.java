package de.teamlapen.faction.common.factions.tasks.reward;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.factions.tasks.ITaskRewardInstance;
import de.teamlapen.faction.api.factions.tasks.TaskReward;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.common.core.FactionTasks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.List;

public interface IItemReward extends TaskReward {

    List<ItemStack> getAllPossibleRewards();

    public record Instance(ItemStackTemplate reward) implements ITaskRewardInstance {

        public static final MapCodec<IItemReward.Instance> CODEC = RecordCodecBuilder.mapCodec(inst -> {
            return inst.group(ItemStackTemplate.CODEC.fieldOf("item").forGetter(IItemReward.Instance::reward)).apply(inst, IItemReward.Instance::new);
        });

        public Instance(ItemStackTemplate reward) {
            this.reward = reward;
        }

        @Override
        public void applyReward(IFactionPlayer<?> player) {
            if (!player.asEntity().addItem(this.reward.create())) {
                player.asEntity().drop(this.reward.create(), true);
            }
        }

        @Override
        public MapCodec<? extends ITaskRewardInstance> codec() {
            return FactionTasks.ITEM_REWARD_INSTANCE.get();
        }
    }
}
