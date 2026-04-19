package de.teamlapen.faction.common.factions.tasks.reward;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.factions.tasks.ITaskRewardInstance;
import de.teamlapen.faction.api.factions.tasks.TaskReward;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.common.core.FactionTasks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.Collections;
import java.util.List;

public class ItemReward implements TaskReward {

    public static final MapCodec<ItemReward> CODEC = RecordCodecBuilder.mapCodec(inst -> {
        return inst.group(ItemStackTemplate.CODEC.fieldOf("item").forGetter(i -> i.reward)
        ).apply(inst, ItemReward::new);
    });

    protected final ItemStackTemplate reward;

    public ItemReward(ItemStackTemplate reward) {
        this.reward = reward;
    }

    @Override
    public ITaskRewardInstance createInstance(IFactionPlayer<?> player) {
        return new Instance(this.reward);
    }

    public List<ItemStack> getAllPossibleRewards() {
        return Collections.singletonList(this.reward.create());
    }

    @Override
    public MapCodec<? extends TaskReward> codec() {
        return FactionTasks.ITEM_REWARD.get();
    }

    @Override
    public Component description() {
        return Component.translatable(this.reward.item().value().getDescriptionId());
    }

    public record Instance(ItemStackTemplate reward) implements ITaskRewardInstance {

        public static final MapCodec<Instance> CODEC = RecordCodecBuilder.mapCodec(inst -> {
            return inst.group(ItemStackTemplate.CODEC.fieldOf("item").forGetter(i -> i.reward)).apply(inst, Instance::new);
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
