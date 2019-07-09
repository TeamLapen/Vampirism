package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
    public DoubleCrossbowItem(String regName, float speed, int coolDownTicks, int maxDamage) {
        super(regName, speed, coolDownTicks, maxDamage);
    }

    @Nullable
    @Override
    public ISkill getRequiredSkill(@Nonnull ItemStack stack) {
        return HunterSkills.double_crossbow;
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        shoot(playerIn, 0, 0, worldIn, stack, handIn);
        shoot(playerIn, -0.2F, 0, worldIn, stack, handIn);
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    @Override
    protected boolean isIgnoreHurtTime(ItemStack crossbow) {
        return true;
    }
}
