package de.teamlapen.vampirism.items.crossbow;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.items.*;
import de.teamlapen.vampirism.core.ModEnchantments;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.mixin.CrossbowItemMixin;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public abstract class VampirismCrossbowItem extends CrossbowItem implements IFactionLevelItem<IHunterPlayer>, IVampirismCrossbow {

    protected final Tier itemTier;
    protected final float arrowVelocity;
    protected final int chargeTime;

    public VampirismCrossbowItem(Item.Properties properties, float arrowVelocity, int chargeTime, Tier itemTier) {
        super(properties);
        this.arrowVelocity = arrowVelocity;
        this.chargeTime = chargeTime;
        this.itemTier = itemTier;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltips, @Nonnull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltips, flag);
        this.addFactionLevelToolTip(stack, level, tooltips, flag,  VampirismMod.proxy.getClientPlayer());
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
        return repairItem.is(Tags.Items.STRING) || super.isValidRepairItem(crossbow, repairItem);
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return this.itemTier.getEnchantmentValue();
    }

    @Override
    public int getMinLevel(@Nonnull ItemStack stack) {
        return 0;
    }

    @Override
    public @org.jetbrains.annotations.Nullable IFaction<?> getExclusiveFaction(@NotNull ItemStack stack) {
        return  VReference.HUNTER_FACTION;
    }

    @Nonnull
    @Override
    public Predicate<ItemStack> getSupportedHeldProjectiles() {
        return getAllSupportedProjectiles();
    }

    @Override
    public int getUseDuration(@Nonnull ItemStack crossbow) {
        return getChargeDurationMod(crossbow) + 3;
    }

    /**
     * same as {@link net.minecraft.world.item.CrossbowItem#use(net.minecraft.world.level.Level, net.minecraft.world.entity.player.Player, net.minecraft.world.InteractionHand)}
     * <br>
     * check comments for changes
     */
    @Nonnull
    public InteractionResultHolder<ItemStack> use(@Nonnull Level p_77659_1_, Player p_77659_2_, @Nonnull InteractionHand p_77659_3_) {
        ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
        if (isCharged(itemstack)) {
            performShootingMod(p_77659_1_, p_77659_2_, p_77659_3_, itemstack, getShootingPowerMod(itemstack), 1.0F); //call modded shoot function with shooting power
            setUncharged(p_77659_2_, itemstack); //set uncharged with extra steps
            return InteractionResultHolder.consume(itemstack);
        } else if (!p_77659_2_.getProjectile(itemstack).isEmpty()) {
            if (!isCharged(itemstack)) {
                ((CrossbowItemMixin) this).setStartSoundPlayed(false);
                ((CrossbowItemMixin) this).setMidLoadSoundPlayer(false);
                p_77659_2_.startUsingItem(p_77659_3_);
            }

            return InteractionResultHolder.consume(itemstack);
        } else {
            return InteractionResultHolder.fail(itemstack);
        }
    }

    /**
     * same as {@link net.minecraft.world.item.CrossbowItem#performShooting(net.minecraft.world.level.Level, net.minecraft.world.entity.LivingEntity, net.minecraft.world.InteractionHand, net.minecraft.world.item.ItemStack, float, float)}
     * <br>
     * check comments for changes
     */
    public boolean performShootingMod(Level level, LivingEntity shooter, InteractionHand hand, ItemStack stack, float speed, float angle) {
        List<ItemStack> list = CrossbowItemMixin.getChargedProjectiles(stack);
        float[] afloat = CrossbowItemMixin.getShotPitches(shooter.getRandom());

        for(int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = list.get(i);
            boolean flag = shooter instanceof Player && ((Player) shooter).getAbilities().instabuild;
            if (!itemstack.isEmpty()) {
                shootProjectileMod(level, shooter, hand, stack, itemstack, afloat[i], flag, speed, angle, 0.0F);
                break; // only shoot one projectile
            }
        }

        CrossbowItemMixin.onCrossbowShot(level, shooter, stack);
        return list.isEmpty();
    }

    /**
     * same as {@link net.minecraft.world.item.CrossbowItem#shootProjectile(net.minecraft.world.level.Level, net.minecraft.world.entity.LivingEntity, net.minecraft.world.InteractionHand, net.minecraft.world.item.ItemStack, net.minecraft.world.item.ItemStack, float, boolean, float, float, float)}
     * <br>
     * see comments for changes
     */
    protected void shootProjectileMod(Level p_220016_0_, LivingEntity p_220016_1_, InteractionHand p_220016_2_, ItemStack p_220016_3_, ItemStack p_220016_4_, float p_220016_5_, boolean p_220016_6_, float p_220016_7_, float p_220016_8_, float p_220016_9_) {
        if (!p_220016_0_.isClientSide) {
            AbstractArrow projectileentity;
            projectileentity = modifyArrow(p_220016_3_, CrossbowItemMixin.getArrow(p_220016_0_, p_220016_1_, p_220016_3_, p_220016_4_)); // modify arrow
            if (p_220016_6_ || p_220016_9_ != 0.0F) {
                projectileentity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }

            if (p_220016_1_ instanceof CrossbowAttackMob) {
                CrossbowAttackMob icrossbowuser = (CrossbowAttackMob)p_220016_1_;
                icrossbowuser.shootCrossbowProjectile(icrossbowuser.getTarget(), p_220016_3_, projectileentity, p_220016_9_);
            } else {
                Vec3 vector3d1 = p_220016_1_.getUpVector(1.0F);
                Quaternion quaternion = new Quaternion(new Vector3f(vector3d1), p_220016_9_, true);
                Vec3 vector3d = p_220016_1_.getViewVector(1.0F);
                Vector3f vector3f = new Vector3f(vector3d);
                vector3f.transform(quaternion);
                projectileentity.shoot((double)vector3f.x(), (double)vector3f.y(), (double)vector3f.z(), p_220016_7_, p_220016_8_);
            }

            p_220016_3_.hurtAndBreak(1, p_220016_1_, (p_220017_1_) -> {
                p_220017_1_.broadcastBreakEvent(p_220016_2_);
            });
            p_220016_0_.addFreshEntity(projectileentity);
            p_220016_0_.playSound((Player)null, p_220016_1_.getX(), p_220016_1_.getY(), p_220016_1_.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, p_220016_5_);
        }
    }

    protected AbstractArrow modifyArrow(ItemStack stack, AbstractArrow arrowEntity) {
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
    public void releaseUsing(@Nonnull ItemStack p_77615_1_, @Nonnull Level p_77615_2_, @Nonnull LivingEntity p_77615_3_, int p_77615_4_) {
        int i = this.getUseDuration(p_77615_1_) - p_77615_4_;
        float f = getPowerForTimeMod(i, p_77615_1_);
        if (f >= 1.0F && !isCharged(p_77615_1_) && tryLoadProjectilesMod(p_77615_3_, p_77615_1_)) {
            setCharged(p_77615_1_, true);
            SoundSource soundcategory = p_77615_3_ instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
            p_77615_2_.playSound(null, p_77615_3_.getX(), p_77615_3_.getY(), p_77615_3_.getZ(), SoundEvents.CROSSBOW_LOADING_END, soundcategory, 1.0F, 1.0F / (p_77615_2_.random.nextFloat() * 0.5F + 1.0F) + 0.2F);
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

    protected void setUncharged(Player player, ItemStack stack) {
        int frugal = isFrugal(stack);
        if (frugal > 0 && player.getRandom().nextInt(Math.max(2, 4- frugal)) == 0) {
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
     * from {@link net.minecraft.world.item.CrossbowItem#tryLoadProjectiles(net.minecraft.world.entity.LivingEntity, net.minecraft.world.item.ItemStack)}
     * <br>
     * see comments for change
     */
    protected boolean tryLoadProjectilesMod(LivingEntity entity, ItemStack crossbow) {
        boolean flag = entity instanceof Player && ((Player)entity).getAbilities().instabuild;
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
     * from {@link net.minecraft.world.item.CrossbowItem#loadProjectile(net.minecraft.world.entity.LivingEntity, net.minecraft.world.item.ItemStack, net.minecraft.world.item.ItemStack, boolean, boolean)}
     * <br>
     * changes at comments
     */
    protected boolean loadProjectileMod(LivingEntity entity, ItemStack crossbow, ItemStack projectile, boolean p_220023_3_, boolean noConsume) {
        if (projectile.isEmpty()) {
            return false;
        } else {
            boolean flag = noConsume && projectile.getItem() instanceof ArrowItem;
            ItemStack itemstack;
            if (!flag && !noConsume && !p_220023_3_) {
                itemstack = projectile.getItem() instanceof IArrowContainer ? projectile : projectile.split(1);
                if (projectile.isEmpty() && entity instanceof Player) {
                    ((Player)entity).getInventory().removeItem(projectile);
                }
            } else {
                itemstack = projectile.getItem() instanceof IArrowContainer ? projectile : projectile.copy();
            }

            if (itemstack.getItem() instanceof IArrowContainer container) { // if arrow container use contents
                Collection<ItemStack> projectiles = noConsume ? container.getArrows(projectile) : container.getAndRemoveArrows(projectile);
                for (ItemStack arrow : projectiles) {
                    CrossbowItemMixin.addChargedProjectile(crossbow, arrow);
                }
            } else {
                CrossbowItemMixin.addChargedProjectile(crossbow, itemstack);
            }
            return true;
        }
    }
}
