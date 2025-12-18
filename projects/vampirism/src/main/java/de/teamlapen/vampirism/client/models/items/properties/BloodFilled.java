package de.teamlapen.vampirism.client.models.items.properties;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.world.items.component.BottleBlood;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record BloodFilled() implements RangeSelectItemModelProperty {

    public static final ResourceLocation ID = VResourceLocation.mod("blood_filled");
    public static final MapCodec<BloodFilled> CODEC = MapCodec.unit(BloodFilled::new);

    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int light) {
        return (float) stack.getOrDefault(ModDataComponents.BOTTLE_BLOOD, BottleBlood.EMPTY).blood() / BottleBlood.MAX_VALUE;
    }

    @Override
    public @NotNull MapCodec<? extends RangeSelectItemModelProperty> type() {
        return CODEC;
    }
}
