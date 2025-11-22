package de.teamlapen.vampirism.client.color.item;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.items.CrossbowArrowItem;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrossbowArrowTint implements ItemTintSource {

    public static final ResourceLocation ID = VResourceLocation.mod("arrow_tint");
    public static final CrossbowArrowTint INSTANCE = new CrossbowArrowTint();
    public static final MapCodec<CrossbowArrowTint> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public int calculate(@NotNull ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        return stack.getItem() instanceof CrossbowArrowItem arrow ? arrow.tintIndex() : -1;
    }

    @Override
    public @NotNull MapCodec<? extends ItemTintSource> type() {
        return CODEC;
    }
}
