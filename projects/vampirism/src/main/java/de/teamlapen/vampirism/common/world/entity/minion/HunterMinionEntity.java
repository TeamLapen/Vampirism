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
import de.teamlapen.faction.common.factions.minions.stats.MinionStat;
import de.teamlapen.faction.common.world.entities.appearance.AppearanceKey;
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

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
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

    @Override
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
                var clip = ModItems.ARROW_CLIP.get().getDefaultInstance();
                ModItems.ARROW_CLIP.get().addArrows(clip, Collections.nCopies(12, ModItems.CROSSBOW_ARROW_NORMAL.get().getDefaultInstance())); //Careful, all entries of the list are the same object, not copies
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
            predicate = predicate.and(stack -> !(stack.getItem() instanceof TechCrossbowItem) || getLord().flatMap(ILordPlayer::asSkillPlayer).map(s -> s.getSkillHandler().isSkillEnabled(HunterSkills.MINION_TECH_CROSSBOWS)).orElse(false));
        }
        return predicate;
    }

    public static class HunterMinionData extends MinionData {
        public static final Identifier ID = VIdentifier.mod("hunter");

        private static final Identifier INVENTORY_STATS_ID = VIdentifier.mod("inventory");
        private static final Identifier HEALTH_STATS_ID = VIdentifier.mod("health");
        private static final Identifier STRENGTH_STATS_ID = VIdentifier.mod("strength");
        private static final Identifier RESOURCES_STATS_ID = VIdentifier.mod("resources");
        public static final MinionStat<HunterMinionData> INVENTORY_STATS = new MinionStat<>(INVENTORY_STATS_ID, 2, Component.translatable("gui.vampirism.minion.stats.inventory_level")) {
            @Override
            public void apply(int level, MinionEntity<?> minion, HunterMinionData data) {
                int size = data.getDefaultInventorySize();
                data.getInventory().setAvailableSize(level == 1 ? size + 3 : (level == 2 ? size + 6 : size));
                if (level == 0) {
                    data.shrinkInventory(minion);
                }
            }

            @Override
            public String currentValue(MinionEntity<?> minion, MinionData data) {
                return String.valueOf(data.getInventory().getContainerSize());
            }
        };
        public static final MinionStat<HunterMinionData> HEALTH_STATS = new MinionStat<>(HEALTH_STATS_ID, 3, Component.translatable(Attributes.MAX_HEALTH.value().getDescriptionId())) {
            @Override
            public String currentValue(MinionEntity<?> minion, MinionData data) {
                return String.format("%.1f", minion.getAttribute(Attributes.MAX_HEALTH).getBaseValue());
            }
        };
        public static final MinionStat<HunterMinionData> STRENGTH_STATS = new MinionStat<>(STRENGTH_STATS_ID, 3, Component.translatable(Attributes.ATTACK_DAMAGE.value().getDescriptionId())) {
            @Override
            public String currentValue(MinionEntity<?> minion, MinionData data) {
                return String.format("%.1f", minion.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
            }
        };
        public static final MinionStat<HunterMinionData> RESOURCES_STATS = new MinionStat<>(RESOURCES_STATS_ID, 2, Component.translatable("gui.vampirism.minion.stats.resource_level")) {
            @Override
            public String currentValue(MinionEntity<?> minion, MinionData data) {
                return String.format("%.1f", (Math.ceil((float) (currentLevel(data) + 1) / (HunterMinionEntity.HunterMinionData.RESOURCES_STATS.getMaxLevel() + 1) * 100))) + "%";
            }
        };

        public static final int MAX_LEVEL = 6;

        private int type;
        private boolean useLordSkin;
        private boolean minionSkin;

        private int level;

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
        protected int getMaxStatLevel() {
            return MAX_LEVEL;
        }

        @Override
        protected void registerStats(Consumer<MinionStat<?>> consumer) {
            super.registerStats(consumer);
            consumer.accept(INVENTORY_STATS);
            consumer.accept(HEALTH_STATS);
            consumer.accept(STRENGTH_STATS);
            consumer.accept(RESOURCES_STATS);
        }

        @Override
        protected void registerProperties() {
            super.registerProperties();
            this.registerProperty(VIdentifier.mod("type")).simple(0, () -> type, x -> type = x);
            this.registerProperty(VIdentifier.mod("level")).simple(0, () -> level, x -> level = x);
            this.registerProperty(VIdentifier.mod("use_lord_skin")).simple(false, () -> useLordSkin, x -> useLordSkin = x);
            this.registerProperty(VIdentifier.mod("minion_skin")).simple(false, () -> minionSkin, x -> minionSkin = x);
            this.registerProperty(VIdentifier.mod("has_increased_stats")).simple(false, () -> hasIncreasedStats, x -> hasIncreasedStats = x);
        }

        @Override
        public @NotNull MutableComponent getFormattedName() {
            return super.getFormattedName().withStyle(style -> style.withColor((ModFactions.HUNTER.get().getChatColor())));
        }

        public int getHealthLevel() {
            return this.getStatLevel(HEALTH_STATS_ID);
        }

        public int getLevel() {
            return this.level;
        }

        public int getResourceEfficiencyLevel() {
            return getStatLevel(RESOURCES_STATS_ID);
        }

        public int getStrengthLevel() {
            return getStatLevel(STRENGTH_STATS_ID);
        }

        @Override
        public <T> void setAppearanceData(AppearanceKey<T> id, T data) {
            super.setAppearanceData(id, data);
            if (id.equals(SkinType)) {
                this.type = (Integer) data;
            } else if (id.equals(AppearanceType)) {
                int intData = (Integer) data;
                this.minionSkin = (intData & 0b10) == 0b10;
                this.useLordSkin = (intData & 0b1) == 1;
            }
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
