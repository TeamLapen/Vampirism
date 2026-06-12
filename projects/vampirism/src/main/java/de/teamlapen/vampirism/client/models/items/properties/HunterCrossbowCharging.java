package de.teamlapen.vampirism.client.models.items.properties;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.world.items.crossbow.HunterCrossbowItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record HunterCrossbowCharging() implements ConditionalItemModelProperty {

    public static final Identifier ID = VIdentifier.mod("hunter_crossbow_charging");
    public static final MapCodec<HunterCrossbowCharging> CODEC = MapCodec.unit(HunterCrossbowCharging::new);

    @Override
    public boolean get(@NotNull ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, @NotNull ItemDisplayContext displayContext) {
        if (entity == null || !(stack.getItem() instanceof HunterCrossbowItem crossbow)) {
            return false;
        }
        InteractionHand hand = entity.getItemInHand(InteractionHand.OFF_HAND) == stack ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        return crossbow.isChargingHand(entity, hand, stack);
    }

    @Override
    public @NotNull MapCodec<? extends ConditionalItemModelProperty> type() {
        return CODEC;
    }
}
