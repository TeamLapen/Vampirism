package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IEntityLeader;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.core.tags.ModDamageTypeTags;
import de.teamlapen.vampirism.entity.ai.goals.DefendLeaderGoal;
import de.teamlapen.vampirism.entity.ai.goals.FindLeaderGoal;
import de.teamlapen.vampirism.entity.ai.goals.NearestTargetGoalModifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class GhostEntity extends VampirismEntity implements IRemainsEntity, IEntityFollower {

    private static final UUID SPEED_MODIFIER = UUID.fromString("e8c3b0b0-3d6c-11eb-b378-0242ac130002");
    private IEntityLeader leader;

    public GhostEntity(@NotNull EntityType<? extends VampirismEntity> type, @NotNull Level world) {
        super(type, world);
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.ARMOR, 15).add(Attributes.ARMOR_TOUGHNESS, 5).add(Attributes.ATTACK_DAMAGE, 6).add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.FLYING_SPEED, 0.3).add(Attributes.ENTITY_INTERACTION_RANGE, 1);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        GhostPathNavigation navigation = new GhostPathNavigation(this, pLevel);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource pSource) {
        return pSource.is(DamageTypeTags.IS_PROJECTILE) || pSource.is(ModDamageTypeTags.MOTHER_RESISTANT_TO) && super.isInvulnerableTo(pSource);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new GhostMeleeAttackGoal(1, true));
        this.goalSelector.addGoal(3, new FindLeaderGoal<>(this, VulnerableRemainsDummyEntity.class::isInstance));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 0.9F));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 16));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        NearestAttackableTargetGoal<Player> goal = new NearestAttackableTargetGoal<>(this, Player.class, 0, false, false, VampirismAPI.factionRegistry().getPredicate(VReference.VAMPIRE_FACTION, true, false, true, true, null));
        ((NearestTargetGoalModifier) goal).ignoreLineOfSight();
        this.targetSelector.addGoal(3, goal);
        NearestAttackableTargetGoal<?> goal2 = new NearestAttackableTargetGoal<>(this, PathfinderMob.class, 5, false, false, VampirismAPI.factionRegistry().getPredicate(VReference.VAMPIRE_FACTION, false, true, false, true, null)) {
            @Override
            protected double getFollowDistance() {
                return super.getFollowDistance() / 2;
            }
        };
        ((NearestTargetGoalModifier) goal2).ignoreLineOfSight();
        this.targetSelector.addGoal(4, goal2);
        this.targetSelector.addGoal(8, new DefendLeaderGoal<>(this));
    }

    @Override
    public void tick() {
        this.setNoGravity(true);
        this.noPhysics = true;
        checkInsideBlocks();
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);
    }

    @Override
    protected void onInsideBlock(BlockState pState) {
        if (pState.isAir()) {
            AttributeInstance attribute = getAttribute(Attributes.FLYING_SPEED);
            if (attribute != null && attribute.getModifier(SPEED_MODIFIER) == null) {
                attribute.addTransientModifier(new AttributeModifier(SPEED_MODIFIER, "free movement", 0.2, AttributeModifier.Operation.ADD_VALUE));
            }
        } else {
            AttributeInstance attribute = getAttribute(Attributes.FLYING_SPEED);
            if (attribute != null) {
                attribute.removeModifier(SPEED_MODIFIER);
            }
        }
    }

    @Override
    public boolean isFollowing() {
        return this.leader != null;
    }

    @Override
    public <T extends LivingEntity & IEntityLeader> T getLeader() {
        return (T) this.leader;
    }

    @Override
    public <T extends LivingEntity & IEntityLeader> void setLeader(T leader) {
        this.leader = leader;
    }

    @Override
    public float getPathfindingMalus(PathType type) {
        return 0;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.GHOST_AMBIENT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.GHOST_DEATH.get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.GHOST_HURT.get();
    }

    @Override
    public @NotNull SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    class GhostMeleeAttackGoal extends MeleeAttackGoal {

        public GhostMeleeAttackGoal(double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
            super(GhostEntity.this, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
        }

    }

    static class GhostPathNavigation extends FlyingPathNavigation {

        public GhostPathNavigation(Mob pMob, Level pLevel) {
            super(pMob, pLevel);
        }

        @Override
        protected boolean canMoveDirectly(@NotNull Vec3 pPosVec31, @NotNull Vec3 pPosVec32) {
            return true;
        }
    }
}
