package de.teamlapen.vampirism.client.color.item;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.world.items.QuarrelItem;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuarrelTint implements ItemTintSource {

    public static final Identifier ID = VIdentifier.mod("quarrel_tint");
    public static final QuarrelTint INSTANCE = new QuarrelTint();
    public static final MapCodec<QuarrelTint> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public int calculate(@NotNull ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        return stack.getItem() instanceof QuarrelItem arrow ? arrow.tintIndex() : -1;
    }

    @Override
    public @NotNull MapCodec<? extends ItemTintSource> type() {
        return CODEC;
    }
}
