package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GarlicBreadItem extends Item implements IFactionExclusiveItem {

    public GarlicBreadItem() {
        super(new Properties().food((new FoodProperties.Builder()).nutrition(6).saturationMod(0.7F).build()).tab(CreativeModeTab.TAB_FOOD));
    }

    @Nullable
    @Override
    public IFaction<?> getExclusiveFaction(@Nonnull ItemStack stack) {
        return VReference.HUNTER_FACTION;
    }


    @Nonnull
    @Override
    public ItemStack finishUsingItem(@Nonnull ItemStack stack, Level worldIn, @Nonnull LivingEntity entityLiving) {
        if (!worldIn.isClientSide) {
            entityLiving.curePotionEffects(stack);
        }
        return super.finishUsingItem(stack, worldIn, entityLiving);
    }
}
