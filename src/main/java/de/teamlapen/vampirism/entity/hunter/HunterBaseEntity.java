package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.advancements.VampireActionTrigger;
import de.teamlapen.vampirism.api.entity.hunter.IHunterMob;
import de.teamlapen.vampirism.core.ModAdvancements;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Base class for all vampire hunter
 */
public abstract class HunterBaseEntity extends VampirismEntity implements IHunterMob, INPC/*mainly for JourneyMap*/ {

    public static boolean spawnPredicateHunter(EntityType<? extends HunterBaseEntity> entityType, IWorld world, SpawnReason spawnReason, BlockPos blockPos, Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && spawnPredicateCanSpawn(entityType, world, spawnReason, blockPos, random);
    }
    protected final int MOVE_TO_RESTRICT_PRIO = 3;
    private final boolean countAsMonster;

    public HunterBaseEntity(EntityType<? extends HunterBaseEntity> type, World world, boolean countAsMonster) {
        super(type, world);
        this.countAsMonster = countAsMonster;
    }

    @Override
    public EntityClassification getClassification(boolean forSpawnCount) {
        if (forSpawnCount && countAsMonster) {
            return EntityClassification.MONSTER;
        }
        return super.getClassification(forSpawnCount);
    }

    @Override
    public LivingEntity getRepresentingEntity() {
        return this;
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (cause.getEntity() instanceof ServerPlayerEntity && Helper.isVampire(((PlayerEntity) cause.getEntity())) && this.getEffect(ModEffects.FREEZE.get()) != null) {
            ModAdvancements.TRIGGER_VAMPIRE_ACTION.trigger(((ServerPlayerEntity) cause.getEntity()), VampireActionTrigger.Action.KILL_FROZEN_HUNTER);
        }
    }

    public void makeCampHunter(BlockPos pos) {
        super.setHome(new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).inflate(10));
        this.setMoveTowardsRestriction(MOVE_TO_RESTRICT_PRIO, true);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
    }

    /**
     * Tries to cure sanguinare and if successful sends a message.
     *
     * @return If player was cured
     */
    protected boolean tryCureSanguinare(PlayerEntity entity) {
        if (!this.level.isClientSide && entity.hasEffect(ModEffects.SANGUINARE.get())) {
            entity.removeEffect(ModEffects.SANGUINARE.get());
            entity.sendMessage(new TranslationTextComponent("text.vampirism.hunter.cured_sanguinare"), Util.NIL_UUID);
            return true;
        }
        return false;
    }
}
