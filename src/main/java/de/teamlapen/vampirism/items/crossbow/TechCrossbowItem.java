package de.teamlapen.vampirism.items.crossbow;

import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class TechCrossbowItem extends VampirismCrossbowItem {

    public TechCrossbowItem(Item.Properties properties, float arrowVelocity, int chargeTime, ItemTier itemTier) {
        super(properties, arrowVelocity, chargeTime, itemTier);
    }

    @Nullable
    @Override
    public ISkill getRequiredSkill(@Nonnull ItemStack stack) {
        return HunterSkills.TECH_WEAPONS.get();
    }

    @Nonnull
    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.getItem() == ModItems.TECH_CROSSBOW_AMMO_PACKAGE.get();
    }

    /**
     * same as {@link net.minecraft.item.CrossbowItem#use(net.minecraft.world.World, net.minecraft.entity.player.PlayerEntity, net.minecraft.util.Hand)}
     * <br>
     * check comments
     * TODO 1.19 recheck
     */
    @Nonnull
    @Override
    public ActionResult<ItemStack> use(@Nonnull World p_77659_1_, PlayerEntity p_77659_2_, @Nonnull Hand p_77659_3_) {
        ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
        if (isCharged(itemstack)) {
            if(performShootingMod(p_77659_1_, p_77659_2_, p_77659_3_, itemstack, getShootingPowerMod(itemstack), 1.0F)) { // do not set uncharged if projectiles left | get shooting power from crossbow
                setCharged(itemstack, false);
            } else {
                p_77659_2_.getCooldowns().addCooldown(this, 10); // add cooldown if projectiles left
            }
            return ActionResult.consume(itemstack);
        } else if (!p_77659_2_.getProjectile(itemstack).isEmpty()) {
            if (!isCharged(itemstack)) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
                p_77659_2_.startUsingItem(p_77659_3_);
            }

            return ActionResult.consume(itemstack);
        } else {
            return ActionResult.fail(itemstack);
        }
    }

    /**
     * same as {@link  net.minecraft.item.CrossbowItem#performShooting(net.minecraft.world.World, net.minecraft.entity.LivingEntity, net.minecraft.util.Hand, net.minecraft.item.ItemStack, float, float)} see comments for changes
     * TODO 1.19 recheck
     */
    public boolean performShootingMod(World p_220014_0_, LivingEntity p_220014_1_, Hand p_220014_2_, ItemStack p_220014_3_, float p_220014_4_, float p_220014_5_) {
        List<ItemStack> list = getChargedProjectiles(p_220014_3_);
        float[] afloat = getShotPitches(p_220014_1_.getRandom());

        ItemStack itemstack = getProjectile(p_220014_1_, p_220014_3_, list); //delegate for easy usage and frugality
        boolean flag = p_220014_1_ instanceof PlayerEntity && ((PlayerEntity) p_220014_1_).abilities.instabuild;
        if (!itemstack.isEmpty()) {
            shootProjectileMod(p_220014_0_, p_220014_1_, p_220014_2_, p_220014_3_, itemstack, afloat[0], flag, p_220014_4_, p_220014_5_, 0.0F); // do not shoot more than one projectile
        }

        onCrossbowShot(p_220014_0_, p_220014_1_, p_220014_3_);
        setChargedProjectiles(p_220014_3_, list); // set the loaded projectiles
        return list.isEmpty();
    }

    private static void setChargedProjectiles(ItemStack p_220014_0_, List<ItemStack> p_220014_1_) {
        CompoundNBT compoundnbt = p_220014_0_.getOrCreateTag();
        ListNBT list = new ListNBT();
        p_220014_1_.forEach(stack -> {
            CompoundNBT stackNbt = new CompoundNBT();
            stack.save(stackNbt);
            list.add(stackNbt);
        });
        compoundnbt.put("ChargedProjectiles", list);
    }

    @Override
    public boolean isValidRepairItem(@Nonnull ItemStack crossbow, ItemStack repairItem) {
        return Tags.Items.INGOTS_IRON.contains(repairItem.getItem());
    }

    private ItemStack getProjectile(LivingEntity entity, ItemStack crossbow, List<ItemStack> projectiles) {
        int frugal = isFrugal(crossbow);
        if (frugal > 0 && random.nextInt(Math.max(2, 4 - frugal)) == 0) {
            return projectiles.get(0).copy();
        }
        return projectiles.remove(0);
    }

    @Override
    protected boolean canBeInfinit(ItemStack crossbow) {
        return false;
    }

    @Override
    public int getChargeDurationMod(ItemStack crossbow) {
        return this.chargeTime;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment != Enchantments.QUICK_CHARGE && enchantment != Enchantments.INFINITY_ARROWS && super.canApplyAtEnchantingTable(stack, enchantment);
    }
}
