package de.teamlapen.vampirism.items.crossbow;

import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.mixin.accessor.CrossbowItemMixin;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class DoubleCrossbowItem extends VampirismCrossbowItem {

    public DoubleCrossbowItem(Item.Properties properties, float arrowVelocity, int chargeTime, Tier itemTier, Supplier<ISkill<IHunterPlayer>> requiredSkill) {
        super(properties, arrowVelocity, chargeTime, itemTier, requiredSkill);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return (stack -> stack.getItem() instanceof IVampirismCrossbowArrow<?>);
    }

    /**
     * same as {@link  net.minecraft.world.item.CrossbowItem#releaseUsing(net.minecraft.world.item.ItemStack, net.minecraft.world.level.Level, net.minecraft.world.entity.LivingEntity, int)}
     * <br>
     * check comments for change
     * TODO 1.19 recheck
     */
    @Override
    public void releaseUsing(@Nonnull ItemStack p_77615_1_, @Nonnull Level p_77615_2_, @Nonnull LivingEntity p_77615_3_, int p_77615_4_) {
        int i = this.getUseDuration(p_77615_1_) - p_77615_4_;
        float f = getPowerForTimeMod(i, p_77615_1_); // get mod power
        if (f >= 1.0F && !isCharged(p_77615_1_)) {
            boolean first = tryLoadProjectilesMod(p_77615_3_, p_77615_1_);
            boolean second = tryLoadProjectilesMod(p_77615_3_, p_77615_1_);
            if (first || second) { //load two projectiles or only one
                setCharged(p_77615_1_, true);
                SoundSource soundcategory = p_77615_3_ instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
                p_77615_2_.playSound((Player) null, p_77615_3_.getX(), p_77615_3_.getY(), p_77615_3_.getZ(), SoundEvents.CROSSBOW_LOADING_END, soundcategory, 1.0F, 1.0F / (p_77615_2_.random.nextFloat() * 0.5F + 1.0F) + 0.2F);
            }
        }
    }

    /**
     * same as {@link net.minecraft.world.item.CrossbowItem#performShooting(net.minecraft.world.level.Level, net.minecraft.world.entity.LivingEntity, net.minecraft.world.InteractionHand, net.minecraft.world.item.ItemStack, float, float)}
     * <br>
     * see comments for changes
     * TODO 1.19 recheck
     */
    public boolean performShootingMod(Level level, LivingEntity shooter, InteractionHand hand, ItemStack stack, float speed, float angle) {
        List<ItemStack> list = CrossbowItemMixin.getChargedProjectiles(stack);
        float[] afloat = CrossbowItemMixin.getShotPitches(shooter.getRandom());

        for(int i = 0; i < list.size() && i < 2; ++i) { // only shoot a maximum of 2 arrows
            ItemStack itemstack = list.get(i);
            boolean flag = !(shooter instanceof Player player) || player.getAbilities().instabuild;
            if (!itemstack.isEmpty()) {
                shootProjectileMod(level, shooter, hand, stack, itemstack, afloat[i], flag, speed, angle); // only one arrow per projectile
            }
        }

        CrossbowItemMixin.onCrossbowShot(level, shooter, stack);
        return list.isEmpty();
    }
}
