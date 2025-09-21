package de.teamlapen.vampirism.client.colors;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RefinementTint implements ItemTintSource {

    public static final RefinementTint INSTANCE = new RefinementTint();
    public static final MapCodec<RefinementTint> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public int calculate(@NotNull ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        if (stack.getItem() instanceof IRefinementItem refinementItem) {
            IRefinementSet set = refinementItem.getRefinementSet(stack);
            if (set != null) {
                return set.getColor() | 0xFF000000;
            }
        }
        return -1;
    }

    @Override
    public @NotNull MapCodec<? extends ItemTintSource> type() {
        return CODEC;
    }
}
