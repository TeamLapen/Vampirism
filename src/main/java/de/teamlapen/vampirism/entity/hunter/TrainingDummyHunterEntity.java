package de.teamlapen.vampirism.entity.hunter;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class TrainingDummyHunterEntity extends BasicHunterEntity {

    private final TargetingConditions PREDICATE = TargetingConditions.forNonCombat().ignoreLineOfSight();
    private int startTicks = 0;
    private float damageTaken = 0;

    public TrainingDummyHunterEntity(EntityType<? extends BasicHunterEntity> type, Level world) {
        super(type, world);
        this.disableImobConversion();
    }

    @Override
    public boolean hurt(@NotNull DamageSource damageSource, float amount) {
        if (!this.level().isClientSide) {
            this.level().getNearbyPlayers(PREDICATE, this, this.getBoundingBox().inflate(40)).forEach(p -> p.displayClientMessage(Component.literal("Damage " + amount + " from " + damageSource.type().msgId()), false));
            if (this.startTicks != 0) this.damageTaken += amount;
        }
        return super.hurt(damageSource, amount);

    }

    @Override
    public void convertToMinion(@NotNull Player lord) {
        super.convertToMinion(lord);
    }

    @Override
    protected void actuallyHurt(@NotNull DamageSource damageSrc, float damageAmount) {
        if (damageSrc.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            super.actuallyHurt(damageSrc, damageAmount);
        }
    }

    @NotNull
    @Override
    protected InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) { //processInteract
        if (!this.level().isClientSide && hand == InteractionHand.MAIN_HAND) {
            if (startTicks == 0) {
                player.displayClientMessage(Component.literal("Start recording"), false);
                this.startTicks = this.tickCount;
            } else {
                player.displayClientMessage(Component.literal("Damage: " + damageTaken + " - DPS: " + (damageTaken / ((float) (this.tickCount - this.startTicks)) * 20f)), false);
                this.discard();
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void registerGoals() {

    }
}