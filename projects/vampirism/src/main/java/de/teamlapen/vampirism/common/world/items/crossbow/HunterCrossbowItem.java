package de.teamlapen.vampirism.common.world.items.crossbow;

import de.teamlapen.vampirism.api.world.items.IHunterCrossbow;
import de.teamlapen.vampirism.api.world.items.IVampirismQuarrel;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModEnchantments;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.world.entity.QuarrelEntity;
import de.teamlapen.vampirism.common.world.items.component.EnchantmentOverride;
import de.teamlapen.vampirism.common.world.items.component.QuarrelPouchContents;
import de.teamlapen.vampirism.common.tags.ModItemTags;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.common.world.items.component.SelectedAmmunition;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.HumanoidArm;
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
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("deprecation")
public abstract class HunterCrossbowItem extends CrossbowItem implements IHunterCrossbow {

    private static final float ACCURACY_PER_PRECISION_LEVEL = 0.3f;
    private static final float GRAVITY_PER_PRECISION_LEVEL = 0.133f;

    private static final double VOLLEY_ARROW_SPACING = 0.225;
    private static final double DUAL_WIELD_SPACING = 0.3;

    protected final ToolMaterial itemTier;
    protected final float arrowVelocity;
    protected final int chargeTime;
    private boolean chargeStartSoundPlayed = false;
    private boolean chargeMidSoundPlayed = false;

    public HunterCrossbowItem(Properties properties, float arrowVelocity, int chargeTime, ToolMaterial itemTier) {
        super(properties.repairable(ModItemTags.CROSSBOW_REPAIRABLE).enchantable(itemTier.enchantmentValue()));
        this.arrowVelocity = arrowVelocity;
        this.chargeTime = chargeTime;
        this.itemTier = itemTier;
    }

