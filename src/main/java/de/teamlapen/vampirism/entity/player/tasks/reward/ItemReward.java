package de.teamlapen.vampirism.entity.player.tasks.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskRewardInstance;
import de.teamlapen.vampirism.api.entity.player.task.TaskReward;
import de.teamlapen.vampirism.core.ModTasks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ItemReward implements TaskReward {

    public static final Codec<ItemReward> CODEC = RecordCodecBuilder.create(inst -> {
        return inst.group(ItemStack.CODEC.fieldOf("item").forGetter(i -> i.reward)
        ).apply(inst, ItemReward::new);
    });

    protected final ItemStack reward;

    public ItemReward(ItemStack reward) {
        this.reward = reward;
    }

    @Override
    public ITaskRewardInstance createInstance(@Nullable IFactionPlayer<?> player) {
        return new Instance(this.reward);
    }

    public List<ItemStack> getAllPossibleRewards() {
        return Collections.singletonList(this.reward);
    }

    @Override
    public Codec<? extends TaskReward> codec() {
        return ModTasks.ITEM_REWARD.get();
    }

    @Override
    public Component description() {
        return this.reward.getItem().getDescription();
    }

    public record Instance(ItemStack reward) implements ITaskRewardInstance {

        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(inst -> {
            return inst.group(ItemStack.CODEC.fieldOf("item").forGetter(i -> i.reward)).apply(inst, Instance::new);
        });

        public Instance(@NotNull ItemStack reward) {
            this.reward = reward;
        }

        @Override
        public void applyReward(IFactionPlayer<?> player) {
            if (!player.getRepresentingPlayer().addItem(this.reward.copy())) {
                player.getRepresentingPlayer().drop(this.reward.copy(), true);
            }
        }

        @Override
        public Codec<? extends ITaskRewardInstance> codec() {
            return ModTasks.ITEM_REWARD_INSTANCE.get();
        }
    }
}
