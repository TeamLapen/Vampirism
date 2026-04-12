package de.teamlapen.vampirism.data.loot.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.common.world.items.StakeItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class StakeCondition implements LootItemCondition {

    public static final MapCodec<StakeCondition> CODEC = RecordCodecBuilder.mapCodec(inst ->
            inst.group(
                    LootContext.EntityTarget.CODEC.fieldOf("target").forGetter(l -> l.target)
            ).apply(inst, StakeCondition::new));

    public static @NotNull Builder builder(LootContext.EntityTarget target) {
        return () -> new StakeCondition(target);
    }

    private final LootContext.EntityTarget target;

    public StakeCondition(LootContext.EntityTarget targetIn) {
        this.target = targetIn;
    }

    @Override
    public @NonNull MapCodec<? extends LootItemCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull LootContext context) {
        Entity player = context.getOptionalParameter(target.contextParam());
        if (player instanceof Player) {
            ItemStack active = ((Player) player).getMainHandItem();
            return !active.isEmpty() && active.getItem() instanceof StakeItem;
        }
        return false;
    }

}
