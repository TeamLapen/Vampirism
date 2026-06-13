package de.teamlapen.vampirism.common.world.entity.minion;

import com.google.common.collect.Lists;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionEntity;
import de.teamlapen.faction.api.factions.IFactionPredicate;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.faction.common.core.FactionMinionTasks;
import de.teamlapen.faction.common.factions.minions.MinionData;
import de.teamlapen.faction.common.factions.minions.MinionEntity;
import de.teamlapen.faction.common.factions.minions.stats.MinionStat;
import de.teamlapen.faction.common.world.items.consume.FactionFoodEntry;
import de.teamlapen.faction.common.world.items.consume.FactionFoodList;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.event.BloodDrinkEvent;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.util.VampirismEventFactory;
import de.teamlapen.faction.api.world.entities.ICustomizationHolder;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IDrinkBloodContext;
import de.teamlapen.vampirism.api.world.entity.vampire.IVampire;
import de.teamlapen.vampirism.common.config.BalanceMobProps;
import de.teamlapen.vampirism.common.core.*;
import de.teamlapen.vampirism.common.tags.ModFactionTags;
import de.teamlapen.vampirism.common.util.DamageHandler;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.world.attachments.ModDamageSources;
import de.teamlapen.vampirism.common.world.entity.ai.goals.FleeSunVampireGoal;
import de.teamlapen.vampirism.common.world.entity.ai.goals.RestrictSunVampireGoal;
import de.teamlapen.vampirism.common.world.entity.minion.management.MinionTasks;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.common.world.entity.vampire.BasicVampireEntity;
import de.teamlapen.vampirism.common.world.items.MinionUpgradeItem;
import de.teamlapen.vampirism.common.world.items.component.BottleBlood;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Consumer;


public class VampireMinionEntity extends MinionEntity<VampireMinionEntity.VampireMinionData> implements IVampire {

    public static AttributeSupplier.@NotNull Builder getAttributeBuilder() {
        return BasicVampireEntity.getAttributeBuilder();
    }

    private boolean sundamageCache;
    private @NotNull EnumStrength garlicCache = EnumStrength.NONE;

    public VampireMinionEntity(EntityType<? extends MinionEntity<?>> type, Level world) {
        super(type, world, IFactionPredicate.builder(ModFactions.VAMPIRE).targetFaction(ModFactionTags.VAMPIRE_MINION_TARGETS).build().or(e -> !(e instanceof IFactionEntity) && e instanceof Enemy && !(e instanceof Zombie) && !(e instanceof Skeleton) && !(e instanceof Creeper)));
    }

    @Override
    public VampireMinionData createData() {
        return new VampireMinionData();
    }

    @Override
    public AttachmentType<?> getDataAttachmentType() {
        return ModAttachments.VAMPIRE_MINION_DATA.get();
    }

    @Override
    public boolean doesResistGarlic(EnumStrength strength) {
        return false;
    }

    @Override
    public void drinkBlood(int amt, float saturationMod, boolean useRemaining, IDrinkBloodContext drinkContext) {
        BloodDrinkEvent.@NotNull EntityDrinkBloodEvent event = VampirismEventFactory.fireVampireDrinkBlood(this, amt, saturationMod, useRemaining, drinkContext);
        this.heal(event.getAmount() / 3f); //blood bottle = 900 amt = 9 amt = 2.5 health
    }

    @Override
    public @NotNull List<IMinionTask<?, ?>> getAvailableTasks() {
        return Lists.newArrayList(FactionMinionTasks.FOLLOW_LORD.get(), FactionMinionTasks.STAY.get(), FactionMinionTasks.DEFEND_AREA.get(), MinionTasks.COLLECT_BLOOD.get(), FactionMinionTasks.PROTECT_LORD.get());
    }

    public int getVampireType() {
        return this.getMinionData().map(d -> d.type).map(t -> Math.max(0, t)).orElse(0);
    }

    /**
     * @return Whether the selected skin is from the minion specific pool or a generic vampire skin
     */
    public boolean hasMinionSpecificSkin() {
        return this.getMinionData().map(d -> d.minionSkin).orElse(false);
    }

    @NotNull
    @Override
    public EnumStrength isGettingGarlicDamage(LevelAccessor iWorld, boolean forceRefresh) {
        if (forceRefresh) {
            garlicCache = Helper.getGarlicStrength(this, iWorld);
        }
        return garlicCache;
    }

