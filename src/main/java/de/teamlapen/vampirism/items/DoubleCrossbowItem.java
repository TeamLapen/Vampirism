package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


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
    public ISkill<IHunterPlayer> getRequiredSkill(@NotNull ItemStack stack) {
        return HunterSkills.DOUBLE_CROSSBOW.get();
    }


    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        shoot(playerIn, 0, 0, worldIn, stack, handIn);
        shoot(playerIn, -0.2F, 0, worldIn, stack, handIn);
        return new InteractionResultHolder<>(InteractionResult.CONSUME, stack);
    }

    @Override
    protected boolean isIgnoreHurtTime(ItemStack crossbow) {
        return true;
    }
}