    /**
     * @return the maximum number of projectiles this crossbow can hold loaded at once
     */
    public abstract int getMaxLoadedProjectiles();

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltips, TooltipFlag flag) {
        if (!canSelectAmmunition(stack)) return;
        getAmmunition(stack).ifPresent(ammunition -> tooltips.accept(Component.translatable("tooltip.vampirism.crossbow.selected_ammo", ammunition.getDefaultInstance().getHoverName()).withStyle(ChatFormatting.GRAY)));
    }

    @Override
    public Predicate<ItemStack> getSupportedHeldProjectiles(ItemStack stack) {
        return getAllSupportedProjectiles();
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles(ItemStack stack) {
        return projectile -> testProjectile(stack, projectile);
    }

    @Override
    public Predicate<ItemStack> getSupportedHeldProjectiles() {
        return getAllSupportedProjectiles();
    }

    public boolean canUseDoubleCrossbow(LivingEntity entity) {
        return entity instanceof Player player && HunterPlayer.get(player).getSkillHandler().isSkillEnabled(HunterSkills.DOUBLE_IT);
    }

    public int getCombinedUseDuration(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        ItemStack otherStack = entity.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        if (otherStack.getItem() instanceof HunterCrossbowItem otherCrossbow && !CrossbowItem.isCharged(otherStack) && canUseDoubleCrossbow(entity) && !entity.getProjectile(otherStack).isEmpty()) {
            return getUseDuration(stack, entity) + otherCrossbow.getUseDuration(otherStack, entity);
        }

        return getUseDuration(stack, entity);
    }

    public float getChargeProgress(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        if (CrossbowItem.isCharged(stack)) return 1f;

        if (!entity.isUsingItem()) return 0f;

        InteractionHand usedHand = entity.getUsedItemHand();
        ItemStack usedStack = entity.getItemInHand(usedHand);
        if (!(usedStack.getItem() instanceof HunterCrossbowItem usedCrossbow)) return 0f;

        int chargeDuration = getChargeDurationMod(stack, entity.level());
        if (chargeDuration <= 0) return 1f;

        int elapsed = usedCrossbow.getCombinedUseDuration(usedStack, entity, usedHand) - entity.getUseItemRemainingTicks();
        if (hand != usedHand) {
            elapsed -= usedCrossbow.getChargeDurationMod(usedStack, entity.level());
        }

        return Mth.clamp((float) elapsed / chargeDuration, 0f, 1f);
    }

    @Nullable
    public static InteractionHand getHeldHand(LivingEntity entity, ItemStack stack) {
        if (entity.getItemInHand(InteractionHand.MAIN_HAND) == stack) return InteractionHand.MAIN_HAND;
        if (entity.getItemInHand(InteractionHand.OFF_HAND) == stack) return InteractionHand.OFF_HAND;
        return null;
    }

    public boolean isChargingHand(LivingEntity entity, InteractionHand hand, ItemStack stack) {
        if (CrossbowItem.isCharged(stack) || !entity.isUsingItem()) return false;

        InteractionHand usedHand = entity.getUsedItemHand();
        ItemStack usedStack = entity.getItemInHand(usedHand);
        if (!(usedStack.getItem() instanceof HunterCrossbowItem)) return false;

        if (hand == usedHand) return true;

        return canUseDoubleCrossbow(entity) && CrossbowItem.isCharged(usedStack);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return getChargeDurationMod(stack, entity.level()) + 3;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (CrossbowItem.isCharged(stack) && !player.isUsingItem() && canUseDoubleCrossbow(player)) {
            InteractionHand otherHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            ItemStack otherStack = player.getItemInHand(otherHand);
            if (otherStack.getItem() instanceof HunterCrossbowItem && !CrossbowItem.isCharged(otherStack) && !player.getProjectile(otherStack).isEmpty()) {
                player.startUsingItem(otherHand);

                return InteractionResult.CONSUME;
            }
        }

        return super.use(level, player, hand);
    }

    @Override
    public void performShooting(Level level, LivingEntity shooter, InteractionHand hand, ItemStack crossbow, float speed, float inaccuracy, @Nullable LivingEntity targetOverride) {
        performShooting(level, shooter, hand, crossbow, speed, inaccuracy, targetOverride, null);
    }

    protected void performShooting(Level level, LivingEntity shooter, InteractionHand hand, ItemStack crossbow, float speed, float inaccuracy, @Nullable LivingEntity targetOverride, @Nullable Boolean sharedFrugality) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (shooter instanceof Player player && EventHooks.onArrowLoose(crossbow, shooter.level(), player, 1, true) < 0) return;

        ChargedProjectiles chargedProjectiles = crossbow.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        if (chargedProjectiles.isEmpty()) return;

        List<ItemStack> availableProjectiles = new ArrayList<>(chargedProjectiles.itemCopies());
        ItemStack otherStack = shooter.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        boolean dualFire = hand == InteractionHand.MAIN_HAND && canUseDoubleCrossbow(shooter) && otherStack.getItem() instanceof HunterCrossbowItem && CrossbowItem.isCharged(otherStack);
        // When both crossbows fire together, decide frugality once and reuse it for both so their magazines stay in sync.
        Boolean frugality = sharedFrugality;
        if (frugality == null && dualFire) {
            frugality = rollSharedFrugality(serverLevel, crossbow);
        }
        List<ItemStack> arrows = getShootingProjectiles(serverLevel, crossbow, availableProjectiles, frugality);
        shoot(serverLevel, shooter, hand, crossbow, arrows, speed, inaccuracy * getInaccuracy(crossbow, otherStack.getItem() instanceof IHunterCrossbow) * getAccuracyFactor(crossbow, level), shooter instanceof Player, targetOverride);
        onShoot(shooter, crossbow);
        crossbow.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.ofNonEmpty(availableProjectiles));

        if (dualFire) {
            ((HunterCrossbowItem) otherStack.getItem()).performShooting(level, shooter, InteractionHand.OFF_HAND, otherStack, speed, inaccuracy, targetOverride, frugality);
        }
    }

    @Override
    protected void shoot(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack crossbowStack, List<ItemStack> projectiles, float speed, float inaccuracy, boolean isPlayer, @Nullable LivingEntity targetOverride) {
        for (int i = 0; i < projectiles.size(); i++) {
            ItemStack projectileStack = projectiles.get(i);
            if (projectileStack.isEmpty()) continue;

            crossbowStack.hurtAndBreak(getDurabilityUse(projectileStack), shooter, hand.asEquipmentSlot());
            Projectile projectile = createProjectile(level, shooter, crossbowStack, projectileStack, isPlayer);
            if (crossbowStack.remove(ModDataComponents.CROSSBOW_FRUGALITY_TRIGGERED) != null && projectile instanceof AbstractArrow arrow) {
                arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
            IVampirismQuarrel.IQuarrelBehavior behavior = behaviorOf(projectileStack);
            float projectileSpeed = behavior != null ? speed * behavior.properties().velocityFactor() : speed;
            float projectileInaccuracy = behavior != null ? inaccuracy * behavior.properties().inaccuracyFactor() : inaccuracy;
            shootProjectile(shooter, projectile, i, projectileSpeed, projectileInaccuracy, 0, targetOverride);
            applyHorizontalSpread(shooter, hand, projectile, i, projectiles.size());
            level.addFreshEntity(projectile);
        }
    }

    protected void applyHorizontalSpread(LivingEntity shooter, InteractionHand hand, Projectile projectile, int index, int count) {
        Vec3 look = shooter.getViewVector(1.0F);
        Vec3 right = new Vec3(-look.z, 0.0, look.x);
        double length = right.length();
        if (length < 0.0001) return;
        right = right.scale(1.0 / length);

        double offset = (index - (count - 1) / 2.0) * VOLLEY_ARROW_SPACING;
        if (isDualWielding(shooter)) {
            boolean rightArm = (hand == InteractionHand.MAIN_HAND) == (shooter.getMainArm() == HumanoidArm.RIGHT);
            offset += (rightArm ? 1 : -1) * DUAL_WIELD_SPACING;
        }

        projectile.setPos(projectile.getX() + right.x * offset, projectile.getY(), projectile.getZ() + right.z * offset);
    }

    private boolean isDualWielding(LivingEntity shooter) {
        return canUseDoubleCrossbow(shooter) && shooter.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof HunterCrossbowItem && shooter.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof HunterCrossbowItem;
    }

    @Override
    public float getInaccuracy(ItemStack stack, boolean doubleCrossbow) {
        return 1;
    }

    protected List<ItemStack> getShootingProjectiles(ServerLevel serverLevel, ItemStack crossbow, List<ItemStack> availableProjectiles, @Nullable Boolean sharedFrugality) {
        List<ItemStack> shootingProjectiles = List.copyOf(availableProjectiles);
        availableProjectiles.clear();

        return shootingProjectiles;
    }

    @Nullable
    protected Boolean rollSharedFrugality(ServerLevel level, ItemStack crossbow) {
        return null;
    }

    protected void onShoot(LivingEntity shooter, ItemStack crossbow) {
        if (shooter instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.SHOT_CROSSBOW.trigger(serverPlayer, crossbow);
            serverPlayer.awardStat(Stats.ITEM_USED.get(crossbow.getItem()));
        }
    }

    @Override
    public AbstractArrow customArrow(AbstractArrow arrow, ItemStack projectileStack, ItemStack weapon) {
        if (arrow instanceof QuarrelEntity quarrel) {
            quarrel.setIgnoreHurtTimer();
            IVampirismQuarrel.IQuarrelBehavior behavior = behaviorOf(projectileStack);
            float gravity = getGravityFactor(weapon, arrow.level());
            if (behavior != null) {
                gravity *= behavior.properties().gravityFactor();
                if (behavior.properties().extraPierceLevel() > 0) {
                    quarrel.setPierceLevel((byte) Math.min(Byte.MAX_VALUE, quarrel.getPierceLevel() + behavior.properties().extraPierceLevel()));
                }
            }
            quarrel.setGravityFactor(gravity);
        }

        return arrow;
    }

    @Nullable
    private static IVampirismQuarrel.IQuarrelBehavior behaviorOf(ItemStack projectileStack) {
        return projectileStack.getItem() instanceof QuarrelItem quarrel ? quarrel.getBehavior() : null;
    }

    protected int getPrecisionLevel(ItemStack crossbow, Level level) {
        Registry<Enchantment> enchantments = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        return crossbow.getEnchantmentLevel(enchantments.getOrThrow(ModEnchantments.PRECISION));
    }

    protected float getAccuracyFactor(ItemStack crossbow, Level level) {
        int precision = getPrecisionLevel(crossbow, level);
        return precision <= 0 ? 1f : Math.max(0.1f, 1f - ACCURACY_PER_PRECISION_LEVEL * precision);
    }

    protected float getGravityFactor(ItemStack crossbow, Level level) {
        int precision = getPrecisionLevel(crossbow, level);
        return precision <= 0 ? 1f : Math.max(0.1f, 1f - GRAVITY_PER_PRECISION_LEVEL * precision);
    }

    @Override
    public boolean releaseUsing(ItemStack itemStack, Level level, LivingEntity entity, int remainingTime) {
        InteractionHand hand = entity.getUsedItemHand();
        int elapsed = getCombinedUseDuration(itemStack, entity, hand) - remainingTime;
        if (chargeCrossbows(level, entity, itemStack, hand, elapsed)) {
            playLoadingEndSound(level, entity);
        }

        return false;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int ticksRemaining) {
        if (level.isClientSide()) return;

        InteractionHand hand = entity.getUsedItemHand();
        int elapsed = getCombinedUseDuration(stack, entity, hand) - ticksRemaining;
        playChargeSounds(level, entity, stack, hand, elapsed);
        if (level instanceof ServerLevel serverLevel) {
            tickChargingBehavior(serverLevel, entity, stack, elapsed);
        }
        if (chargeCrossbows(level, entity, stack, hand, elapsed)) {
            playLoadingEndSound(level, entity);
        }
    }

    private void tickChargingBehavior(ServerLevel level, LivingEntity entity, ItemStack stack, int elapsed) {
        if (CrossbowItem.isCharged(stack)) return;

        IVampirismQuarrel.IQuarrelBehavior behavior = getChargingBehavior(entity, stack);
        if (behavior == null) return;
        
        int chargeDuration = getChargeDurationMod(stack, level);
        float progress = chargeDuration <= 0 ? 1f : Mth.clamp((float) elapsed / chargeDuration, 0f, 1f);
        behavior.onChargeTick(level, entity, stack, progress);
    }

    private boolean chargeCrossbows(Level level, LivingEntity entity, ItemStack stack, InteractionHand hand, int elapsed) {
        int chargeDuration = getChargeDurationMod(stack, level);
        boolean loaded = !CrossbowItem.isCharged(stack) && elapsed >= chargeDuration && tryLoadProjectiles(entity, stack);

        if (CrossbowItem.isCharged(stack) && canUseDoubleCrossbow(entity)) {
            ItemStack otherStack = entity.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
            if (otherStack.getItem() instanceof HunterCrossbowItem otherCrossbow && !CrossbowItem.isCharged(otherStack) && !entity.getProjectile(otherStack).isEmpty() && elapsed - chargeDuration >= otherCrossbow.getChargeDurationMod(otherStack, level) && otherCrossbow.tryLoadProjectiles(entity, otherStack)) {
                loaded = true;
            }
        }

        return loaded;
    }

    private void playChargeSounds(Level level, LivingEntity entity, ItemStack stack, InteractionHand hand, int elapsed) {
        float progress;

        if (!CrossbowItem.isCharged(stack)) {
            progress = (float) elapsed / getChargeDurationMod(stack, level);
        } else {
            ItemStack otherStack = entity.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
            if (!canUseDoubleCrossbow(entity) || !(otherStack.getItem() instanceof HunterCrossbowItem otherCrossbow) || CrossbowItem.isCharged(otherStack)) return;
            progress = (float) (elapsed - getChargeDurationMod(stack, level)) / otherCrossbow.getChargeDurationMod(otherStack, level);
        }

        if (progress < 0.2F) {
            this.chargeStartSoundPlayed = false;
            this.chargeMidSoundPlayed = false;
        }
        if (progress >= 0.2F && !this.chargeStartSoundPlayed) {
            this.chargeStartSoundPlayed = true;
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.CROSSBOW_LOADING_START, SoundSource.PLAYERS, 0.5F, 1.0F);
        }
        if (progress >= 0.5F && !this.chargeMidSoundPlayed) {
            this.chargeMidSoundPlayed = true;
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.CROSSBOW_LOADING_MIDDLE, SoundSource.PLAYERS, 0.5F, 1.0F);
        }
    }

    private void playLoadingEndSound(Level level, LivingEntity entity) {
        SoundSource source = entity instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.CROSSBOW_LOADING_END, source, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
    }

    @Override
    public int getChargeDurationMod(ItemStack crossbow, Level level) {
        Registry<Enchantment> enchantments = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        int quickCharge = crossbow.getEnchantmentLevel(enchantments.getOrThrow(Enchantments.QUICK_CHARGE));
        int base = quickCharge == 0 ? this.chargeTime : this.chargeTime - 2 * quickCharge;

        return applyChargeMultiplier(crossbow, base);
    }

    protected int applyChargeMultiplier(ItemStack crossbow, int base) {
        return Mth.ceil(base * getChargeMultiplier(crossbow));
    }

    private float getChargeMultiplier(ItemStack crossbow) {
        IVampirismQuarrel.IQuarrelBehavior behavior = getChargingBehavior(crossbow);
        return behavior != null ? behavior.properties().chargeMultiplier() : 1f;
    }

    @Nullable
    private IVampirismQuarrel.IQuarrelBehavior getChargingBehavior(ItemStack crossbow) {
        return getAmmunition(crossbow).map(HunterCrossbowItem::behaviorFor).orElse(null);
    }

    @Nullable
    private IVampirismQuarrel.IQuarrelBehavior getChargingBehavior(LivingEntity shooter, ItemStack crossbow) {
        IVampirismQuarrel.IQuarrelBehavior selected = getChargingBehavior(crossbow);
        if (selected != null) return selected;

        if (usesQuarrelPouch()) {
            ItemStack pouch = findLoadablePouch(shooter, crossbow);
            if (!pouch.isEmpty()) {
                return behaviorFor(matchingQuarrelInPouch(crossbow, pouch).getItem());
            }
        }
        return behaviorFor(shooter.getProjectile(crossbow).getItem());
    }

    @Nullable
    private static IVampirismQuarrel.IQuarrelBehavior behaviorFor(Item ammo) {
        if (ammo instanceof QuarrelItem quarrel) {
            return quarrel.getBehavior();
        }
        ItemStackTemplate contained = ammo.components().get(ModDataComponents.CONTAINED_PROJECTILES.get());
        if (contained != null && contained.item().value() instanceof QuarrelItem quarrel) {
            return quarrel.getBehavior();
        }

        return null;
    }

    protected boolean tryLoadProjectiles(LivingEntity shooter, ItemStack crossbow) {
        if (usesQuarrelPouch()) {
            ItemStack pouch = findLoadablePouch(shooter, crossbow);
            if (!pouch.isEmpty() && loadProjectiles(shooter, crossbow, pouch)) {
                return true;
            }
        }

        return loadProjectiles(shooter, crossbow, shooter.getProjectile(crossbow));
    }

    protected boolean loadProjectiles(LivingEntity shooter, ItemStack crossbow, ItemStack projectileStack) {
        List<ItemStack> drawn = drawMod(crossbow, projectileStack, shooter);
        if (drawn.isEmpty()) return false;

        ArrayList<ItemStack> projectiles = new ArrayList<>(crossbow.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).itemCopies());
        projectiles.addAll(drawn);
        crossbow.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.ofNonEmpty(projectiles));

        return true;
    }

    protected boolean usesQuarrelPouch() {
        return true;
    }

    protected ItemStack findLoadablePouch(LivingEntity shooter, ItemStack crossbow) {
        if (!(shooter instanceof Player player)) return ItemStack.EMPTY;

        for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
            if (stack.getItem() instanceof QuarrelPouchItem && !matchingQuarrelInPouch(crossbow, stack).isEmpty()) return stack;
        }

        return ItemStack.EMPTY;
    }

    protected ItemStack matchingQuarrelInPouch(ItemStack crossbow, ItemStack pouch) {
        QuarrelPouchContents contents = pouch.getOrDefault(ModDataComponents.QUARREL_POUCH_CONTENTS, QuarrelPouchContents.EMPTY);
        return getAmmunition(crossbow).map(contents::getSpecific).orElseGet(contents::getFirst);
    }

    protected List<ItemStack> getLoadingProjectiles(ItemStack crossbowStack, ItemStack projectileStack, LivingEntity shooter) {
        if (projectileStack.getItem() instanceof QuarrelPouchItem) {
            if (shooter.hasInfiniteMaterials()) {
                projectileStack = projectileStack.copy();
            }

            QuarrelPouchContents.Mutable contents = projectileStack.getOrDefault(ModDataComponents.QUARREL_POUCH_CONTENTS, QuarrelPouchContents.EMPTY).asMutable();
            Item selected = getAmmunition(crossbowStack).orElse(null);
            ItemStack quarrel = selected != null ? contents.getSpecific(selected) : contents.getFirst();
            if (quarrel.isEmpty()) {
                return List.of();
            }

            projectileStack.set(ModDataComponents.QUARREL_POUCH_CONTENTS, contents.toImmutable());

            return List.of(quarrel);
        }

        return List.of(projectileStack);
    }

    protected List<ItemStack> drawMod(ItemStack crossbowStack, ItemStack projectileStack, LivingEntity shooter) {
        if (projectileStack.isEmpty()) return List.of();
        return getLoadingProjectiles(crossbowStack, projectileStack, shooter).stream().map(projectile -> useAmmo(crossbowStack, projectile, shooter, false)).toList();
    }

    protected static ItemStack useAmmo(ItemStack crossbowStack, ItemStack projectileStack, LivingEntity shooter, boolean infinite) {
        boolean consume = !infinite && !(shooter.hasInfiniteMaterials() || (projectileStack.getItem() instanceof ArrowItem arrowItem && arrowItem.isInfinite(projectileStack, crossbowStack, shooter)));
        if (!consume) {
            ItemStack copy = projectileStack.copyWithCount(1);
            copy.set(DataComponents.INTANGIBLE_PROJECTILE, Unit.INSTANCE);

            return copy;
        }

        ItemStack used = projectileStack.split(1);
        if (projectileStack.isEmpty() && shooter instanceof Player player) {
            player.getInventory().removeItem(projectileStack);
        }

        return used;
    }

    @Override
    public boolean canSelectAmmunition(ItemStack crossbow) {
        return true;
    }

    public Collection<Item> getSelectableAmmo() {
        return QuarrelHandler.getQuarrels();
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

    @Override
    public ItemStack getDefaultCreativeAmmo(@Nullable Player player, ItemStack weapon) {
        return ModItems.QUARREL_NORMAL.get().getDefaultInstance();
    }

    public boolean testProjectile(ItemStack crossbow, ItemStack projectile) {
        if (projectile.getItem() instanceof IVampirismQuarrel<?>) {
            return testQuarrel(crossbow, projectile);
        }
        if (usesQuarrelPouch() && projectile.getItem() instanceof QuarrelPouchItem) {
            return !matchingQuarrelInPouch(crossbow, projectile).isEmpty();
        }

        return false;
    }

    public boolean testQuarrel(ItemStack crossbow, ItemStack quarrel) {
        return getAmmunition(crossbow).map(quarrel::is).orElse(true);
    }

    @Override
    public boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) {
        return EnchantmentOverride.isPrimary(stack, enchantment) || super.isPrimaryItemFor(stack, enchantment);
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return EnchantmentOverride.isPrimary(stack, enchantment) || super.supportsEnchantment(stack, enchantment);
    }

    /**
     * Slot-based handler over the loaded projectiles, backed by the vanilla {@link DataComponents#CHARGED_PROJECTILES}
     * component. Each slot holds a single bolt, the number of slots is the crossbow's {@link #getMaxLoadedProjectiles()}.
     */
    public static class ResourceHandler implements net.neoforged.neoforge.transfer.ResourceHandler<ItemResource> {

        private final ItemAccess itemAccess;
        private final HunterCrossbowItem crossbow;

        public ResourceHandler(ItemAccess itemAccess) {
            this.itemAccess = itemAccess;
            this.crossbow = (HunterCrossbowItem) itemAccess.getResource().getItem();
        }

        private List<ItemStack> items() {
            ChargedProjectiles charged = this.itemAccess.getResource().get(DataComponents.CHARGED_PROJECTILES);
            return charged == null ? List.of() : charged.itemCopies();
        }

        @Override
        public int size() {
            return this.crossbow.getMaxLoadedProjectiles();
        }

        @Override
        public ItemResource getResource(int index) {
            List<ItemStack> items = items();
            return index < 0 || index >= items.size() ? ItemResource.EMPTY : ItemResource.of(items.get(index));
        }

        @Override
        public long getAmountAsLong(int index) {
            List<ItemStack> items = items();
            return index < 0 || index >= items.size() ? 0 : items.get(index).getCount();
        }

        @Override
        public long getCapacityAsLong(int index, ItemResource resource) {
            return index >= 0 && index < size() ? 1 : 0;
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            return index >= 0 && index < size() && resource.is(holder -> holder.value() instanceof IVampirismQuarrel<?>);
        }

        @Override
        public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
            TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
            TransferPreconditions.checkNonNegative(index);
            if (!isValid(index, resource)) {
                return 0;
            }
            int accessAmount = this.itemAccess.getAmount();
            if (accessAmount <= 0) {
                return 0;
            }
            ItemResource accessResource = this.itemAccess.getResource();
            List<ItemStack> items = new ArrayList<>(items());
            // one bolt per slot, slots are filled contiguously
            if (index != items.size() || items.size() >= size()) {
                return 0;
            }
            items.add(resource.toStack(1));
            ItemResource newResource = accessResource.with(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.ofNonEmpty(items));
            return this.itemAccess.exchange(newResource, accessAmount, transaction) > 0 ? 1 : 0;
        }

        @Override
        public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
            TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
            TransferPreconditions.checkNonNegative(index);
            int accessAmount = this.itemAccess.getAmount();
            if (accessAmount <= 0) {
                return 0;
            }
            ItemResource accessResource = this.itemAccess.getResource();
            List<ItemStack> items = new ArrayList<>(items());
            if (index >= items.size() || !resource.matches(items.get(index))) {
                return 0;
            }
            items.remove(index);
            ItemResource newResource = items.isEmpty()
                    ? accessResource.without(DataComponents.CHARGED_PROJECTILES)
                    : accessResource.with(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.ofNonEmpty(items));
            return this.itemAccess.exchange(newResource, accessAmount, transaction) > 0 ? 1 : 0;
        }
    }
}
