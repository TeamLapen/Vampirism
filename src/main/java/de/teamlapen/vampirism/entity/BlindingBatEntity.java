package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModBiomes;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * Bat which blinds non vampires for a short time.
 */
public class BlindingBatEntity extends BatEntity {

    public static boolean spawnPredicate(EntityType<? extends BlindingBatEntity> entityType, IWorld iWorld, SpawnReason spawnReason, BlockPos blockPos, Random random) {
        if (ModBiomes.VAMPIRE_FOREST.get().getRegistryName().equals(Helper.getBiomeId(iWorld, blockPos)) || ModBiomes.VAMPIRE_FOREST_HILLS.get().getRegistryName().equals(Helper.getBiomeId(iWorld, blockPos)))
            return true;
        if (blockPos.getY() >= iWorld.getSeaLevel()) {
            return false;
        } else {
            int i = iWorld.getMaxLocalRawBrightness(blockPos);
            int j = 4;
            if (random.nextBoolean())
                return false;

            return i <= random.nextInt(j) && checkMobSpawnRules(entityType, iWorld, spawnReason, blockPos, random);
        }
    }

    private final EntityPredicate nonVampirePredicatePlayer = new EntityPredicate().selector(VampirismAPI.factionRegistry().getPredicate(VReference.VAMPIRE_FACTION, true).and(EntityPredicates.NO_CREATIVE_OR_SPECTATOR));
    private final EntityPredicate nonVampirePredicate = new EntityPredicate().selector(e -> !Helper.isVampire(e));
    private boolean restrictLiveSpan;
    private boolean targeting;
    private boolean targetingMob = false;

    public BlindingBatEntity(EntityType<? extends BlindingBatEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public boolean checkSpawnRules(IWorld worldIn, SpawnReason spawnReasonIn) {
        return worldIn.isUnobstructed(this, VoxelShapes.create(this.getBoundingBox())) && worldIn.isUnobstructed(this) && !worldIn.containsAnyLiquid(this.getBoundingBox()); //Check no entity collision
    }

    public void restrictLiveSpan() {
        this.restrictLiveSpan = true;
    }

    public void setTargeting() {
        this.targeting = true;
    }

    @Override
    public void tick() {
        super.tick();
        if (restrictLiveSpan && this.tickCount > BalanceMobProps.mobProps.BLINDING_BAT_LIVE_SPAWN) {
            this.hurt(DamageSource.MAGIC, 10F);
        }
        if (!this.level.isClientSide) {
            List<LivingEntity> l = level.getEntitiesOfClass(targetingMob ? MonsterEntity.class : PlayerEntity.class, this.getBoundingBox());
            boolean hit = false;
            for (LivingEntity e : l) {
                if (e.isAlive() && !Helper.isVampire(e)) {
                    e.addEffect(new EffectInstance(Effects.BLINDNESS, BalanceMobProps.mobProps.BLINDING_BAT_EFFECT_DURATION));
                    hit = true;
                }
            }
            if (targeting && hit) {
                this.hurt(DamageSource.GENERIC, 1000);
            }
        }
    }

    @Override
    protected void customServerAiStep() {
        boolean t = false;
        if (targeting && this.tickCount > 40) {
            targetingMob = false;
            LivingEntity e = level.getNearestPlayer(nonVampirePredicatePlayer, this);
            if (e == null) {
                e = level.getNearestEntity(MonsterEntity.class, nonVampirePredicate, null, this.getX(), this.getY(), this.getZ(), this.getBoundingBox().inflate(20));
                targetingMob = true;
            }
            if (e != null) {
                Vector3d diff = e.position().add(0, e.getEyeHeight(), 0).subtract(this.position());
                double dist = diff.length();
                if (dist < 20) {
                    Vector3d mov = diff.scale(0.15 / dist);
                    this.setDeltaMovement(mov);
                    float f = (float) (MathHelper.atan2(mov.z, mov.x) * (double) (180F / (float) Math.PI)) - 90.0F;
                    float f1 = MathHelper.wrapDegrees(f - this.yRot);
                    this.zza = 0.5F;
                    this.yRot += f1;
                    t = true;
                }
            }

        }
        if (!t) {
            super.customServerAiStep();
        }

    }
}
