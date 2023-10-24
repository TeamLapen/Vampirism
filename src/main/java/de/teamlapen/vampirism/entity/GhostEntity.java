package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IEntityLeader;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.entity.ai.goals.DefendLeaderGoal;
import de.teamlapen.vampirism.entity.ai.goals.FindLeaderGoal;
import de.teamlapen.vampirism.entity.ai.goals.NearestTargetGoalModifier;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
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
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;

public class GhostEntity extends VampirismEntity implements IRemainsEntity, IEntityFollower {

    private IEntityLeader leader;

    public GhostEntity(@NotNull EntityType<? extends VampirismEntity> type, @NotNull Level world) {
        super(type, world);
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.ARMOR, 15).add(Attributes.ARMOR_TOUGHNESS, 5).add(Attributes.ATTACK_DAMAGE, 6).add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.FLYING_SPEED, 0.3).add(ForgeMod.ENTITY_REACH.get(), 1);
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
        return pSource.is(DamageTypeTags.IS_PROJECTILE) || pSource.is(ModTags.DamageTypes.MOTHER_RESISTANT_TO) && super.isInvulnerableTo(pSource);
    }

    @Override
    public void playerTouch(Player pPlayer) {
        if (pPlayer.canFreeze()) {
            pPlayer.setTicksFrozen(Math.min(pPlayer.getTicksFrozen() + 2, pPlayer.getTicksRequiredToFreeze() + 10));
        }
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
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);
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
    public float getPathfindingMalus(@NotNull BlockPathTypes pNodeType) {
        return 0;
    }

    class GhostMeleeAttackGoal extends MeleeAttackGoal {

        public GhostMeleeAttackGoal(double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
            super(GhostEntity.this, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
        }

        @Override
        protected double getAttackReachSqr(LivingEntity pAttackTarget) {
            return this.mob.getBbWidth() * 3.0F * this.mob.getBbWidth() * 3.0F + pAttackTarget.getBbWidth();
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
