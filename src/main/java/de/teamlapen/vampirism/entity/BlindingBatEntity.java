package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModBiomes;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * Bat which blinds non vampires for a short time.
 */
public class BlindingBatEntity extends BatEntity {

    public static boolean spawnPredicate(EntityType<? extends BlindingBatEntity> entityType, IWorld iWorld, SpawnReason spawnReason, BlockPos blockPos, Random random) {
        if (ModBiomes.vampire_forest.equals(iWorld.getBiome(blockPos))) return true;
        if (blockPos.getY() >= iWorld.getSeaLevel()) {
            return false;
        } else {
            int i = iWorld.getLight(blockPos);
            int j = 4;
            if (random.nextBoolean())
                return false;

            return i <= random.nextInt(j) && canSpawnOn(entityType, iWorld, spawnReason, blockPos, random);
        }
    }
    private boolean restrictLiveSpan;

    public BlindingBatEntity(EntityType<? extends BlindingBatEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return worldIn.checkNoEntityCollision(this, VoxelShapes.create(this.getBoundingBox())) && worldIn.func_226668_i_(this) && !worldIn.containsAnyLiquid(this.getBoundingBox()); //Check no entity collision
    }

    public void restrictLiveSpan() {
        this.restrictLiveSpan = true;
    }

    @Override
    public void tick() {
        super.tick();
        if (restrictLiveSpan && this.ticksExisted > BalanceMobProps.mobProps.BLINDING_BAT_LIVE_SPAWN) {
            this.attackEntityFrom(DamageSource.MAGIC, 10F);
        }
        if (!this.world.isRemote) {
            List l = world.getEntitiesWithinAABB(PlayerEntity.class, this.getBoundingBox());
            for (Object e : l) {
                if (VampirePlayer.get((PlayerEntity) e).getLevel() == 0) {
                    ((PlayerEntity) e).addPotionEffect(new EffectInstance(Effects.BLINDNESS, BalanceMobProps.mobProps.BLINDING_BAT_EFFECT_DURATION));
                }
            }
        }
    }
}
