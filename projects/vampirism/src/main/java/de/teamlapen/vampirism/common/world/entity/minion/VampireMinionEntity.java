package de.teamlapen.vampirism.common.world.entity.minion;

import com.google.common.collect.Lists;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionEntity;
import de.teamlapen.faction.api.factions.IFactionPredicate;
import de.teamlapen.faction.api.world.entities.minion.MinionStat;
import de.teamlapen.faction.api.world.entities.minion.tasks.IMinionTask;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.faction.common.core.FactionMinionTasks;
import de.teamlapen.faction.common.factions.minions.MinionData;
import de.teamlapen.faction.common.factions.minions.MinionEntity;
import de.teamlapen.faction.common.world.items.consume.FactionFoodEntry;
import de.teamlapen.faction.common.world.items.consume.FactionFoodList;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.world.EnumStrength;
import de.teamlapen.vampirism.api.VampirismApi;
import de.teamlapen.vampirism.api.event.BloodDrinkEvent;
import de.teamlapen.vampirism.common.events.VampirismEventFactory;
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
import de.teamlapen.vampirism.common.world.entity.vampire.BasicVampireEntity;
import de.teamlapen.vampirism.common.world.items.MinionUpgradeItem;
import de.teamlapen.vampirism.common.world.items.component.BottleBlood;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class VampireMinionEntity extends MinionEntity implements IVampire {

    public static AttributeSupplier.@NotNull Builder getAttributeBuilder() {
        return BasicVampireEntity.getAttributeBuilder();
    }

    private boolean sundamageCache;
    private @NotNull EnumStrength garlicCache = EnumStrength.NONE;

    public VampireMinionEntity(EntityType<? extends MinionEntity> type, Level world) {
        super(type, world, IFactionPredicate.builder(ModFactions.VAMPIRE).targetFaction(ModFactionTags.VAMPIRE_MINION_TARGETS).build().or(e -> !(e instanceof IFactionEntity) && e instanceof Enemy && !(e instanceof Zombie) && !(e instanceof Skeleton) && !(e instanceof Creeper)));
    }

    @Override
    public MinionData createData() {
        return new MinionData(ModFactions.VAMPIRE_MINION.get());
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
    public @NotNull List<IMinionTask<?>> getAvailableTasks() {
        return Lists.newArrayList(FactionMinionTasks.FOLLOW_LORD.get(), FactionMinionTasks.STAY.get(), FactionMinionTasks.DEFEND_AREA.get(), FactionMinionTasks.PROTECT_LORD.get());
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
        return (sundamageCache = VampirismApi.services().sunDamageRegistry().isGettingSundamage(this, iWorld));
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
        VampirismMod.proxy.displayVampireMinionStatsScreen(this);
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
                if (this.minionData.getLevel() + 1 >= ((MinionUpgradeItem) heldItem.getItem()).getMinLevel() && this.minionData.getLevel() + 1 <= ((MinionUpgradeItem) heldItem.getItem()).getMaxLevel()) {
                    this.minionData.setLevel(this.minionData.getLevel() + 1);
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
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(3, new RestrictSunVampireGoal<>(this));
        this.goalSelector.addGoal(8, new FleeSunVampireGoal<>(this, 1, true));
    }

    @Override
    public void updateAttributes() {
        float statsMultiplier = this.getMinionData().filter(MinionData::hasIncreasedStats).map(a -> 1.2f).orElse(1f);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((BalanceMobProps.mobProps.MINION_MAX_HEALTH + BalanceMobProps.mobProps.MINION_MAX_HEALTH_PL * getMinionData().map(MinionStat.HEALTH_STATS::currentLevel).orElse(0)) * statsMultiplier);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((BalanceMobProps.mobProps.MINION_ATTACK_DAMAGE + BalanceMobProps.mobProps.MINION_ATTACK_DAMAGE_PL * getMinionData().map(MinionStat.STRENGTH_STATS::currentLevel).orElse(0)) * statsMultiplier);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((BalanceMobProps.mobProps.VAMPIRE_SPEED + 0.05 * getMinionData().map(MinionStat.SPEED_STATS::currentLevel).orElse(0)) * statsMultiplier);
    }
}
