package de.teamlapen.vampirism.common.world.entity.minion;

import com.google.common.collect.Lists;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.IFactionEntity;
import de.teamlapen.factions.api.factions.IFactionPredicate;
import de.teamlapen.factions.api.world.entities.minion.IMinionTask;
import de.teamlapen.factions.common.core.FactionMinionTasks;
import de.teamlapen.factions.common.factions.minions.MinionData;
import de.teamlapen.factions.common.factions.minions.MinionEntity;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.event.BloodDrinkEvent;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.api.util.VampirismEventFactory;
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
import de.teamlapen.vampirism.common.world.entity.vampire.BasicVampireEntity;
import de.teamlapen.vampirism.common.world.items.MinionUpgradeItem;
import de.teamlapen.vampirism.common.world.items.component.BottleBlood;
import de.teamlapen.vampirism.common.world.items.consume.BloodFoodProperties;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jetbrains.annotations.NotNull;

import java.util.List;


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
        return (sundamageCache = Helper.gettingSundamge(this, iWorld));
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

    public void eat(@NotNull Level world, @NotNull ItemStack stack, BloodFoodProperties properties) {
        float healAmount = properties.blood() / 2f;
        this.heal(healAmount);
    }

    @Override
    public void eat(@NotNull Level world, @NotNull ItemStack stack, FoodProperties properties) {
    }

    @Override
    protected boolean canConsume(@NotNull ItemStack stack, @NotNull Consumable consumable) {
        if (!super.canConsume(stack, consumable)) return false;
        boolean fullHealth = this.getHealth() == this.getMaxHealth();
        BloodFoodProperties bloodFoodProperties = stack.get(ModDataComponents.VAMPIRE_FOOD);
        if (bloodFoodProperties != null && (!fullHealth || bloodFoodProperties.canAlwaysEat())) {
            return true;
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
                    player.displayClientMessage(Component.translatable("text.vampirism.vampire_minion.binding_upgrade"), false);
                    sync();
                } else {
                    player.displayClientMessage(Component.translatable("text.vampirism.vampire_minion.binding_wrong"), false);

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

    public void updateAttributes() {
        float statsMultiplier = this.getMinionData().filter(d -> d.hasIncreasedStats).map(a -> 1.2f).orElse(1f);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((BalanceMobProps.mobProps.MINION_MAX_HEALTH + BalanceMobProps.mobProps.MINION_MAX_HEALTH_PL * getMinionData().map(VampireMinionData::getHealthLevel).orElse(0)) * statsMultiplier);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((BalanceMobProps.mobProps.MINION_ATTACK_DAMAGE + BalanceMobProps.mobProps.MINION_ATTACK_DAMAGE_PL * getMinionData().map(VampireMinionData::getStrengthLevel).orElse(0)) * statsMultiplier);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((BalanceMobProps.mobProps.VAMPIRE_SPEED + 0.05 * getMinionData().map(VampireMinionData::getSpeedLevel).orElse(0)) * statsMultiplier);
    }

    public static class VampireMinionData extends MinionData {
        public static final Identifier ID = VResourceLocation.mod("vampire");

        public static final int MAX_LEVEL = 6;
        public static final int MAX_LEVEL_INVENTORY = 2;
        public static final int MAX_LEVEL_HEALTH = 3;
        public static final int MAX_LEVEL_STRENGTH = 3;
        public static final int MAX_LEVEL_SPEED = 3;
        private int type;
        private boolean useLordSkin;
        private boolean minionSkin;
        /**
         * Should be between 0 and {@link VampireMinionData#MAX_LEVEL}
         */
        private int level;
        private int inventoryLevel;
        private int healthLevel;
        private int strengthLevel;
        private int speedLevel;

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

        @Override
        public void deserialize(@NotNull ValueInput input) {
            super.deserialize(input);
            type = input.getIntOr("vampire_type", 0);
            level = input.getIntOr("level", 0);
            useLordSkin = input.getBooleanOr("use_lord_skin", false);
            inventoryLevel = input.getIntOr("l_inv", 0);
            healthLevel = input.getIntOr("l_he", 0);
            strengthLevel = input.getIntOr("l_str", 0);
            speedLevel = input.getIntOr("l_spe", 0);
            minionSkin = input.getBooleanOr("ms", false);
            hasIncreasedStats = input.getBooleanOr("hasIncreasedStats", false);
        }

        @Override
        protected void registerProperties() {
            super.registerProperties();
            registerProperty(VResourceLocation.mod("type")).simple(0, () -> type, t -> type = t);
            registerProperty(VResourceLocation.mod("level")).simple(0, () -> level, t -> level = t);
            registerProperty(VResourceLocation.mod("use_lord_skin")).simple(false, () -> useLordSkin, t -> useLordSkin = t);
            registerProperty(VResourceLocation.mod("inventory_level")).simple(0, () -> inventoryLevel, t -> inventoryLevel = t);
            registerProperty(VResourceLocation.mod("health_level")).simple(0, () -> healthLevel, t -> healthLevel = t);
            registerProperty(VResourceLocation.mod("strength_level")).simple(0, () -> strengthLevel, t -> strengthLevel = t);
            registerProperty(VResourceLocation.mod("speed_level")).simple(0, () -> speedLevel, t -> speedLevel = t);
            registerProperty(VResourceLocation.mod("minion_skin")).simple(false, () -> minionSkin, t -> minionSkin = t);
            registerProperty(VResourceLocation.mod("has_increased_stats")).simple(false, () -> hasIncreasedStats, t -> hasIncreasedStats = t);
        }

        @Override
        public @NotNull MutableComponent getFormattedName() {
            return super.getFormattedName().withStyle(style -> style.withColor(ModFactions.VAMPIRE.value().getChatColor()));
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
            return Math.max(0, this.level - inventoryLevel - healthLevel - speedLevel - strengthLevel);
        }

        public int getSpeedLevel() {
            return this.speedLevel;
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
            return this.inventoryLevel + this.healthLevel + this.strengthLevel + this.speedLevel > 0;
        }

        @Override
        public void resetStats(@NotNull MinionEntity<?> entity) {
            assert entity instanceof VampireMinionEntity;
            this.inventoryLevel = 0;
            this.healthLevel = 0;
            this.strengthLevel = 0;
            this.speedLevel = 0;
            this.getInventory().setAvailableSize(getInventorySize());
            ((VampireMinionEntity) entity).updateAttributes();
            super.resetStats(entity);
        }

        @Override
        public void serialize(@NotNull ValueOutput output) {
            super.serialize(output);
            output.putInt("vampire_type", type);
            output.putInt("level", level);
            output.putBoolean("use_lord_skin", useLordSkin);
            output.putInt("l_inv", inventoryLevel);
            output.putInt("l_he", healthLevel);
            output.putInt("l_str", strengthLevel);
            output.putInt("l_spe", speedLevel);
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

        @Override
        public boolean upgradeStat(int statId, @NotNull MinionEntity<?> entity) {
            if (super.upgradeStat(statId, entity)) return true;
            if (getRemainingStatPoints() == 0) {
                LOGGER.warn("Cannot upgrade minion stat as no stat points are left");
                return false;
            }
            assert entity instanceof VampireMinionEntity;
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
                    ((VampireMinionEntity) entity).updateAttributes();
                    return true;
                }
                case 2 -> {
                    if (strengthLevel >= MAX_LEVEL_STRENGTH) return false;
                    strengthLevel++;
                    ((VampireMinionEntity) entity).updateAttributes();
                    return true;
                }
                case 3 -> {
                    if (speedLevel >= MAX_LEVEL_SPEED) return false;
                    speedLevel++;
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
    }
}
