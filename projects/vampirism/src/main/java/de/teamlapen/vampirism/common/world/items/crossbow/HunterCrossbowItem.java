package de.teamlapen.vampirism.common.world.items.crossbow;

import de.teamlapen.vampirism.api.world.items.IArrowContainer;
import de.teamlapen.vampirism.api.world.items.IEntityCrossbowArrow;
import de.teamlapen.vampirism.api.world.items.IHunterCrossbow;
import de.teamlapen.vampirism.api.world.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.tags.ModItemTags;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.common.world.items.QuarrelPouch;
import de.teamlapen.vampirism.common.world.items.component.QuarrelPouchContents;
import de.teamlapen.vampirism.common.world.items.component.SelectedAmmunition;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class HunterCrossbowItem extends CrossbowItem implements IHunterCrossbow {

    protected final ToolMaterial itemTier;
    protected final float arrowVelocity;
    protected final int chargeTime;

    public HunterCrossbowItem(Properties properties, float arrowVelocity, int chargeTime, ToolMaterial itemTier) {
        super(properties.repairable(ModItemTags.CROSSBOW_REPAIRABLE).enchantable(itemTier.enchantmentValue()));
        this.arrowVelocity = arrowVelocity;
        this.chargeTime = chargeTime;
        this.itemTier = itemTier;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltips, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltips, flag);
        this.addAmmunitionTypeHoverText(stack, context, tooltipDisplay, tooltips, flag);
    }

    protected void addAmmunitionTypeHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltips, TooltipFlag flag) {
        getAmmunition(stack).ifPresent(ammunition -> tooltips.accept(Component.translatable("tooltip.vampirism.crossbow.selected_ammo", ammunition.getName(stack)).withStyle(ChatFormatting.GRAY)));
    }

    @Override
    public Predicate<ItemStack> getSupportedHeldProjectiles(ItemStack stack) {
        return getAllSupportedProjectiles();
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles(ItemStack stack) {
        return x -> testProjectile(stack, x);
    }

    @Override
    public Predicate<ItemStack> getSupportedHeldProjectiles() {
        return getAllSupportedProjectiles();
    }

    public int getCombinedUseDuration(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        ItemStack otherItemStack = entity.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        if (otherItemStack.getItem() instanceof HunterCrossbowItem otherItem && !CrossbowItem.isCharged(otherItemStack) && canUseDoubleCrossbow(entity) && !entity.getProjectile(otherItemStack).isEmpty()) {
            return this.getUseDuration(stack, entity) + otherItem.getUseDuration(otherItemStack, entity);
        }
        return this.getUseDuration(stack, entity);
    }

    public boolean canUseDoubleCrossbow(LivingEntity entity) {
        return entity instanceof Player player && HunterPlayer.get(player).getSkillHandler().isSkillEnabled(HunterSkills.DOUBLE_IT);
    }

//    @Override
//    public ItemUseAnimation getUseAnimation(ItemStack pStack) {
//        return ItemUseAnimation.CUSTOM;
//    }

    @Override
    public int getUseDuration(ItemStack pStack, LivingEntity entity) {
        return getChargeDurationMod(pStack, entity.level()) + 3;
    }

    @Override
    public void performShooting(Level level, LivingEntity shooter, InteractionHand hand, ItemStack crossbow, float speed, float inacurracy, @Nullable LivingEntity p_331602_) {
        if (level instanceof ServerLevel serverLevel) {
            if (shooter instanceof Player player && net.neoforged.neoforge.event.EventHooks.onArrowLoose(crossbow, shooter.level(), player, 1, true) < 0) return;
            ChargedProjectiles chargedprojectiles = crossbow.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
            if (!chargedprojectiles.isEmpty()) {
                List<ItemStack> availableProjectiles = new ArrayList<>(chargedprojectiles.getItems());
                List<ItemStack> arrows = getShootingProjectiles(serverLevel, crossbow, availableProjectiles);
                ItemStack otherStack = shooter.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
                this.shoot(serverLevel, shooter, hand, crossbow, arrows, speed, inacurracy * getInaccuracy(crossbow, otherStack.getItem() instanceof IHunterCrossbow), shooter instanceof Player, p_331602_);
                onShoot(shooter, crossbow);
                crossbow.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(availableProjectiles));

                if (hand == InteractionHand.MAIN_HAND) {
                    if (shooter instanceof Player player && canUseDoubleCrossbow(player) && otherStack.getItem() instanceof HunterCrossbowItem otherCrossbow && CrossbowItem.isCharged(otherStack)) {
                        otherCrossbow.use(level, player, InteractionHand.OFF_HAND);
                    }
                }
            }
        }
    }

    @Override
    protected void shoot(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack crossbowStack, List<ItemStack> projectiles, float speed, float inaccuracy, boolean isPlayer, @Nullable LivingEntity p_331167_) {
        for (int i = 0; i < projectiles.size(); i++) {
            ItemStack itemstack = projectiles.get(i);
            if (!itemstack.isEmpty()) {
                crossbowStack.hurtAndBreak(this.getDurabilityUse(itemstack), shooter, hand.asEquipmentSlot());
                Projectile projectile = this.createProjectile(level, shooter, crossbowStack, itemstack, isPlayer);
                if(crossbowStack.remove(ModDataComponents.CROSSBOW_FRUGALITY_TRIGGERED) != null && projectile instanceof AbstractArrow arrow) {
                    arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }
                this.shootProjectile(shooter, projectile, i, speed, inaccuracy, 0, p_331167_);
                level.addFreshEntity(projectile);
            }
        }
    }

    @Override
    public float getInaccuracy(ItemStack stack, boolean doubleCrossbow) {
        return 1;
    }

    protected List<ItemStack> getShootingProjectiles(ServerLevel serverLevel, ItemStack crossbow, List<ItemStack> availableProjectiles) {
        List<ItemStack> shootingProjectiles = List.copyOf(availableProjectiles);
        availableProjectiles.clear();
        return shootingProjectiles;
    }

    protected void onShoot(LivingEntity shooter, ItemStack crossbow) {
        if (shooter instanceof ServerPlayer serverplayer) {
            CriteriaTriggers.SHOT_CROSSBOW.trigger(serverplayer, crossbow);
            serverplayer.awardStat(Stats.ITEM_USED.get(crossbow.getItem()));
        }
    }

    @Override
    public AbstractArrow customArrow(AbstractArrow arrow, ItemStack projectileStack, ItemStack weapon) {
        if (ignoreHurtTimer(projectileStack) && arrow instanceof IEntityCrossbowArrow) {
            ((IEntityCrossbowArrow) arrow).setIgnoreHurtTimer();
        }
        return arrow;
    }

    protected boolean ignoreHurtTimer(ItemStack crossbow) {
        return false;
    }

    @Override
    public boolean releaseUsing(ItemStack itemStack, Level level, LivingEntity entity, int pTimeCharged) {
        int combinedUseDuration = this.getCombinedUseDuration(itemStack, entity, entity.getUsedItemHand());
        int useDuration = this.getUseDuration(itemStack, entity);
        int combinedChargingDuration = combinedUseDuration - pTimeCharged;
        int chargingDuration = useDuration - pTimeCharged;
        if (combinedChargingDuration != useDuration) {
            chargingDuration += combinedUseDuration - useDuration;
        }
        if ((float) chargingDuration / getChargeDurationMod(itemStack, level) >= 1.0F && !CrossbowItem.isCharged(itemStack) && tryLoadProjectiles(entity, itemStack)) {
            ItemStack otherStack = entity.getItemInHand(entity.getUsedItemHand() == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
            if (canUseDoubleCrossbow(entity) && (float) combinedChargingDuration / getCombinedChargeDurationMod(itemStack, entity, entity.getUsedItemHand()) >= 1f && otherStack.getItem() instanceof HunterCrossbowItem && !CrossbowItem.isCharged(otherStack)) {
                tryLoadProjectiles(entity, otherStack);
            }
            SoundSource source = entity instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.CROSSBOW_LOADING_END, source, 1.0F, 1.0F / (level.random.nextFloat() * 0.5F + 1.0F) + 0.2F);
        }
        return false;
    }

    public int getCombinedChargeDurationMod(ItemStack crossbow, LivingEntity entity, InteractionHand hand) {
        ItemStack otherItemStack = entity.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        if (otherItemStack.getItem() instanceof HunterCrossbowItem other && !CrossbowItem.isCharged(otherItemStack) && canUseDoubleCrossbow(entity) && !entity.getProjectile(otherItemStack).isEmpty()) {
            return this.getChargeDurationMod(crossbow, entity.level()) + other.getChargeDurationMod(otherItemStack, entity.level());
        }
        return this.getChargeDurationMod(crossbow, entity.level());
    }

    @Override
    public int getChargeDurationMod(ItemStack crossbow, Level level) {
        Registry<Enchantment> enchantments = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        int i = crossbow.getEnchantmentLevel(enchantments.getOrThrow(Enchantments.QUICK_CHARGE));
        return i == 0 ? this.chargeTime : this.chargeTime - 2 * i;
    }

    protected boolean tryLoadProjectiles(LivingEntity pShooter, ItemStack pCrossbowStack) {
        List<ItemStack> list = drawMod(pCrossbowStack, pShooter.getProjectile(pCrossbowStack), pShooter);
        if (!list.isEmpty()) {
            ArrayList<ItemStack> itemStacks = new ArrayList<>(pCrossbowStack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).getItems());
            itemStacks.addAll(list);
            pCrossbowStack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(itemStacks));
            return true;
        } else {
            return false;
        }
    }

    protected List<ItemStack> getLoadingProjectiles(ItemStack crossbowStack, ItemStack projectileStack, LivingEntity shooter) {
        if (projectileStack.getItem() instanceof QuarrelPouch) {
            QuarrelPouchContents orDefault = projectileStack.getOrDefault(ModDataComponents.QUARREL_POUCH_CONTENTS, QuarrelPouchContents.EMPTY);
            if (!orDefault.isEmpty()) {
                QuarrelPouchContents.Mutable mutable = orDefault.asMutable();
                ItemStack itemStack = getAmmunition(crossbowStack).map(mutable::getSpecific).orElseGet(mutable::getFirst);
                projectileStack.set(ModDataComponents.QUARREL_POUCH_CONTENTS, mutable.toImmutable());
                return List.of(itemStack);
            }
            return List.of();
        } else if (projectileStack.getItem() instanceof IArrowContainer container) {
            if (shooter.hasInfiniteMaterials()) {
                projectileStack = projectileStack.copy();
            }
            return container.getAndRemoveArrows(projectileStack).stream().toList();
        } else {
            return List.of(projectileStack);
        }
    }

    protected List<ItemStack> drawMod(ItemStack crossbowStack, ItemStack projectileStack, LivingEntity shooter) {
        if (projectileStack.isEmpty()) {
            return List.of();
        } else {
            return getLoadingProjectiles(crossbowStack, projectileStack, shooter).stream().map(projectile -> useAmmo(crossbowStack, projectile, shooter, false)).toList();
        }
    }

    protected static ItemStack useAmmo(ItemStack crossbowStack, ItemStack projectileStack, LivingEntity shooter, boolean infinite) {
        boolean flag = !infinite && !(shooter.hasInfiniteMaterials() || (projectileStack.getItem() instanceof ArrowItem && ((ArrowItem) projectileStack.getItem()).isInfinite(projectileStack, crossbowStack, shooter)));
        if (!flag) {
            ItemStack itemstack1 = projectileStack.copyWithCount(1);
            itemstack1.set(DataComponents.INTANGIBLE_PROJECTILE, Unit.INSTANCE);
            return itemstack1;
        } else {
            ItemStack itemstack = projectileStack.split(1);
            if (projectileStack.isEmpty() && shooter instanceof Player player) {
                player.getInventory().removeItem(projectileStack);
            }

            return itemstack;
        }
    }

    @Override
    public boolean canSelectAmmunition(ItemStack crossbow) {
        return true;
    }

    @Override
    public Optional<Item> getAmmunition(ItemStack crossbow) {
        return Optional.ofNullable(crossbow.get(ModDataComponents.SELECTED_AMMUNITION)).map(SelectedAmmunition::item);
    }

    @Override
    public void setAmmunition(ItemStack crossbow, @Nullable Item ammo) {
        if (ammo == null) {
            crossbow.remove(ModDataComponents.SELECTED_AMMUNITION);
        } else {
            crossbow.set(ModDataComponents.SELECTED_AMMUNITION, new SelectedAmmunition(ammo));
        }
    }

    public boolean testProjectile(ItemStack crossbow, ItemStack projectile) {
        if (projectile.getItem() instanceof IVampirismCrossbowArrow<?>) {
            return testQuarrel(crossbow, projectile);
        } else if (projectile.getItem() instanceof QuarrelPouch) {
            return testQuarrelPouch(crossbow, projectile);
        }
        return false;
    }

    public boolean testQuarrelPouch(ItemStack crossbow, ItemStack quarrelPouch) {
        QuarrelPouchContents orDefault = quarrelPouch.getOrDefault(ModDataComponents.QUARREL_POUCH_CONTENTS, QuarrelPouchContents.EMPTY);
        ItemStack stack = getAmmunition(crossbow).map(orDefault::getSpecific).orElseGet(orDefault::getFirst);
        return testProjectile(crossbow, stack);
    }

    public boolean testQuarrel(ItemStack crossbow, ItemStack quarrel) {
        return getAmmunition(crossbow).map(quarrel::is).orElse(true);
    }

}
