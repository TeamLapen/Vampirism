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
    public void releaseUsing(ItemStack p_77615_1_, World p_77615_2_, LivingEntity p_77615_3_, int p_77615_4_) {
        int i = this.getUseDuration(p_77615_1_) - p_77615_4_;
        float f = getPowerForTimeMod(i, p_77615_1_); // get mod power
        if (f >= 1.0F && !isCharged(p_77615_1_)) {
            boolean first = tryLoadProjectiles(p_77615_3_, p_77615_1_);
            boolean second = tryLoadProjectiles(p_77615_3_, p_77615_1_);
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
    public void performShootingMod(World p_220014_0_, LivingEntity p_220014_1_, Hand p_220014_2_, ItemStack p_220014_3_, float p_220014_4_, float p_220014_5_) {
        List<ItemStack> list = getChargedProjectiles(p_220014_3_);
        float[] afloat = getShotPitches(p_220014_1_.getRandom());

        for(int i = 0; i < list.size() || i < 2; ++i) { // only shoot a maximum of 2 arrows
            ItemStack itemstack = list.get(i);
            boolean flag = p_220014_1_ instanceof PlayerEntity && ((PlayerEntity)p_220014_1_).abilities.instabuild;
            if (!itemstack.isEmpty()) {
                shootProjectile(p_220014_0_, p_220014_1_, p_220014_2_, p_220014_3_, itemstack, afloat[i], flag, p_220014_4_, p_220014_5_, 0.0F); // only one arrow per projectile
            }
        }

        onCrossbowShot(p_220014_0_, p_220014_1_, p_220014_3_);
    }
}
