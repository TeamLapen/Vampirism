package de.teamlapen.vampirism.common.world.entity.minion;

import com.google.common.collect.Lists;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionEntity;
import de.teamlapen.faction.api.factions.IFactionPredicate;
import de.teamlapen.faction.api.factions.skills.ISkillHandler;
import de.teamlapen.faction.api.world.entities.minion.tasks.IMinionTask;
import de.teamlapen.faction.common.core.FactionMinionTasks;
import de.teamlapen.faction.common.factions.minions.MinionData;
import de.teamlapen.faction.common.factions.minions.MinionEntity;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.world.entity.hunter.IHunter;
import de.teamlapen.vampirism.api.world.entity.hunter.IVampirismCrossbowUser;
import de.teamlapen.vampirism.api.world.items.IHunterCrossbow;
import de.teamlapen.vampirism.common.config.BalanceMobProps;
import de.teamlapen.vampirism.common.core.ModDataComponents;
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
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Predicate;


public class HunterMinionEntity extends MinionEntity implements IHunter, IVampirismCrossbowUser {

    /**
     * Used for holding a crossbow
     */
    private static final EntityDataAccessor<Boolean> RAISED_ARM = SynchedEntityData.defineId(HunterMinionEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(HunterMinionEntity.class, EntityDataSerializers.BOOLEAN);


    public static AttributeSupplier.@NotNull Builder getAttributeBuilder() {
        return BasicHunterEntity.getAttributeBuilder();
    }

    public HunterMinionEntity(EntityType<? extends MinionEntity> type, Level world) {
        super(type, world, IFactionPredicate.builder(ModFactions.HUNTER).targetFaction(ModFactionTags.HUNTER_MINION_TARGETS).build().or(e -> !(e instanceof IFactionEntity) && (e instanceof Enemy) && !(e instanceof Creeper)));
    }

    @Override
    public MinionData createData() {
        return new MinionData(ModFactions.HUNTER_MINION.get());
    }

    @Override
    public @NotNull List<IMinionTask<?>> getAvailableTasks() {
        return Lists.newArrayList(FactionMinionTasks.FOLLOW_LORD.get(), FactionMinionTasks.DEFEND_AREA.get(), FactionMinionTasks.STAY.get(), FactionMinionTasks.PROTECT_LORD.get());
    }

    @Override
    public void openAppearanceScreen() {
        VampirismMod.proxy.displayHunterMinionAppearanceScreen(this);
    }

    @Override
    public void openStatsScreen() {
        VampirismMod.proxy.displayHunterMinionStatsScreen(this);
    }

    @Override
    protected boolean canConsume(@NotNull ItemStack stack, @NotNull Consumable consumable) {
        if (!super.canConsume(stack, consumable)) return false;
        boolean fullHealth = this.getHealth() == this.getMaxHealth();
        FoodProperties foodProperties = stack.get(DataComponents.FOOD);
        return foodProperties == null || !fullHealth || foodProperties.canAlwaysEat();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(RAISED_ARM, false);
        builder.define(IS_CHARGING_CROSSBOW, false);

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

    @Override
    public void updateAttributes() {
        float statsMultiplier = this.getMinionData().filter(MinionData::hasIncreasedStats).map(a -> 1.2f).orElse(1f);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((BalanceMobProps.mobProps.MINION_MAX_HEALTH + BalanceMobProps.mobProps.MINION_MAX_HEALTH_PL * getMinionData().map(MinionData.HEALTH_STATS::currentLevel).orElse(0)) * statsMultiplier);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((BalanceMobProps.mobProps.MINION_ATTACK_DAMAGE + BalanceMobProps.mobProps.MINION_ATTACK_DAMAGE_PL * getMinionData().map(MinionData.STRENGTH_STATS::currentLevel).orElse(0)) * statsMultiplier);
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
        return !(stack.getItem() instanceof TechCrossbowItem) || getLordOpt().map(x -> ISkillHandler.isSkillEnabled(x.asEntity(), HunterSkills.MINION_TECH_CROSSBOWS)).orElse(false);
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
            predicate = predicate.and(stack -> !(stack.getItem() instanceof TechCrossbowItem) || getLord().map(x -> ISkillHandler.isSkillEnabled(x.asEntity(), HunterSkills.MINION_TECH_CROSSBOWS)).orElse(false));
        }
        return predicate;
    }
}
