package de.teamlapen.vampirism.entity.minion;

import com.google.common.collect.Lists;
import de.teamlapen.lib.HelperLib;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.api.entity.hunter.IVampirismCrossbowUser;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.items.IHunterCrossbow;
import de.teamlapen.vampirism.api.items.IVampirismCrossbow;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.ai.goals.AttackRangedCrossbowGoal;
import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.items.MinionUpgradeItem;
import de.teamlapen.vampirism.items.crossbow.TechCrossbowItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;


public class HunterMinionEntity extends MinionEntity<HunterMinionEntity.HunterMinionData> implements IHunter, IVampirismCrossbowUser {

    /**
     * Used for holding a crossbow
     */
    private static final EntityDataAccessor<Boolean> RAISED_ARM = SynchedEntityData.defineId(HunterMinionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(HunterMinionEntity.class, EntityDataSerializers.BOOLEAN);


    public static AttributeSupplier.@NotNull Builder getAttributeBuilder() {
        return BasicHunterEntity.getAttributeBuilder();
    }

    public HunterMinionEntity(EntityType<? extends VampirismEntity> type, Level world) {
        super(type, world, VampirismAPI.factionRegistry().getPredicate(VReference.HUNTER_FACTION, true, true, false, false, null).or(e -> !(e instanceof IFactionEntity) && (e instanceof Enemy) && !(e instanceof Creeper)));
    }

    @Override
    public @NotNull List<IMinionTask<?, ?>> getAvailableTasks() {
        return Lists.newArrayList(MinionTasks.FOLLOW_LORD.get(), MinionTasks.DEFEND_AREA.get(), MinionTasks.STAY.get(), MinionTasks.COLLECT_HUNTER_ITEMS.get(), MinionTasks.PROTECT_LORD.get());
    }

    public void setHatType(int type) {
        assert type >= -1;
        this.getMinionData().ifPresent(d -> d.hat = type);
    }

    public int getHunterType() {
        return this.getMinionData().map(d -> d.type).map(t -> Math.max(0, t)).orElse(0);
    }

    @Override
    public @NotNull LivingEntity getRepresentingEntity() {
        return this;
    }

    /**
     * @return Whether the selected skin is from the minion specific pool or a generic vampire skin
     */
    public boolean hasMinionSpecificSkin() {
        return this.getMinionData().map(d -> d.minionSkin).orElse(false);
    }

    public int getHatType() {
        return this.getItemBySlot(EquipmentSlot.HEAD).isEmpty() ? this.getMinionData().map(d -> d.hat).orElse(0) : -1;
    }

    @Override
    public void openAppearanceScreen() {
        VampirismMod.proxy.displayHunterMinionAppearanceScreen(this);
    }

    @Override
    public void openStatsScreen() {
        VampirismMod.proxy.displayHunterMinionStatsScreen(this);
    }

    public void setHunterType(int type, boolean minionSkin) {
        getMinionData().ifPresent(d -> {
            d.type = type;
            d.minionSkin = minionSkin;
        });
    }

    public void setUseLordSkin(boolean useLordSkin) {
        this.getMinionData().ifPresent(d -> d.useLordSkin = useLordSkin);
    }

    public boolean shouldRenderLordSkin() {
        return this.getMinionData().map(d -> d.useLordSkin).orElse(false);
    }

    @Override
    protected boolean canConsume(@NotNull ItemStack stack) {
        if (!super.canConsume(stack)) return false;
        boolean fullHealth = this.getHealth() == this.getMaxHealth();
        return !stack.isEdible() || !fullHealth || stack.getItem().getFoodProperties(stack, this).canAlwaysEat();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(RAISED_ARM, false);
        this.getEntityData().define(IS_CHARGING_CROSSBOW, false);

    }

    @Override
    protected void onMinionDataReceived(@NotNull HunterMinionData data) {
        super.onMinionDataReceived(data);
        this.updateAttackGoal();
        this.updateAttributes();
    }

    @NotNull
    @Override
    protected InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (!this.level().isClientSide() && isLord(player) && minionData != null) {
            ItemStack heldItem = player.getItemInHand(hand);
            if (heldItem.getItem() instanceof MinionUpgradeItem && ((MinionUpgradeItem) heldItem.getItem()).getFaction() == this.getFaction()) {
                if (this.minionData.level + 1 >= ((MinionUpgradeItem) heldItem.getItem()).getMinLevel() && this.minionData.level + 1 <= ((MinionUpgradeItem) heldItem.getItem()).getMaxLevel()) {
                    this.minionData.level++;
                    if (!player.getAbilities().instabuild) heldItem.shrink(1);
                    player.displayClientMessage(Component.translatable("text.vampirism.hunter_minion.equipment_upgrade"), false);
                    HelperLib.sync(this);
                } else {
                    player.displayClientMessage(Component.translatable("text.vampirism.hunter_minion.equipment_wrong"), false);

                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new AttackRangedCrossbowGoal<>(this, 0.8, 60));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
    }

    private void updateAttackGoal() {
        if (this.level().isClientSide()) return;
    }

    public void updateAttributes() {
        float statsMultiplier = this.getMinionData().filter(d -> d.hasIncreasedStats).map(a -> 1.2f).orElse(1f);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((BalanceMobProps.mobProps.MINION_MAX_HEALTH + BalanceMobProps.mobProps.MINION_MAX_HEALTH_PL * getMinionData().map(HunterMinionData::getHealthLevel).orElse(0)) * statsMultiplier);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((BalanceMobProps.mobProps.MINION_ATTACK_DAMAGE + BalanceMobProps.mobProps.MINION_ATTACK_DAMAGE_PL * getMinionData().map(HunterMinionData::getStrengthLevel).orElse(0)) * statsMultiplier);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_HUNTER_SPEED * statsMultiplier);
    }

    @Override
    public void setChargingCrossbow(boolean p_213671_1_) {
        this.getEntityData().set(IS_CHARGING_CROSSBOW, p_213671_1_);
    }

    @Override
    public void shootCrossbowProjectile(LivingEntity p_32328_, ItemStack p_32329_, Projectile p_32330_, float p_32331_) {
        this.shootCrossbowProjectile(this, p_32328_, p_32330_, p_32331_, 1.6f);
    }

    @Override
    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    @Override
    public void performRangedAttack(LivingEntity p_82196_1_, float p_82196_2_) {
        this.performCrossbowAttack(this, 1.6f);
    }

    @Override
    public boolean isHoldingCrossbow() {
        return this.isHolding(stack -> stack.getItem() instanceof IHunterCrossbow);
    }

    @Override
    public boolean canUseCrossbow(ItemStack stack) {
        return stack.getItem() instanceof TechCrossbowItem ? getLordOpt().map(p -> HunterPlayer.get(p.getPlayer()).getSkillHandler().isSkillEnabled(HunterSkills.MINION_TECH_CROSSBOWS.get())).orElse(false) : true;
    }

    @Override
    public boolean isChargingCrossbow() {
        return this.getEntityData().get(IS_CHARGING_CROSSBOW);
    }

    @NotNull
    @Override
    public ItemStack getProjectile(ItemStack stack) {
        if (stack.getItem() instanceof IHunterCrossbow) {
            if (stack.getItem() instanceof TechCrossbowItem) {
                var clip = ModItems.ARROW_CLIP.get().getDefaultInstance();
                ModItems.ARROW_CLIP.get().addArrows(clip, Collections.nCopies(12, ModItems.CROSSBOW_ARROW_NORMAL.get().getDefaultInstance()));
                return clip;
            } else {
                return ModItems.CROSSBOW_ARROW_NORMAL.get().getDefaultInstance();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull Predicate<ItemStack> getEquipmentPredicate(EquipmentSlot slotType) {
        Predicate<ItemStack> predicate = super.getEquipmentPredicate(slotType);
        if (slotType == EquipmentSlot.MAINHAND) {
            predicate = predicate.and(stack -> !(stack.getItem() instanceof TechCrossbowItem) || HunterPlayer.get(getLord().getPlayer()).getSkillHandler().isSkillEnabled(HunterSkills.MINION_TECH_CROSSBOWS.get()));
        }
        return predicate;
    }

    public static class HunterMinionData extends MinionData {
        public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "hunter");

        public static final int MAX_LEVEL = 6;
        public static final int MAX_LEVEL_INVENTORY = 2;
        public static final int MAX_LEVEL_HEALTH = 3;
        public static final int MAX_LEVEL_STRENGTH = 3;
        public static final int MAX_LEVEL_RESOURCES = 2;

        private int type;
        private int hat;
        private boolean useLordSkin;
        private boolean minionSkin;

        /**
         * Should be between 0 and {@link HunterMinionData#MAX_LEVEL}
         */
        private int level;
        private int inventoryLevel;
        private int healthLevel;
        private int strengthLevel;
        private int resourceEfficiencyLevel;

        private boolean hasIncreasedStats;

        public HunterMinionData(String name, int type, int hat, boolean useLordSkin, boolean hasIncreasedStats) {
            super(name, 9);
            this.type = type;
            this.hat = hat;
            this.useLordSkin = useLordSkin;
            this.level = 0;
            this.minionSkin = false;
            this.hasIncreasedStats = hasIncreasedStats;
        }

        public HunterMinionData() {
            super();
        }

        @Override
        public void deserializeNBT(@NotNull CompoundTag nbt) {
            super.deserializeNBT(nbt);
            type = nbt.getInt("hunter_type");
            hat = nbt.getInt("hunter_hat");
            level = nbt.getInt("level");
            useLordSkin = nbt.getBoolean("use_lord_skin");
            inventoryLevel = nbt.getInt("l_inv");
            healthLevel = nbt.getInt("l_he");
            strengthLevel = nbt.getInt("l_str");
            resourceEfficiencyLevel = nbt.getInt("l_res");
            minionSkin = nbt.getBoolean("ms");
            hasIncreasedStats = nbt.getBoolean("hasIncreasedStats");
        }

        @Override
        public @NotNull MutableComponent getFormattedName() {
            return super.getFormattedName().withStyle(style -> style.withColor((VReference.HUNTER_FACTION.getChatColor())));
        }

        public int getHealthLevel() {
            return healthLevel;
        }

        public int getInventoryLevel() {
            return this.inventoryLevel;
        }

        @Override
        public int getInventorySize() {
            int size = getDefaultInventorySize();
            return inventoryLevel == 1 ? size + 3 : (inventoryLevel == 2 ? size + 6 : size);
        }

        public int getLevel() {
            return this.level;
        }

        public int getRemainingStatPoints() {
            return Math.max(0, this.level - inventoryLevel - healthLevel - resourceEfficiencyLevel - strengthLevel);
        }

        public int getResourceEfficiencyLevel() {
            return resourceEfficiencyLevel;
        }

        public int getStrengthLevel() {
            return strengthLevel;
        }

        @Override
        public void handleMinionAppearanceConfig(String newName, int @NotNull ... data) {
            this.setName(newName);
            if (data.length >= 3) {
                type = data[0];
                hat = data[1];
                this.useLordSkin = (data[2] & 0b1) == 1;
                this.minionSkin = (data[2] & 0b10) == 0b10;
            }
        }

        @Override
        public boolean hasUsedSkillPoints() {
            return this.inventoryLevel + this.healthLevel + this.strengthLevel + this.resourceEfficiencyLevel > 0;
        }

        @Override
        public void resetStats(MinionEntity<?> entity) {
            assert entity instanceof HunterMinionEntity;
            this.inventoryLevel = 0;
            this.healthLevel = 0;
            this.strengthLevel = 0;
            this.resourceEfficiencyLevel = 0;
            this.shrinkInventory(entity);
            ((HunterMinionEntity) entity).updateAttributes();
            super.resetStats(entity);
        }

        @Override
        public void serializeNBT(@NotNull CompoundTag tag) {
            super.serializeNBT(tag);
            tag.putInt("hunter_type", type);
            tag.putInt("hunter_hat", hat);
            tag.putInt("level", level);
            tag.putInt("l_inv", inventoryLevel);
            tag.putInt("l_he", healthLevel);
            tag.putInt("l_str", strengthLevel);
            tag.putInt("l_res", resourceEfficiencyLevel);
            tag.putBoolean("use_lord_skin", useLordSkin);
            tag.putBoolean("ms", minionSkin);
            tag.putBoolean("hasIncreasedStats", hasIncreasedStats);
        }

        /**
         * @param level 0, 1 or 2
         * @return If the new level is higher than the old
         */
        public boolean setLevel(int level) {
            if (level < 0 || level > MAX_LEVEL) return false;
            boolean levelup = level > this.level;
            this.level = level;
            return levelup;
        }

        /**
         * Called on server side to upgrade a stat of the given id
         * <p>
         * @param  statId values: <br>
         * -1: reset all stats <br>
         * -2: update attributes <br>
         * 0: increases inventory level <br>
         * 1: increases health level <br>
         * 2: increases strength level <br>
         * 3: increases resource efficiency level <br>
         *
         * @return if attributes where changed and a sync is required
         */
        @Override
        public boolean upgradeStat(int statId, @NotNull MinionEntity<?> entity) {
            if (super.upgradeStat(statId, entity)) return true;
            if (getRemainingStatPoints() == 0) {
                LOGGER.warn("Cannot upgrade minion stat as no stat points are left");
                return false;
            }
            assert entity instanceof HunterMinionEntity;
            switch (statId) {
                case 0 -> {
                    if (inventoryLevel >= MAX_LEVEL_INVENTORY) return false;
                    inventoryLevel++;
                    this.getInventory().setAvailableSize(getInventorySize());
                    return true;
                }
                case 1 -> {
                    if (healthLevel >= MAX_LEVEL_HEALTH) return false;
                    healthLevel++;
                    ((HunterMinionEntity) entity).updateAttributes();
                    entity.setHealth(entity.getMaxHealth());
                    return true;
                }
                case 2 -> {
                    if (strengthLevel >= MAX_LEVEL_STRENGTH) return false;
                    strengthLevel++;
                    ((HunterMinionEntity) entity).updateAttributes();
                    return true;
                }
                case 3 -> {
                    if (resourceEfficiencyLevel >= MAX_LEVEL_RESOURCES) return false;
                    resourceEfficiencyLevel++;
                    return true;
                }
                default -> {
                    LOGGER.warn("Cannot upgrade minion stat {} as it does not exist", statId);
                    return false;
                }
            }
        }

        public void setIncreasedStats(boolean hasIncreasedStats) {
            this.hasIncreasedStats = hasIncreasedStats;
        }

        @Override
        protected ResourceLocation getDataType() {
            return ID;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setUseLordSkin(boolean useLordSkin) {
            this.useLordSkin = useLordSkin;
        }

        public void setHat(int hat) {
            this.hat = hat;
        }
    }
}
