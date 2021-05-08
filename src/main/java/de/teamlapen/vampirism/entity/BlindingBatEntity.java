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
        if (ModBiomes.vampire_forest.getRegistryName().equals(Helper.getBiomeId(iWorld, blockPos)) || ModBiomes.vampire_forest_hills.getRegistryName().equals(Helper.getBiomeId(iWorld, blockPos)))
            return true;
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
    private boolean targeting;
    private boolean targetingMob=false;
    private final EntityPredicate nonVampirePredicatePlayer = new EntityPredicate().setCustomPredicate(VampirismAPI.factionRegistry().getPredicate(VReference.VAMPIRE_FACTION, true).and(EntityPredicates.CAN_AI_TARGET));
    private final EntityPredicate nonVampirePredicate = new EntityPredicate().setCustomPredicate(e->!Helper.isVampire(e));

    public BlindingBatEntity(EntityType<? extends BlindingBatEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return worldIn.checkNoEntityCollision(this, VoxelShapes.create(this.getBoundingBox())) && worldIn.checkNoEntityCollision(this) && !worldIn.containsAnyLiquid(this.getBoundingBox()); //Check no entity collision
    }

    public void restrictLiveSpan() {
        this.restrictLiveSpan = true;
    }

    public void setTargeting(){
        this.targeting = true;
    }

    @Override
    public void tick() {
        super.tick();
        if (restrictLiveSpan && this.ticksExisted > BalanceMobProps.mobProps.BLINDING_BAT_LIVE_SPAWN) {
            this.attackEntityFrom(DamageSource.MAGIC, 10F);
        }
        if (!this.world.isRemote) {
            List<LivingEntity> l = world.getEntitiesWithinAABB(targetingMob ? MonsterEntity.class : PlayerEntity.class, this.getBoundingBox());
            boolean hit=false;
            for (LivingEntity e : l) {
                if (e.isAlive() && !Helper.isVampire(e)) {
                    e.addPotionEffect(new EffectInstance(Effects.BLINDNESS, BalanceMobProps.mobProps.BLINDING_BAT_EFFECT_DURATION));
                    hit=true;
                }
            }
            if(targeting && hit){
                this.attackEntityFrom(DamageSource.GENERIC,1000);
            }
        }
    }

    @Override
    protected void updateAITasks() {
        boolean t = false;
        if(targeting&&this.ticksExisted>40){
            targetingMob=false;
            LivingEntity e = world.getClosestPlayer(nonVampirePredicatePlayer,this);
            if(e==null){
                e = world.getClosestEntityWithinAABB(MonsterEntity.class, nonVampirePredicate, null, this.getPosX(), this.getPosY(), this.getPosZ(), this.getBoundingBox().grow(20) );
                targetingMob=true;
            }
            if(e!=null){
                Vector3d diff = e.getPositionVec().add(0,e.getEyeHeight(),0).subtract(this.getPositionVec());
                double dist = diff.length();
                if(dist<20){
                    Vector3d mov = diff.scale(0.15/dist);
                    this.setMotion(mov);
                    float f = (float)(MathHelper.atan2(mov.z, mov.x) * (double)(180F / (float)Math.PI)) - 90.0F;
                    float f1 = MathHelper.wrapDegrees(f - this.rotationYaw);
                    this.moveForward = 0.5F;
                    this.rotationYaw += f1;
                    t=true;
                }
            }

        }
        if(!t){
            super.updateAITasks();
        }

    }
}
