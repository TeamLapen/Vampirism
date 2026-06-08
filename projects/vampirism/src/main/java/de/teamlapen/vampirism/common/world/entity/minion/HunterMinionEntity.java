package de.teamlapen.vampirism.common.world.entity.minion;

import com.google.common.collect.Lists;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionEntity;
import de.teamlapen.faction.api.factions.IFactionPredicate;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import de.teamlapen.faction.common.core.FactionMinionTasks;
import de.teamlapen.faction.common.factions.minions.MinionData;
import de.teamlapen.faction.common.factions.minions.MinionEntity;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.hunter.IHunter;
import de.teamlapen.vampirism.api.world.entity.hunter.IVampirismCrossbowUser;
import de.teamlapen.vampirism.api.world.items.IHunterCrossbow;
import de.teamlapen.vampirism.common.config.BalanceMobProps;
import de.teamlapen.vampirism.common.core.ModAttachments;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.tags.ModFactionTags;
import de.teamlapen.vampirism.common.world.entity.ai.goals.RangedHunterCrossbowAttackGoal;
import de.teamlapen.vampirism.common.world.entity.hunter.BasicHunterEntity;
import de.teamlapen.vampirism.common.world.entity.minion.management.MinionTasks;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.common.world.items.MinionUpgradeItem;
import de.teamlapen.vampirism.common.world.items.crossbow.TechCrossbowItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
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
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jetbrains.annotations.NotNull;

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

    public HunterMinionEntity(EntityType<? extends MinionEntity<?>> type, Level world) {
        super(type, world, IFactionPredicate.builder(ModFactions.HUNTER).targetFaction(ModFactionTags.HUNTER_MINION_TARGETS).build().or(e -> !(e instanceof IFactionEntity) && (e instanceof Enemy) && !(e instanceof Creeper)));
    }

    @Override
    public HunterMinionData createData() {
        return new HunterMinionData();
    }

    @Override
    public AttachmentType<?> getDataAttachmentType() {
        return ModAttachments.HUNTER_MINION_DATA.get();
    }

    @Override
    public @NotNull List<IMinionTask<?, ?>> getAvailableTasks() {
        return Lists.newArrayList(FactionMinionTasks.FOLLOW_LORD.get(), FactionMinionTasks.DEFEND_AREA.get(), FactionMinionTasks.STAY.get(), MinionTasks.COLLECT_HUNTER_ITEMS.get(), FactionMinionTasks.PROTECT_LORD.get());
    }

    public int getHunterType() {
        return this.getMinionData().map(d -> d.type).map(t -> Math.max(0, t)).orElse(0);
    }

    /**
     * @return Whether the selected skin is from the minion specific pool or a generic vampire skin
     */
    public boolean hasMinionSpecificSkin() {
        return this.getMinionData().map(d -> d.minionSkin).orElse(false);
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
    protected boolean canConsume(@NotNull ItemStack stack, @NotNull Consumable consumable) {
        if (!super.canConsume(stack, consumable)) return false;
        boolean fullHealth = this.getHealth() == this.getMaxHealth();
        FoodProperties foodProperties = stack.get(DataComponents.FOOD);
        return foodProperties == null || !fullHealth || foodProperties.canAlwaysEat();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(RAISED_ARM, false);
        builder.define(IS_CHARGING_CROSSBOW, false);

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
            if (heldItem.getItem() instanceof MinionUpgradeItem && IFaction.is(((MinionUpgradeItem) heldItem.getItem()).getFaction(), this.getFaction())) {
                if (this.minionData.level + 1 >= ((MinionUpgradeItem) heldItem.getItem()).getMinLevel() && this.minionData.level + 1 <= ((MinionUpgradeItem) heldItem.getItem()).getMaxLevel()) {
                    this.minionData.level++;
                    if (!player.getAbilities().instabuild) heldItem.shrink(1);
                    player.sendOverlayMessage(Component.translatable("dialogue.vampirism.hunter_minion.upgrade"));
                } else {
                    player.sendOverlayMessage(Component.translatable("dialogue.vampirism.hunter_minion.wrong_upgrade"));
                    sync();
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new RangedHunterCrossbowAttackGoal<>(this, 0.8, 60));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
    }

    private void updateAttackGoal() {
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
        return stack.getItem() instanceof TechCrossbowItem ? getLordOpt().flatMap(ILordPlayer::asSkillPlayer).map(x -> x.getSkillHandler().isSkillEnabled(HunterSkills.MINION_TECH_CROSSBOWS)).orElse(false) : true;
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
                return ModItems.QUARREL_CLIP.get().getDefaultInstance();
            } else {
                return ModItems.QUARREL_NORMAL.get().getDefaultInstance();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull Predicate<ItemStack> getEquipmentPredicate(EquipmentSlot slotType) {
        Predicate<ItemStack> predicate = super.getEquipmentPredicate(slotType);
        if (slotType == EquipmentSlot.MAINHAND) {
            predicate = predicate.and(stack -> !(stack.getItem() instanceof TechCrossbowItem) || getLord().flatMap(ILordPlayer::asSkillPlayer).map(s -> s.getSkillHandler().isSkillEnabled(HunterSkills.MINION_TECH_CROSSBOWS)).orElse(false));
        }
        return predicate;
    }

    public static class HunterMinionData extends MinionData {
        public static final Identifier ID = VIdentifier.mod("hunter");

        public static final int MAX_LEVEL = 6;
        public static final int MAX_LEVEL_INVENTORY = 2;
        public static final int MAX_LEVEL_HEALTH = 3;
        public static final int MAX_LEVEL_STRENGTH = 3;
        public static final int MAX_LEVEL_RESOURCES = 2;

        private int type;
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

        public HunterMinionData(String name, int type, boolean useLordSkin, boolean hasIncreasedStats) {
            super(name, 9);
            this.type = type;
            this.useLordSkin = useLordSkin;
            this.level = 0;
            this.minionSkin = false;
            this.hasIncreasedStats = hasIncreasedStats;
        }

        public HunterMinionData() {
            super();
        }

        @Override
        public void deserialize(@NotNull ValueInput input) {
            super.deserialize(input);
            type = input.getIntOr("hunter_type", 0);
            level = input.getIntOr("level", 0);
            useLordSkin = input.getBooleanOr("use_lord_skin", false);
            inventoryLevel = input.getIntOr("l_inv", 0);
            healthLevel = input.getIntOr("l_he", 0);
            strengthLevel = input.getIntOr("l_str", 0);
            resourceEfficiencyLevel = input.getIntOr("l_res", 0);
            minionSkin = input.getBooleanOr("ms", false);
            hasIncreasedStats = input.getBooleanOr("hasIncreasedStats", false);
        }

        @Override
        protected void registerProperties() {
            super.registerProperties();
            this.registerProperty(VIdentifier.mod("type")).simple(0, () -> type, x -> type = x);
            this.registerProperty(VIdentifier.mod("level")).simple(0, () -> level, x -> level = x);
            this.registerProperty(VIdentifier.mod("use_lord_skin")).simple(false, () -> useLordSkin, x -> useLordSkin = x);
            this.registerProperty(VIdentifier.mod("inventory_level")).simple(0, () -> inventoryLevel, x -> inventoryLevel = x);
            this.registerProperty(VIdentifier.mod("health_level")).simple(0, () -> healthLevel, x -> healthLevel = x);
            this.registerProperty(VIdentifier.mod("strength_level")).simple(0, () -> strengthLevel, x -> strengthLevel = x);
            this.registerProperty(VIdentifier.mod("resource_efficiency_level")).simple(0, () -> resourceEfficiencyLevel, x -> resourceEfficiencyLevel = x);
            this.registerProperty(VIdentifier.mod("minion_skin")).simple(false, () -> minionSkin, x -> minionSkin = x);
            this.registerProperty(VIdentifier.mod("has_increased_stats")).simple(false, () -> hasIncreasedStats, x -> hasIncreasedStats = x);
        }

        @Override
        public @NotNull MutableComponent getFormattedName() {
            return super.getFormattedName().withStyle(style -> style.withColor((ModFactions.HUNTER.get().getChatColor())));
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
        public void handleMinionAppearanceConfig(String newName, @NotNull List<Integer> data) {
            this.setName(newName);
            for (int i = 0; i < data.size(); i++) {
                switch (i) {
                    case 0 -> this.type = data.get(i);
                    case 1 -> {
                        this.useLordSkin = (data.get(i) & 0b1) == 1;
                        this.minionSkin = (data.get(i) & 0b10) == 0b10;
                    }
                }
            }
        }

        @Override
        public boolean hasUsedSkillPoints() {
            return this.inventoryLevel + this.healthLevel + this.strengthLevel + this.resourceEfficiencyLevel > 0;
        }

        @Override
        public void resetStats(@NotNull MinionEntity<?> entity) {
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
        public void serialize(@NotNull ValueOutput output) {
            super.serialize(output);
            output.putInt("hunter_type", type);
            output.putInt("level", level);
            output.putInt("l_inv", inventoryLevel);
            output.putInt("l_he", healthLevel);
            output.putInt("l_str", strengthLevel);
            output.putInt("l_res", resourceEfficiencyLevel);
            output.putBoolean("use_lord_skin", useLordSkin);
            output.putBoolean("ms", minionSkin);
            output.putBoolean("hasIncreasedStats", hasIncreasedStats);
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
         *
         * @param statId values: <br>
         *               -1: reset all stats <br>
         *               -2: update attributes <br>
         *               0: increases inventory level <br>
         *               1: increases health level <br>
         *               2: increases strength level <br>
         *               3: increases resource efficiency level <br>
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
        protected Identifier getDataType() {
            return ID;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setUseLordSkin(boolean useLordSkin) {
            this.useLordSkin = useLordSkin;
        }

        public boolean isUsingLordSkin() {
            return this.useLordSkin;
        }

    }
}
