package de.teamlapen.vampirism.items.crossbow;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.*;
import de.teamlapen.vampirism.client.extensions.ItemExtensions;
import de.teamlapen.vampirism.core.ModEnchantments;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.mixin.accessor.CrossbowItemMixin;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class VampirismCrossbowItem extends ProjectileWeaponItem implements IFactionLevelItem<IHunterPlayer>, IHunterCrossbow {

    protected final Tier itemTier;
    private final Supplier<ISkill<IHunterPlayer>> requiredSkill;
    protected final float arrowVelocity;
    protected final int chargeTime;
    protected boolean startSoundPlayed = false;
    protected boolean midLoadSoundPlayed = false;

    public VampirismCrossbowItem(Properties properties, float arrowVelocity, int chargeTime, Tier itemTier, @NotNull Supplier<@Nullable ISkill<IHunterPlayer>> requiredSkill) {
        super(properties);
        this.arrowVelocity = arrowVelocity;
        this.chargeTime = chargeTime;
        this.itemTier = itemTier;
        this.requiredSkill = requiredSkill;
    }

    @Override
    public @Nullable ISkill<IHunterPlayer> getRequiredSkill(@NotNull ItemStack stack) {
        return this.requiredSkill.get();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltips, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltips, flag);
        this.addAmmunitionTypeHoverText(stack, level, tooltips, flag);
        this.addFactionToolTips(stack, level, tooltips, flag,  VampirismMod.proxy.getClientPlayer());
    }

    protected void addAmmunitionTypeHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltips, @NotNull TooltipFlag flag) {
        getAmmunition(stack).ifPresent(ammunition -> {
            tooltips.add(Component.translatable("text.vampirism.crossbow.ammo_type").append(" ").append(ammunition.getName(stack)).withStyle(ChatFormatting.GRAY));
        });
    }

    @Override
    public boolean canApplyAtEnchantingTable(@NotNull ItemStack stack, @NotNull Enchantment enchantment) {
        return (super.canApplyAtEnchantingTable(stack, enchantment) && enchantment != Enchantments.MULTISHOT) || getCompatibleEnchantments().contains(enchantment);
    }

    protected Collection<Enchantment> getCompatibleEnchantments() {
        return Arrays.asList(Enchantments.INFINITY_ARROWS, Enchantments.PUNCH_ARROWS, Enchantments.POWER_ARROWS); // plus normal crossbow enchantments
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack crossbow, ItemStack repairItem) {
        return repairItem.is(Tags.Items.STRING) || super.isValidRepairItem(crossbow, repairItem);
    }

    @Override
    public int getEnchantmentValue(@NotNull ItemStack stack) {
        return this.itemTier.getEnchantmentValue();
    }

    @Override
    public int getMinLevel(@NotNull ItemStack stack) {
        return 0;
    }

    @Override
    public @Nullable IFaction<?> getExclusiveFaction(@NotNull ItemStack stack) {
        return  VReference.HUNTER_FACTION;
    }

    @NotNull
    @Override
    public Predicate<ItemStack> getSupportedHeldProjectiles() {
        return getAllSupportedProjectiles();
    }

    @Override
    public int getDefaultProjectileRange() {
        return 8;
    }

    @Override
    public boolean canUseDoubleCrossbow(LivingEntity entity) {
        return entity instanceof Player player && HunterPlayer.get(player).getSkillHandler().isSkillEnabled(HunterSkills.DOUBLE_IT.get());
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return UseAnim.CUSTOM;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(ItemExtensions.HUNTER_CROSSBOW);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack crossbow) {
        return getChargeDuration(crossbow) + 3;
    }

    @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand interactionHand) {
        ItemStack itemstack = player.getItemInHand(interactionHand);
        if (isCharged(itemstack)) {
            shoot(level, player, interactionHand, itemstack);
            ItemStack otherStack = player.getItemInHand(interactionHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
            if (canUseDoubleCrossbow(player) && otherStack.getItem() instanceof VampirismCrossbowItem && isCharged(otherStack)) {
                shoot(level, player, interactionHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, otherStack);
            }
            return InteractionResultHolder.consume(itemstack);
        } else if (!player.getProjectile(itemstack).isEmpty()) {
            if (!isCharged(itemstack)) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
                player.startUsingItem(interactionHand);
            }

            return InteractionResultHolder.consume(itemstack);
        } else {
            return InteractionResultHolder.fail(itemstack);
        }
    }

    protected void shoot(@NotNull Level level, Player player, @NotNull InteractionHand interactionHand, ItemStack itemstack) {
        performShooting(level, player, interactionHand, itemstack, getShootingPowerMod(itemstack), 1.0F);
        setUncharged(player, itemstack);
    }

    @Override
    public boolean performShooting(Level level, LivingEntity shooter, InteractionHand hand, ItemStack stack, float speed, float angle) {
        List<ItemStack> list = getChargedProjectiles(stack);
        float[] afloat = getShotPitches(shooter.getRandom());

        for(int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = list.get(i);
            boolean flag = !(shooter instanceof Player player) || player.getAbilities().instabuild;
            if (!itemstack.isEmpty()) {
                shootProjectile(level, shooter, hand, stack, itemstack, afloat[i], flag, speed, angle);
                break;
            }
        }

        onCrossbowShot(level, shooter, stack);
        return list.isEmpty();
    }

    @Override
    public boolean useOnRelease(ItemStack pStack) {
        return pStack.is(this);
    }

    @SuppressWarnings("UnreachableCode")
    protected void shootProjectile(Level level, LivingEntity shooter, InteractionHand hand, ItemStack crossbow, ItemStack projectile, float pitch, boolean pickup, float speed, float angle) {
        if (!level.isClientSide) {
            AbstractArrow projectileentity = modifyArrow(crossbow, getArrow(level, shooter, crossbow, projectile));
            if (pickup) {
                projectileentity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }

            if (shooter instanceof CrossbowAttackMob crossbowUser) {
                crossbowUser.shootCrossbowProjectile(crossbowUser.getTarget(), crossbow, projectileentity, (float) 0.0);
            } else {
                Vec3 vec31 = shooter.getUpVector(1.0F);
                Quaternionf quaternionf = (new Quaternionf()).setAngleAxis((float) 0.0 * ((float)Math.PI / 180F), vec31.x, vec31.y, vec31.z);
                Vec3 vec3 = shooter.getViewVector(1.0F);
                Vector3f vector3f = vec3.toVector3f().rotate(quaternionf);
                projectileentity.shoot(vector3f.x(), vector3f.y(), vector3f.z(), speed, angle);
            }

            crossbow.hurtAndBreak(1, shooter, (p_220017_1_) -> {
                p_220017_1_.broadcastBreakEvent(hand);
            });

            if (isInfinite(crossbow)) {
                projectileentity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
            level.addFreshEntity(projectileentity);
            level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, pitch);
        }
    }

    protected AbstractArrow modifyArrow(ItemStack stack, AbstractArrow arrowEntity) {
        if (ignoreHurtTimer(stack) && arrowEntity instanceof IEntityCrossbowArrow entity) {
            entity.setIgnoreHurtTimer();
        }

        int j = stack.getEnchantmentLevel(Enchantments.POWER_ARROWS);
        if (j > 0) {
            arrowEntity.setBaseDamage(arrowEntity.getBaseDamage() + (double) j * 0.2D + 0.2D);
        }

        int k = stack.getEnchantmentLevel(Enchantments.PUNCH_ARROWS);

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
    public void releaseUsing(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull LivingEntity entity, int pTimeCharged) {
        int combinedUseDuration = this.getCombinedUseDuration(itemStack, entity, entity.getUsedItemHand());
        int useDuration = this.getUseDuration(itemStack);
        int combinedChargingDuration = combinedUseDuration - pTimeCharged;
        int chargingDuration = useDuration - pTimeCharged;
        if (combinedChargingDuration != useDuration) {
            chargingDuration += combinedUseDuration - useDuration;
        }
        if ((float)chargingDuration/ getChargeDuration(itemStack) >= 1.0F && !isCharged(itemStack) && loadCrossbow(itemStack, level, entity, pTimeCharged)) {
            ItemStack otherStack = entity.getItemInHand(entity.getUsedItemHand() == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
            if (canUseDoubleCrossbow(entity)&& (float)combinedChargingDuration / getCombinedChargeDuration(itemStack, entity, entity.getUsedItemHand()) >= 1f && otherStack.getItem() instanceof VampirismCrossbowItem && !isCharged(otherStack)) {
                loadCrossbow(otherStack, level, entity, pTimeCharged);
            }
            SoundSource source = entity instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.CROSSBOW_LOADING_END, source, 1.0F, 1.0F / (level.random.nextFloat() * 0.5F + 1.0F) + 0.2F);
        }
    }

    protected boolean loadCrossbow(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull LivingEntity entity, int p_77615_4_) {
        if (tryLoadProjectiles(entity, itemStack)) {
            setCharged(itemStack, true);
            return true;
        }
        return false;
    }

    @Override
    public int getChargeDuration(ItemStack crossbow) {
        int i = crossbow.getEnchantmentLevel(Enchantments.QUICK_CHARGE);
        return i == 0 ? this.chargeTime : this.chargeTime - 2 * i;
    }

    protected void setUncharged(Player player, ItemStack stack) {
        int frugal = isFrugal(stack);
        if (frugal > 0 && player.getRandom().nextInt(Math.max(2, 4- frugal)) == 0) {
            return;
        }
        CrossbowItem.setCharged(stack, false);
    }

    protected int isFrugal(ItemStack crossbow) {
        return crossbow.getEnchantmentLevel(ModEnchantments.CROSSBOWFRUGALITY.get());
    }

    protected boolean isInfinite(ItemStack crossbow) {
        return crossbow.getEnchantmentLevel(Enchantments.INFINITY_ARROWS) > 0;
    }

    protected boolean canBeInfinite(ItemStack crossbow) {
        return true;
    }

    protected boolean tryLoadProjectiles(LivingEntity entity, ItemStack crossbow) {
        boolean flag = entity instanceof Player && ((Player)entity).getAbilities().instabuild;
        ItemStack projectile = entity.getProjectile(crossbow);

        if ((projectile.isEmpty() || projectile.getItem() == Items.ARROW) && flag) {
            projectile = getAmmunition(crossbow).orElse(ModItems.CROSSBOW_ARROW_NORMAL.get()).getDefaultInstance();
        }

        if (canBeInfinite(crossbow) && isInfinite(crossbow) && projectile.getItem() instanceof IVampirismCrossbowArrow<?> && ((IVampirismCrossbowArrow<?>) projectile.getItem()).isCanBeInfinite()) {
            projectile = projectile.copy(); // do not consume arrow if infinite
        }

        return loadProjectile(entity, crossbow, projectile, flag);
    }

    protected boolean loadProjectile(LivingEntity entity, ItemStack crossbow, ItemStack projectile, boolean noConsume) {
        if (projectile.isEmpty()) {
            return false;
        } else {
            boolean flag = noConsume && projectile.getItem() instanceof ArrowItem;
            ItemStack itemstack;
            if (!flag && !noConsume) {
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
                    addChargedProjectile(crossbow, arrow);
                }
            } else {
                addChargedProjectile(crossbow, itemstack);
            }
            return true;
        }
    }

    @Override
    public boolean canSelectAmmunition(ItemStack crossbow) {
        return true;
    }

    @Override
    public Optional<Item> getAmmunition(ItemStack crossbow) {
        return Optional.ofNullable(crossbow.getTag()).filter(a -> a.contains("ammunition")).map(a -> a.getString("ammunition")).map(ResourceLocation::new).map(BuiltInRegistries.ITEM::get);
    }

    @Override
    public void setAmmunition(ItemStack crossbow, @Nullable Item ammo) {
        setAmmunition(crossbow, ammo == null ? null : RegUtil.id(ammo));
    }

    @Override
    public void setAmmunition(ItemStack crossbow, @Nullable ResourceLocation ammo) {
        if (ammo == null) {
            crossbow.getOrCreateTag().remove("ammunition");
        } else {
            crossbow.getOrCreateTag().putString("ammunition", ammo.toString());
        }
    }

    @Override
    public Predicate<ItemStack> getSupportedProjectiles(ItemStack crossbow) {
        return getAllSupportedProjectiles().and(stack -> {
            if (canSelectAmmunition(crossbow)) {
                return getAmmunition(crossbow).map(restriction -> stack.getItem() == restriction).orElse(true);
            } else {
                return true;
            }
        });
    }

    @Override
    public List<ItemStack> getChargedProjectiles(ItemStack stack) {
        return CrossbowItemMixin.getChargedProjectiles(stack);
    }

    @Override
    public void setCharged(ItemStack crossbowStack, boolean charged) {
        CrossbowItem.setCharged(crossbowStack, charged);
    }

    @Override
    public boolean isCharged(ItemStack stack) {
        return CrossbowItem.isCharged(stack);
    }

    @Override
    public float[] getShotPitches(RandomSource pRandom) {
        return CrossbowItemMixin.getShotPitches(pRandom);
    }

    void onCrossbowShot(Level pLevel, LivingEntity pShooter, ItemStack pCrossbowStack) {
        CrossbowItemMixin.onCrossbowShot(pLevel, pShooter, pCrossbowStack);
    }

    public static AbstractArrow getArrow(Level pLevel, LivingEntity pLivingEntity, ItemStack pCrossbowStack, ItemStack pAmmoStack) {
        return CrossbowItemMixin.getArrow(pLevel, pLivingEntity, pCrossbowStack, pAmmoStack);
    }

    void addChargedProjectile(ItemStack pCrossbowStack, ItemStack pAmmoStack) {
        CrossbowItemMixin.addChargedProjectile(pCrossbowStack, pAmmoStack);
    }
}
