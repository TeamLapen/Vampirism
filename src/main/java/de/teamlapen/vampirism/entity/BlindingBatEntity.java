package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.player.VampirismPlayer;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.world.gen.biome.VampireBiome;
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
        if (iWorld.getBiome(blockPos) instanceof VampireBiome) return true;
        if (blockPos.getY() >= iWorld.getSeaLevel()) {
            return false;
        } else {
            int i = iWorld.getLight(blockPos);
            int j = 4;
            if (random.nextBoolean())
                return false;

            return i <= random.nextInt(j) && spawnPredicate(entityType, iWorld, spawnReason, blockPos, random);
        }
    }
    private boolean restrictLiveSpan;

    public BlindingBatEntity(EntityType<? extends BlindingBatEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return worldIn.checkNoEntityCollision(this, VoxelShapes.create(this.getBoundingBox())) && worldIn.isCollisionBoxesEmpty(this, this.getBoundingBox()) && !worldIn.containsAnyLiquid(this.getBoundingBox());
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
            List<PlayerEntity> l = world.getEntitiesWithinAABB(PlayerEntity.class, this.getBoundingBox());
            for (PlayerEntity e : l) {
                if (e.isAlive() && VampirePlayer.getOpt(e).map(VampirismPlayer::getLevel).orElse(0) == 0) {
                    e.addPotionEffect(new EffectInstance(Effects.BLINDNESS, BalanceMobProps.mobProps.BLINDING_BAT_EFFECT_DURATION));
                }
            }
        }
    }
}
