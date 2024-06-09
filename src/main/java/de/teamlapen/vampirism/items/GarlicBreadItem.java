package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.core.ModFactions;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GarlicBreadItem extends Item implements IFactionExclusiveItem {

    public GarlicBreadItem() {
        super(new Properties().food((new FoodProperties.Builder()).nutrition(6).saturationModifier(0.7F).build()));
    }

    @Override
    public @Nullable Holder<? extends IFaction<?>> getExclusiveFaction(@NotNull ItemStack stack) {
        return ModFactions.HUNTER;
    }


    @NotNull
    @Override
    public ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull LivingEntity entityLiving) {
        if (!worldIn.isClientSide) {
            entityLiving.removeEffectsCuredBy(ModItems.GARLIC_CURE);
        }
        return super.finishUsingItem(stack, worldIn, entityLiving);
    }
}
