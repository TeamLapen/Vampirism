package de.teamlapen.factions.client.color.tint;

import com.mojang.serialization.MapCodec;
import de.teamlapen.factions.api.items.IRefinementItem;
import de.teamlapen.factions.api.refinements.IRefinementSet;
import de.teamlapen.factions.api.util.REFERENCE;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RefinementTint implements ItemTintSource {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(REFERENCE.MOD_ID,"refinement_tint");
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
