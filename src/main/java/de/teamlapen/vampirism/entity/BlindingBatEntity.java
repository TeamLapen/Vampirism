package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.util.DamageHandler;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Bat which blinds non vampires for a short time.
 */
public class BlindingBatEntity extends Bat {

    public static boolean spawnPredicate(@NotNull EntityType<? extends BlindingBatEntity> entityType, @NotNull LevelAccessor iWorld, MobSpawnType spawnReason, @NotNull BlockPos blockPos, @NotNull RandomSource random) {
        if (iWorld.getBiome(blockPos).is(ModTags.Biomes.IS_VAMPIRE_BIOME)) {
            return true;
        }
        if (blockPos.getY() >= iWorld.getSeaLevel()) {
            return false;
        } else {
            int i = iWorld.getMaxLocalRawBrightness(blockPos);
            int j = 4;
            if (random.nextBoolean()) {
                return false;
            }

            return i <= random.nextInt(j) && checkMobSpawnRules(entityType, iWorld, spawnReason, blockPos, random);
        }
    }

    private final TargetingConditions nonVampirePredicatePlayer = TargetingConditions.forCombat().selector(VampirismAPI.factionRegistry().getPredicate(VReference.VAMPIRE_FACTION, true).and(EntitySelector.NO_CREATIVE_OR_SPECTATOR));
    private final TargetingConditions nonVampirePredicate = TargetingConditions.forCombat().selector(e -> !Helper.appearsAsVampire(e, this));
    private boolean restrictLiveSpan;
    private boolean targeting;
    private boolean targetingMob = false;

    public BlindingBatEntity(@NotNull EntityType<? extends BlindingBatEntity> type, @NotNull Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public boolean checkSpawnRules(@NotNull LevelAccessor worldIn, @NotNull MobSpawnType spawnReasonIn) {
        return worldIn.isUnobstructed(this, Shapes.create(this.getBoundingBox())) && worldIn.isUnobstructed(this) && !worldIn.containsAnyLiquid(this.getBoundingBox()); //Check no entity collision
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
            DamageHandler.hurtVanilla(this, DamageSources::magic, 10f);
        }
        if (!this.level().isClientSide) {
            List<? extends LivingEntity> l = targetingMob ? this.level().getEntitiesOfClass(Monster.class, this.getBoundingBox()) : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox());
            boolean hit = false;
            for (LivingEntity e : l) {
                if (e.isAlive() && !Helper.isVampire(e)) {
                    e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, BalanceMobProps.mobProps.BLINDING_BAT_EFFECT_DURATION));
                    hit = true;
                }
            }
            if (targeting && hit) {
                DamageHandler.kill(this, 1000);
            }
        }
    }

    @Override
    protected void customServerAiStep() {
        boolean t = false;
        if (targeting && this.tickCount > 40) {
            targetingMob = false;
            LivingEntity e = this.level().getNearestPlayer(nonVampirePredicatePlayer, this);
            if (e == null) {
                e = this.level().getNearestEntity(Monster.class, nonVampirePredicate, null, this.getX(), this.getY(), this.getZ(), this.getBoundingBox().inflate(20));
                targetingMob = true;
            }
            if (e != null) {
                Vec3 diff = e.position().add(0, e.getEyeHeight(), 0).subtract(this.position());
                double dist = diff.length();
                if (dist < 20) {
                    Vec3 mov = diff.scale(0.15 / dist);
                    this.setDeltaMovement(mov);
                    float f = (float) (Mth.atan2(mov.z, mov.x) * (double) (180F / (float) Math.PI)) - 90.0F;
                    float f1 = Mth.wrapDegrees(f - this.getYRot());
                    this.zza = 0.5F;
                    this.setYRot(this.getYRot() + f1);
                    t = true;
                }
            }

        }
        if (!t) {
            super.customServerAiStep();
        }

    }

    @Override
    public boolean canFreeze() {
        return false;
    }
}
