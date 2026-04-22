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

public class ItemReward implements IItemReward {

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

    @Override
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

}
