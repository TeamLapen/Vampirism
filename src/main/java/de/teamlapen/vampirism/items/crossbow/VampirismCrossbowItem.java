package de.teamlapen.vampirism.items.crossbow;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.items.*;
import de.teamlapen.vampirism.core.ModEnchantments;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public abstract class VampirismCrossbowItem extends CrossbowItem implements IFactionLevelItem<IHunterPlayer>, IVampirismCrossbow {

    protected final ItemTier itemTier;
    protected final float arrowVelocity;
    protected final int chargeTime;

    public VampirismCrossbowItem(Item.Properties properties, float arrowVelocity, int chargeTime, ItemTier itemTier) {
        super(properties);
        this.arrowVelocity = arrowVelocity;
        this.chargeTime = chargeTime;
        this.itemTier = itemTier;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World level, @Nonnull List<ITextComponent> tooltips, @Nonnull ITooltipFlag flag) {
        super.appendHoverText(stack, level, tooltips, flag);
        this.addFactionToolTips(stack, level, tooltips, flag,  VampirismMod.proxy.getClientPlayer());
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return (super.canApplyAtEnchantingTable(stack, enchantment) && enchantment != Enchantments.MULTISHOT) || getCompatibleEnchantments().contains(enchantment);
    }

    protected Collection<Enchantment> getCompatibleEnchantments() {
        return Arrays.asList(Enchantments.INFINITY_ARROWS, Enchantments.PUNCH_ARROWS, Enchantments.POWER_ARROWS); // plus normal crossbow enchantments
    }

    @Override
    public boolean isValidRepairItem(@Nonnull ItemStack crossbow, ItemStack repairItem) {
        return Tags.Items.STRING.contains(repairItem.getItem()) || super.isValidRepairItem(crossbow, repairItem);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return this.itemTier.getEnchantmentValue();
    }

    @Override
    public int getMinLevel(@Nonnull ItemStack stack) {
        return 0;
    }

    @Nullable
    @Override
    public IPlayableFaction<IHunterPlayer> getUsingFaction(@Nonnull ItemStack stack) {
        return VReference.HUNTER_FACTION;
    }

    @Nonnull
    @Override
    public Predicate<ItemStack> getSupportedHeldProjectiles() {
        return (stack) -> false;
    }

    @Override
    public int getUseDuration(@Nonnull ItemStack crossbow) {
        return getChargeDurationMod(crossbow) + 3;
    }

    /**
     * same as {@link net.minecraft.item.CrossbowItem#use(net.minecraft.world.World, net.minecraft.entity.player.PlayerEntity, net.minecraft.util.Hand)}<br>
     * check comments for changes
     */
    @Nonnull
    public ActionResult<ItemStack> use(@Nonnull World p_77659_1_, PlayerEntity p_77659_2_, @Nonnull Hand p_77659_3_) {
        ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
        if (isCharged(itemstack)) {
            performShootingMod(p_77659_1_, p_77659_2_, p_77659_3_, itemstack, getShootingPowerMod(itemstack), 1.0F); //call modded shoot function with shooting power
            setUncharged(p_77659_2_, itemstack); //set uncharged with extra steps
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
     * same as {@link net.minecraft.item.CrossbowItem#performShooting(net.minecraft.world.World, net.minecraft.entity.LivingEntity, net.minecraft.util.Hand, net.minecraft.item.ItemStack, float, float)}
     * check comments for changes
     */
    public boolean performShootingMod(World level, LivingEntity shooter, Hand hand, ItemStack stack, float speed, float angle) {
        List<ItemStack> list = getChargedProjectiles(stack);
        float[] afloat = getShotPitches(shooter.getRandom());

        for(int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = list.get(i);
            boolean flag = shooter instanceof PlayerEntity && ((PlayerEntity) shooter).abilities.instabuild;
            if (!itemstack.isEmpty()) {
                shootProjectileMod(level, shooter, hand, stack, itemstack, afloat[i], flag, speed, angle, 0.0F);
                break; // only shoot one projectile
            }
        }

        onCrossbowShot(level, shooter, stack);
        return list.isEmpty();
    }

    /**
     * same as {@link net.minecraft.item.CrossbowItem#shootProjectile(net.minecraft.world.World, net.minecraft.entity.LivingEntity, net.minecraft.util.Hand, net.minecraft.item.ItemStack, net.minecraft.item.ItemStack, float, boolean, float, float, float)}
     * see comments for changes
     */
    protected void shootProjectileMod(World p_220016_0_, LivingEntity p_220016_1_, Hand p_220016_2_, ItemStack p_220016_3_, ItemStack p_220016_4_, float p_220016_5_, boolean p_220016_6_, float p_220016_7_, float p_220016_8_, float p_220016_9_) {
        if (!p_220016_0_.isClientSide) {
            AbstractArrowEntity projectileentity;
            projectileentity = modifyArrow(p_220016_3_, getArrow(p_220016_0_, p_220016_1_, p_220016_3_, p_220016_4_)); // modify arrow
            if (p_220016_6_ || p_220016_9_ != 0.0F) {
                projectileentity.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
            }

            if (p_220016_1_ instanceof ICrossbowUser) {
                ICrossbowUser icrossbowuser = (ICrossbowUser)p_220016_1_;
                icrossbowuser.shootCrossbowProjectile(icrossbowuser.getTarget(), p_220016_3_, projectileentity, p_220016_9_);
            } else {
                Vector3d vector3d1 = p_220016_1_.getUpVector(1.0F);
                Quaternion quaternion = new Quaternion(new Vector3f(vector3d1), p_220016_9_, true);
                Vector3d vector3d = p_220016_1_.getViewVector(1.0F);
                Vector3f vector3f = new Vector3f(vector3d);
                vector3f.transform(quaternion);
                projectileentity.shoot((double)vector3f.x(), (double)vector3f.y(), (double)vector3f.z(), p_220016_7_, p_220016_8_);
            }

            p_220016_3_.hurtAndBreak(1, p_220016_1_, (p_220017_1_) -> {
                p_220017_1_.broadcastBreakEvent(p_220016_2_);
            });
            if(isInfinit(p_220016_3_)){
                projectileentity.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
            }
            p_220016_0_.addFreshEntity(projectileentity);
            p_220016_0_.playSound((PlayerEntity)null, p_220016_1_.getX(), p_220016_1_.getY(), p_220016_1_.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0F, p_220016_5_);
        }
    }

    protected AbstractArrowEntity modifyArrow(ItemStack stack, AbstractArrowEntity arrowEntity) {
        if (ignoreHurtTimer(stack) && arrowEntity instanceof IEntityCrossbowArrow) {
            ((IEntityCrossbowArrow)arrowEntity).setIgnoreHurtTimer();
        }

        int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
        if (j > 0) {
            arrowEntity.setBaseDamage(arrowEntity.getBaseDamage() + (double) j * 0.2D + 0.2D);
        }

        int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);

        if (k > 0) {
            arrowEntity.setKnockback(k);
        }
        return arrowEntity;
    }

    protected boolean ignoreHurtTimer(ItemStack crossbow) {
        return false;
    }

    public float getShootingPowerMod(ItemStack crossbow) {
        return 3.15F * this.arrowVelocity;
    }

    @Override
    public void releaseUsing(@Nonnull ItemStack p_77615_1_, @Nonnull World p_77615_2_, @Nonnull LivingEntity p_77615_3_, int p_77615_4_) {
        int i = this.getUseDuration(p_77615_1_) - p_77615_4_;
        float f = getPowerForTimeMod(i, p_77615_1_);
        if (f >= 1.0F && !isCharged(p_77615_1_) && tryLoadProjectilesMod(p_77615_3_, p_77615_1_)) {
            setCharged(p_77615_1_, true);
            SoundCategory soundcategory = p_77615_3_ instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
            p_77615_2_.playSound(null, p_77615_3_.getX(), p_77615_3_.getY(), p_77615_3_.getZ(), SoundEvents.CROSSBOW_LOADING_END, soundcategory, 1.0F, 1.0F / (random.nextFloat() * 0.5F + 1.0F) + 0.2F);
        }
    }

    public float getPowerForTimeMod(int p_220031_0_, ItemStack p_220031_1_) {
        float f = (float)p_220031_0_ / (float)getChargeDurationMod(p_220031_1_);
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    public int getChargeDurationMod(ItemStack crossbow) {
        int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, crossbow);
        return i == 0 ? this.chargeTime : this.chargeTime - 2 * i;
    }

    protected void setUncharged(PlayerEntity player, ItemStack stack) {
        int frugal = isFrugal(stack);
        if (frugal > 0 && random.nextInt(Math.max(2, 4- frugal)) == 0) {
            return;
        }
        setCharged(stack, false);
    }

    protected int isFrugal(ItemStack crossbow) {
        return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.CROSSBOWFRUGALITY.get(), crossbow);
    }

    protected boolean isInfinit(ItemStack crossbow) {
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, crossbow) > 0;
    }

    protected boolean canBeInfinit(ItemStack crossbow) {
        return true;
    }

    /**
     * from {@link net.minecraft.item.CrossbowItem#tryLoadProjectiles(net.minecraft.entity.LivingEntity, net.minecraft.item.ItemStack)}<br>
     * see comments for change
     */
    protected boolean tryLoadProjectilesMod(LivingEntity entity, ItemStack crossbow) {
        boolean flag = entity instanceof PlayerEntity && ((PlayerEntity)entity).abilities.instabuild;
        ItemStack projectile = entity.getProjectile(crossbow);

        if (projectile.isEmpty() && flag) {
            projectile = new ItemStack(ModItems.CROSSBOW_ARROW_NORMAL.get()); // use crossbow arrow
        }

        if (canBeInfinit(crossbow) && isInfinit(crossbow) && projectile.getItem() instanceof IVampirismCrossbowArrow<?> && ((IVampirismCrossbowArrow<?>) projectile.getItem()).isCanBeInfinite()) {
            projectile = projectile.copy(); // do not consume arrow if infinite
        }

        return loadProjectileMod(entity, crossbow, projectile, false, flag);
    }

    /**
     * from {@link net.minecraft.item.CrossbowItem#loadProjectile(net.minecraft.entity.LivingEntity, net.minecraft.item.ItemStack, net.minecraft.item.ItemStack, boolean, boolean)}<br>
     * changes at comments
     */
    protected boolean loadProjectileMod(LivingEntity p_220023_0_, ItemStack p_220023_1_, ItemStack p_220023_2_, boolean p_220023_3_, boolean p_220023_4_) {
        if (p_220023_2_.isEmpty()) {
            return false;
        } else {
            boolean flag = p_220023_4_ && p_220023_2_.getItem() instanceof ArrowItem;
            ItemStack itemstack;
            if (!flag && !p_220023_4_ && !p_220023_3_) {
                itemstack = p_220023_2_.split(1);
                if (p_220023_2_.isEmpty() && p_220023_0_ instanceof PlayerEntity) {
                    ((PlayerEntity)p_220023_0_).inventory.removeItem(p_220023_2_);
                }
            } else {
                itemstack = p_220023_2_.copy();
            }

            if (itemstack.getItem() instanceof IArrowContainer) { // if arrow container use contents
                for (ItemStack arrow : ((IArrowContainer) itemstack.getItem()).getArrows(itemstack)) {
                    addChargedProjectile(p_220023_1_, arrow);
                }
            } else {
                addChargedProjectile(p_220023_1_, itemstack);
            }
            return true;
        }
    }
}
