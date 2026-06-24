package de.teamlapen.vampirism.common.world.items.crossbow;

import de.teamlapen.vampirism.api.world.items.IEntityCrossbowArrow;
import de.teamlapen.vampirism.api.world.items.IHunterCrossbow;
import de.teamlapen.vampirism.api.world.items.IVampirismCrossbowArrow;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.tags.ModItemTags;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.common.world.items.component.QuarrelPouchContents;
import de.teamlapen.vampirism.common.world.items.component.SelectedAmmunition;
import de.teamlapen.vampirism.common.world.items.crossbow.arrow.ArrowContainer;
import de.teamlapen.vampirism.common.world.items.crossbow.arrow.QuarrelPouch;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
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
    private boolean startSoundPlayed = false;
    private boolean midLoadSoundPlayed = false;

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

    /**
     * @return the number of projectiles currently loaded into the crossbow
     */
    public static int getLoadedCount(ItemStack crossbow) {
        return crossbow.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).items().size();
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
                List<ItemStack> availableProjectiles = new ArrayList<>(chargedprojectiles.itemCopies());
                int loadedBefore = availableProjectiles.size();
                List<ItemStack> arrows = getShootingProjectiles(serverLevel, crossbow, availableProjectiles);
                int consumed = loadedBefore - availableProjectiles.size();
                ItemStack otherStack = shooter.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
                this.shoot(serverLevel, shooter, hand, crossbow, arrows, speed, inacurracy * getInaccuracy(crossbow, otherStack.getItem() instanceof IHunterCrossbow), shooter instanceof Player, p_331602_);
                onShoot(shooter, crossbow);
                consumeLoadedProjectiles(crossbow, consumed);

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

    /**
     * Mirrors {@link CrossbowItem#onUseTick} but calls our own {@link #tryLoadProjectiles} (which loads via the
     * crossbow's {@link ResourceHandler}) instead of the vanilla private static loading, and additionally charges the
     * off-hand crossbow when the {@code DOUBLE_IT} skill is active. Vanilla loads inside {@code onUseTick}, so without
     * this override the bolts would be drawn with vanilla logic before our loading code ever runs.
     */
    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack itemStack, int ticksRemaining) {
        if (!level.isClientSide()) {
            int useDuration = this.getUseDuration(itemStack, entity);
            int combinedUseDuration = this.getCombinedUseDuration(itemStack, entity, entity.getUsedItemHand());
            int combinedChargingDuration = combinedUseDuration - ticksRemaining;
            int chargingDuration = useDuration - ticksRemaining;
            if (combinedChargingDuration != useDuration) {
                chargingDuration += combinedUseDuration - useDuration;
            }
            float chargePercent = (float) chargingDuration / getChargeDurationMod(itemStack, level);

            if (chargePercent < 0.2F) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
            }
            if (chargePercent >= 0.2F && !this.startSoundPlayed) {
                this.startSoundPlayed = true;
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.CROSSBOW_LOADING_START, SoundSource.PLAYERS, 0.5F, 1.0F);
            }
            if (chargePercent >= 0.5F && !this.midLoadSoundPlayed) {
                this.midLoadSoundPlayed = true;
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.CROSSBOW_LOADING_MIDDLE, SoundSource.PLAYERS, 0.5F, 1.0F);
            }
            if (chargePercent >= 1.0F && !CrossbowItem.isCharged(itemStack) && tryLoadProjectiles(entity, itemStack)) {
                ItemStack otherStack = entity.getItemInHand(entity.getUsedItemHand() == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
                if (canUseDoubleCrossbow(entity) && (float) combinedChargingDuration / getCombinedChargeDurationMod(itemStack, entity, entity.getUsedItemHand()) >= 1f && otherStack.getItem() instanceof HunterCrossbowItem && !CrossbowItem.isCharged(otherStack)) {
                    tryLoadProjectiles(entity, otherStack);
                }
                SoundSource source = entity instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE;
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.CROSSBOW_LOADING_END, source, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
            }
        }
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

    protected boolean tryLoadProjectiles(LivingEntity shooter, ItemStack crossbow) {
        ItemStack ammo = shooter.getProjectile(crossbow);
        if (ammo.isEmpty()) {
            return false;
        }
        int capacityLeft = getMaxLoadedProjectiles() - getLoadedCount(crossbow);
        if (capacityLeft <= 0) {
            return false;
        }
        var crossbowHandler = crossbow.getCapability(Capabilities.Item.ITEM, ItemAccess.forStack(crossbow));
        if (crossbowHandler == null) {
            return false;
        }

        Predicate<ItemResource> filter = res -> res.is(holder -> holder.value() instanceof IVampirismCrossbowArrow<?>) && getAmmunition(crossbow).map(res::is).orElse(true);
        boolean infinite = shooter.hasInfiniteMaterials() || (ammo.getItem() instanceof ArrowItem arrowItem && arrowItem.isInfinite(ammo, crossbow, shooter));

        try (var transaction = Transaction.openRoot()) {
            int loaded;
            if (infinite) {
                // Creative / infinite: don't consume the source, just fill with intangible copies that won't drop when fired.
                ItemResource source = resolveInfiniteResource(ammo, filter, transaction);
                if (source == null) {
                    return false;
                }
                loaded = ResourceHandlerUtil.insertStacking(crossbowHandler, source.with(DataComponents.INTANGIBLE_PROJECTILE, Unit.INSTANCE), capacityLeft, transaction);
            } else {
                var ammoHandler = ammo.getCapability(Capabilities.Item.ITEM, ItemAccess.forStack(ammo));
                if (ammoHandler != null) {
                    // Quarrel pouch / arrow clip: move bolts from the container's handler into the crossbow.
                    loaded = ResourceHandlerUtil.move(ammoHandler, crossbowHandler, filter, capacityLeft, transaction);
                } else {
                    // Plain arrow stack held in the inventory.
                    ItemResource res = ItemResource.of(ammo);
                    if (!filter.test(res)) {
                        return false;
                    }
                    loaded = ResourceHandlerUtil.insertStacking(crossbowHandler, res, Math.min(capacityLeft, ammo.getCount()), transaction);
                    if (loaded > 0) {
                        ammo.shrink(loaded);
                        if (ammo.isEmpty() && shooter instanceof Player player) {
                            player.getInventory().removeItem(ammo);
                        }
                    }
                }
            }
            if (loaded > 0) {
                transaction.commit();
                return true;
            }
        }
        return false;
    }

    /**
     * Resolves which arrow resource to load for creative / infinite shooters without consuming the source.
     */
    @Nullable
    protected ItemResource resolveInfiniteResource(ItemStack ammo, Predicate<ItemResource> filter, @Nullable Transaction transaction) {
        var ammoHandler = ammo.getCapability(Capabilities.Item.ITEM, ItemAccess.forStack(ammo));
        if (ammoHandler != null) {
            ItemResource found = ResourceHandlerUtil.findExtractableResource(ammoHandler, filter, transaction);
            return found == null || found.isEmpty() ? null : found;
        }
        ItemResource res = ItemResource.of(ammo);
        return filter.test(res) ? res : null;
    }

    /**
     * Removes the given number of loaded projectiles from the front of the crossbow's resource handler.
     */
    protected void consumeLoadedProjectiles(ItemStack crossbow, int count) {
        if (count <= 0) {
            return;
        }
        var handler = crossbow.getCapability(Capabilities.Item.ITEM, ItemAccess.forStack(crossbow));
        if (handler == null) {
            return;
        }
        try (var transaction = Transaction.openRoot()) {
            for (int i = 0; i < count; i++) {
                ItemResource res = handler.getResource(0);
                if (res.isEmpty()) {
                    break;
                }
                handler.extract(0, res, 1, transaction);
            }
            transaction.commit();
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
        QuarrelPouchContents.Mutable mutable = orDefault.asMutable();
        ItemStack stack = getAmmunition(crossbow).map(mutable::getSpecific).orElseGet(mutable::getFirst);
        return testProjectile(crossbow, stack);
    }

    public boolean testQuarrel(ItemStack crossbow, ItemStack quarrel) {
        return getAmmunition(crossbow).map(quarrel::is).orElse(true);
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
            return index >= 0 && index < size() && resource.is(holder -> holder.value() instanceof IVampirismCrossbowArrow<?>);
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