    @Override
    public void aiStep() {
        if (this.tickCount % REFERENCE.REFRESH_GARLIC_TICKS == 3) {
            isGettingGarlicDamage(level(), true);
        }
        if (this.tickCount % REFERENCE.REFRESH_SUNDAMAGE_TICKS == 2) {
            isGettingSundamage(level(), true);
        }
        if (level() instanceof ServerLevel level) {
            if (isGettingSundamage(level()) && tickCount % 40 == 11) {
                double dmg = getAttribute(ModAttributes.SUNDAMAGE).getValue();
                dmg *= this.level().environmentAttributes().getValue(ModEnvironmentAttributes.SUN_INTENSITY.get(), this.position());

                if (dmg > 0) {
                    DamageHandler.hurtModded(level,this, ModDamageSources::sunDamage, (float) dmg);
                }
            }
            if (isGettingGarlicDamage(level()) != EnumStrength.NONE) {
                DamageHandler.affectVampireGarlicAmbient(this, isGettingGarlicDamage(level()), this.tickCount);
            }
            if (isAlive() && isInWater()) {
                setAirSupply(300);
                if (tickCount % 16 == 4) {
                    addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0));
                }
            }
        }
        super.aiStep();
    }

    @Override
    public boolean isGettingSundamage(LevelAccessor iWorld, boolean forceRefresh) {
        if (!forceRefresh) return sundamageCache;
        return (sundamageCache = Helper.gettingSunDamage(this, iWorld));
    }

    @Override
    public boolean isIgnoringSundamage() {
        return this.hasEffect(ModEffects.SUNSCREEN);
    }

    @Override
    public void openAppearanceScreen() {
        VampirismMod.proxy.displayVampireMinionAppearanceScreen(this);
    }

    @Override
    public void openStatsScreen() {
        VampirismMod.proxy.displayVampireMinionStatsaScreen(this);
    }

    public void setUseLordSkin(boolean useLordSkin) {
        this.getMinionData().ifPresent(d -> d.useLordSkin = useLordSkin);
    }

    public void setVampireType(int type, boolean minionSkin) {
        getMinionData().ifPresent(d -> {
            d.type = type;
            d.minionSkin = minionSkin;
        });
    }

    public boolean shouldRenderLordSkin() {
        return this.getMinionData().map(d -> d.useLordSkin).orElse(false);
    }

    @Override
    public boolean useBlood(int amt, boolean allowPartial) {
        return false;
    }

    @Override
    public boolean wantsBlood() {
        return false;
    }

    public void eat(int blood) {
        this.heal(blood / 2f);
    }

    public void eat(@NotNull Level world, @NotNull ItemStack stack, FoodProperties properties) {
        float healAmount = properties.nutrition() / 2f;
        this.heal(healAmount);
    }

    @Override
    protected boolean canConsume(@NotNull ItemStack stack, @NotNull Consumable consumable) {
        if (!super.canConsume(stack, consumable)) return false;
        boolean fullHealth = this.getHealth() == this.getMaxHealth();
        FactionFoodList factionFoodList = stack.get(FactionDataComponents.FACTION_FOOD);
        if (factionFoodList != null) {
            List<FactionFoodEntry> factionFoodEntries = factionFoodList.findMatchingEntries(this);
            if (!fullHealth || factionFoodEntries.stream().anyMatch(entry -> entry.foodProperties().canAlwaysEat())) {
                return true;
            }
        }
        BottleBlood bottleBlood = stack.get(ModDataComponents.BOTTLE_BLOOD);
        if (bottleBlood != null && bottleBlood.blood() > 0) {
            return true;
        }

        FoodProperties foodProperties = stack.get(DataComponents.FOOD);
        return foodProperties == null || foodProperties.canAlwaysEat();
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
                    player.sendOverlayMessage(Component.translatable("dialogue.vampirism.vampire_minion.upgrade"));
                    sync();
                } else {
                    player.sendOverlayMessage(Component.translatable("dialogue.vampirism.vampire_minion.wrong_upgrade"));

                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void onMinionDataReceived(@NotNull VampireMinionData data) {
        super.onMinionDataReceived(data);
        updateAttributes();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(3, new RestrictSunVampireGoal<>(this));
        this.goalSelector.addGoal(8, new FleeSunVampireGoal<>(this, 1, true));
    }

    @Override
    public void updateAttributes() {
        float statsMultiplier = this.getMinionData().filter(d -> d.hasIncreasedStats).map(a -> 1.2f).orElse(1f);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((BalanceMobProps.mobProps.MINION_MAX_HEALTH + BalanceMobProps.mobProps.MINION_MAX_HEALTH_PL * getMinionData().map(VampireMinionData::getHealthLevel).orElse(0)) * statsMultiplier);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((BalanceMobProps.mobProps.MINION_ATTACK_DAMAGE + BalanceMobProps.mobProps.MINION_ATTACK_DAMAGE_PL * getMinionData().map(VampireMinionData::getStrengthLevel).orElse(0)) * statsMultiplier);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((BalanceMobProps.mobProps.VAMPIRE_SPEED + 0.05 * getMinionData().map(VampireMinionData::getSpeedLevel).orElse(0)) * statsMultiplier);
    }

    public static class VampireMinionData extends MinionData {
        public static final Identifier ID = VIdentifier.mod("vampire");

        private static final Identifier INVENTORY_STATS_ID = VIdentifier.mod("inventory");
        private static final Identifier HEALTH_STATS_ID = VIdentifier.mod("health");
        private static final Identifier STRENGTH_STATS_ID = VIdentifier.mod("strength");
        private static final Identifier SPEED_STATS_ID = VIdentifier.mod("speed");
        public static final MinionStat<VampireMinionData> INVENTORY_STATS = new MinionStat<>(INVENTORY_STATS_ID, 2, Component.translatable("gui.vampirism.minion.stats.inventory_level")) {
            @Override
            public void apply(int level, MinionEntity<?> minion, VampireMinionData data) {
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
        public static final MinionStat<VampireMinionData> HEALTH_STATS = new MinionStat<>(HEALTH_STATS_ID, 3, Component.translatable(Attributes.MAX_HEALTH.value().getDescriptionId())){
            @Override
            public String currentValue(MinionEntity<?> minion, MinionData data) {
                return String.format("%.1f", minion.getAttribute(Attributes.MAX_HEALTH).getBaseValue());
            }
        };
        public static final MinionStat<VampireMinionData> STRENGTH_STATS = new MinionStat<>(STRENGTH_STATS_ID, 3, Component.translatable(Attributes.ATTACK_DAMAGE.value().getDescriptionId())){
            @Override
            public String currentValue(MinionEntity<?> minion, MinionData data) {
                return String.format("%.1f", minion.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
            }
        };
        public static final MinionStat<VampireMinionData> SPEED_STATS = new MinionStat<>(SPEED_STATS_ID, 3, Component.translatable(Attributes.MOVEMENT_SPEED.value().getDescriptionId())){
            @Override
            public String currentValue(MinionEntity<?> minion, MinionData data) {
                return String.format("%.1f", minion.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue());
            }
        };

        public static final int MAX_LEVEL = 6;
        private int type;
        private boolean useLordSkin;
        private boolean minionSkin;
        /**
         * Should be between 0 and {@link VampireMinionData#MAX_LEVEL}
         */
        private int level;

        private boolean hasIncreasedStats;

        public VampireMinionData(String name, int type, boolean useLordSkin, boolean hasIncreasedStats) {
            super(name, 9);
            this.type = type;
            this.useLordSkin = useLordSkin;
            this.level = 0;
            this.minionSkin = false;
            this.hasIncreasedStats = hasIncreasedStats;
        }

        public VampireMinionData() {
            super();
        }

        public VampireMinionData(ILordPlayer<?> player, ICustomizationHolder customizationHolder) {
            boolean skillEnabled = player.asSkillPlayer().map(ISkillPlayer::getSkillHandler).map(x -> x.isSkillEnabled(HunterSkills.MINION_STATS_INCREASE)).orElse(false);
            this("Minion", customizationHolder.getEntityTextureType(),false, skillEnabled);
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
            consumer.accept(SPEED_STATS);
        }

        @Override
        protected void registerProperties() {
            super.registerProperties();
            registerProperty(VIdentifier.mod("type")).simple(0, () -> type, t -> type = t);
            registerProperty(VIdentifier.mod("level")).simple(0, () -> level, t -> level = t);
            registerProperty(VIdentifier.mod("use_lord_skin")).simple(false, () -> useLordSkin, t -> useLordSkin = t);
            registerProperty(VIdentifier.mod("minion_skin")).simple(false, () -> minionSkin, t -> minionSkin = t);
            registerProperty(VIdentifier.mod("has_increased_stats")).simple(false, () -> hasIncreasedStats, t -> hasIncreasedStats = t);
        }

        @Override
        public @NotNull MutableComponent getFormattedName() {
            return super.getFormattedName().withStyle(style -> style.withColor(ModFactions.VAMPIRE.value().getChatColor()));
        }

        public int getHealthLevel() {
            return getStatLevel(HEALTH_STATS_ID);
        }

        public int getInventoryLevel() {
            return this.getStatLevel(INVENTORY_STATS_ID);
        }

        public int getLevel() {
            return this.level;
        }

        public int getSpeedLevel() {
            return this.getStatLevel(SPEED_STATS_ID);
        }

        public int getStrengthLevel() {
            return getStatLevel(STRENGTH_STATS_ID);
        }

        @Override
        public <T> void setAppearanceData(de.teamlapen.faction.common.world.entities.appearance.@NonNull AppearanceKey<T> id, @NonNull T data) {
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
