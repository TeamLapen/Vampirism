package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class DoubleCrossbowItem extends SimpleCrossbowItem {
    /**
     * @param regName       Registry name
     * @param speed         Speed of the shot arrows (0.1F-20F)
     * @param coolDownTicks Cooldown ticks >0
     * @param maxDamage     Max amount of shot arrrows or 0 if unbreakable
     */
    public DoubleCrossbowItem(float speed, int coolDownTicks, int maxDamage, ItemTier enchantability) {
        super(speed, coolDownTicks, maxDamage, enchantability);
    }

    @Nullable
    @Override
    public ISkill getRequiredSkill(@Nonnull ItemStack stack) {
        return HunterSkills.DOUBLE_CROSSBOW.get();
    }


    @Nonnull
    @Override
    public ActionResult<ItemStack> use(@Nonnull World worldIn, PlayerEntity playerIn, @Nonnull Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        shoot(playerIn, 0, 0, worldIn, stack, handIn);
        shoot(playerIn, -0.2F, 0, worldIn, stack, handIn);
        return new ActionResult<>(ActionResultType.CONSUME, stack);
    }

    @Override
    protected boolean isIgnoreHurtTime(ItemStack crossbow) {
        return true;
    }
}
