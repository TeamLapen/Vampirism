package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class DoubleCrossbowItem extends SimpleCrossbowItem {
    /**
     * @param speed         Speed of the shot arrows (0.1F-20F)
     * @param coolDownTicks Cooldown ticks >0
     * @param maxDamage     Max amount of shot arrrows or 0 if unbreakable
     */
    public DoubleCrossbowItem(float speed, int coolDownTicks, int maxDamage, Tiers material) {
        super(speed, coolDownTicks, maxDamage, material);
    }

    @Nullable
    @Override
    public ISkill<IHunterPlayer> getRequiredSkill(@Nonnull ItemStack stack) {
        return HunterSkills.DOUBLE_CROSSBOW.get();
    }


    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level worldIn, Player playerIn, @Nonnull InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        shoot(playerIn, 0, 0, worldIn, stack, handIn);
        shoot(playerIn, -0.2F, 0, worldIn, stack, handIn);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    @Override
    protected boolean isIgnoreHurtTime(ItemStack crossbow) {
        return true;
    }
}
