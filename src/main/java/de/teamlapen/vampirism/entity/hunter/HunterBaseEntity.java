package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.advancements.critereon.VampireActionCriterionTrigger;
import de.teamlapen.vampirism.api.entity.hunter.IHunterMob;
import de.teamlapen.vampirism.core.ModAdvancements;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for all vampire hunter
 */
public abstract class HunterBaseEntity extends VampirismEntity implements IHunterMob, Npc/*mainly for JourneyMap*/ {

    public static boolean spawnPredicateHunter(@NotNull EntityType<? extends HunterBaseEntity> entityType, @NotNull LevelAccessor world, MobSpawnType spawnReason, @NotNull BlockPos blockPos, RandomSource random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && Mob.checkMobSpawnRules(entityType, world, spawnReason, blockPos, random);
    }

    protected final int MOVE_TO_RESTRICT_PRIO = 3;
    private final boolean countAsMonster;

    public HunterBaseEntity(EntityType<? extends HunterBaseEntity> type, Level world, boolean countAsMonster) {
        super(type, world);
        this.countAsMonster = countAsMonster;
    }

    @Override
    public MobCategory getClassification(boolean forSpawnCount) {
        if (forSpawnCount && countAsMonster) {
            return MobCategory.MONSTER;
        }
        return super.getClassification(forSpawnCount);
    }

    @Override
    public @NotNull LivingEntity getRepresentingEntity() {
        return this;
    }

    @Override
    public void die(@NotNull DamageSource cause) {
        super.die(cause);
        if (cause.getEntity() instanceof ServerPlayer && Helper.isVampire(((Player) cause.getEntity())) && this.getEffect(ModEffects.FREEZE.get()) != null) {
            ModAdvancements.TRIGGER_VAMPIRE_ACTION.trigger(((ServerPlayer) cause.getEntity()), VampireActionCriterionTrigger.Action.KILL_FROZEN_HUNTER);
        }
    }

    public void makeCampHunter(@NotNull BlockPos pos) {
        super.setHome(new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).inflate(10));
        this.setMoveTowardsRestriction(MOVE_TO_RESTRICT_PRIO, true);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
    }

    /**
     * Tries to cure sanguinare and if successful sends a message.
     *
     * @return If player was cured
     */
    protected boolean tryCureSanguinare(@NotNull Player entity) {
        if (!this.level().isClientSide && entity.hasEffect(ModEffects.SANGUINARE.get())) {
            entity.removeEffect(ModEffects.SANGUINARE.get());
            entity.sendSystemMessage(Component.translatable("text.vampirism.hunter.cured_sanguinare"));
            return true;
        }
        return false;
    }
}
