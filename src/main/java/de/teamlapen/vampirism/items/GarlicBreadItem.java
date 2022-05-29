package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Food;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.item.Item;

import javax.annotation.Nonnull;

public class GarlicBreadItem extends Item implements IFactionExclusiveItem {

    public GarlicBreadItem() {
        super(new Properties().food((new Food.Builder()).nutrition(6).saturationMod(0.7F).build()).tab(ItemGroup.TAB_FOOD));
    }

    @Nonnull
    @Override
    public IFaction<?> getExclusiveFaction() {
        return VReference.HUNTER_FACTION;
    }


    @Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        if (!worldIn.isClientSide) {
            entityLiving.curePotionEffects(stack);
        }
        return super.finishUsingItem(stack, worldIn, entityLiving);
    }
}
