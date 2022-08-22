package de.teamlapen.vampirism.items.crossbow;

import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class DoubleCrossbowItem extends VampirismCrossbowItem {

    public DoubleCrossbowItem(Item.Properties properties, float arrowVelocity, int chargeTime, ItemTier itemTier) {
        super(properties, arrowVelocity, chargeTime, itemTier);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return (stack -> stack.getItem() instanceof IVampirismCrossbowArrow<?>);
    }

    @Nullable
    @Override
    public ISkill getRequiredSkill(@Nonnull ItemStack stack) {
        return HunterSkills.DOUBLE_CROSSBOW.get();
    }

    /**
     * same as {@link net.minecraft.item.CrossbowItem#releaseUsing(net.minecraft.item.ItemStack, net.minecraft.world.World, net.minecraft.entity.LivingEntity, int)}<br>
     * check comments for change
     * TODO 1.19 recheck
     */
    @Override
    public void releaseUsing(@Nonnull ItemStack p_77615_1_, @Nonnull World p_77615_2_, @Nonnull LivingEntity p_77615_3_, int p_77615_4_) {
        int i = this.getUseDuration(p_77615_1_) - p_77615_4_;
        float f = getPowerForTimeMod(i, p_77615_1_); // get mod power
        if (f >= 1.0F && !isCharged(p_77615_1_)) {
            boolean first = tryLoadProjectilesMod(p_77615_3_, p_77615_1_);
            boolean second = tryLoadProjectilesMod(p_77615_3_, p_77615_1_);
            if (first || second) { //load two projectiles or only one
                setCharged(p_77615_1_, true);
                SoundCategory soundcategory = p_77615_3_ instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
                p_77615_2_.playSound((PlayerEntity) null, p_77615_3_.getX(), p_77615_3_.getY(), p_77615_3_.getZ(), SoundEvents.CROSSBOW_LOADING_END, soundcategory, 1.0F, 1.0F / (random.nextFloat() * 0.5F + 1.0F) + 0.2F);
            }
        }
    }

    /**
     * same as {@link net.minecraft.item.CrossbowItem#performShooting(net.minecraft.world.World, net.minecraft.entity.LivingEntity, net.minecraft.util.Hand, net.minecraft.item.ItemStack, float, float)}<br>
     * see comments for changes
     * TODO 1.19 recheck
     */
    public boolean performShootingMod(World level, LivingEntity shooter, Hand hand, ItemStack stack, float speed, float angle) {
        List<ItemStack> list = getChargedProjectiles(stack);
        float[] afloat = getShotPitches(shooter.getRandom());

        for(int i = 0; i < list.size() || i < 2; ++i) { // only shoot a maximum of 2 arrows
            ItemStack itemstack = list.get(i);
            boolean flag = shooter instanceof PlayerEntity && ((PlayerEntity) shooter).abilities.instabuild;
            if (!itemstack.isEmpty()) {
                shootProjectileMod(level, shooter, hand, stack, itemstack, afloat[i], flag, speed, angle, 0.0F); // only one arrow per projectile
            }
        }

        onCrossbowShot(level, shooter, stack);
        return list.isEmpty();
    }
}
