package de.teamlapen.vampirism.client.models.items.properties;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.world.items.crossbow.HunterCrossbowItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record HunterCrossbowPull() implements RangeSelectItemModelProperty {

    public static final Identifier ID = VIdentifier.mod("hunter_crossbow_pull");
    public static final MapCodec<HunterCrossbowPull> CODEC = MapCodec.unit(HunterCrossbowPull::new);

    @Override
    public float get(@NotNull ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int light) {
        LivingEntity entity = owner == null ? null : owner.asLivingEntity();
        if (entity == null || CrossbowItem.isCharged(stack) || !(stack.getItem() instanceof HunterCrossbowItem crossbow)) {
            return 0f;
        }
        InteractionHand hand = HunterCrossbowItem.getHeldHand(entity, stack);
        if (hand == null) {
            return 0f; // Not actually held
        }
        return crossbow.getChargeProgress(stack, entity, hand);
    }

    @Override
    public @NotNull MapCodec<? extends RangeSelectItemModelProperty> type() {
        return CODEC;
    }
}
