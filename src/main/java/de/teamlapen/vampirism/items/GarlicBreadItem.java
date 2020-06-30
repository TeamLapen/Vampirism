package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Food;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class GarlicBreadItem extends VampirismItem implements IFactionExclusiveItem {
    private static final String regName = "garlic_bread";

    public GarlicBreadItem() {
        super(regName, new Properties().food((new Food.Builder()).hunger(6).saturation(0.7F).build()).group(ItemGroup.FOOD));
    }

    @Nonnull
    @Override
    public IFaction<?> getExclusiveFaction() {
        return VReference.HUNTER_FACTION;
    }


    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        if (!worldIn.isRemote) {
            entityLiving.curePotionEffects(stack);
        }
        return super.onItemUseFinish(stack, worldIn, entityLiving);
    }
}
