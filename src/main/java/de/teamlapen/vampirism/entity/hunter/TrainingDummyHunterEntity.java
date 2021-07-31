package de.teamlapen.vampirism.entity.hunter;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class TrainingDummyHunterEntity extends BasicHunterEntity {

    private final EntityPredicate PREDICATE = new EntityPredicate().allowInvulnerable().allowNonAttackable().allowUnseeable();
    private int startTicks = 0;
    private float damageTaken = 0;

    public TrainingDummyHunterEntity(EntityType<? extends BasicHunterEntity> type, World world) {
        super(type, world);
        this.disableImobConversion();
    }

    @Override
    public boolean hurt(DamageSource damageSource, float amount) {
        if (!this.level.isClientSide) {
            this.level.getNearbyPlayers(PREDICATE, this, this.getBoundingBox().inflate(40)).forEach(p -> p.displayClientMessage(new StringTextComponent("Damage " + amount + " from " + damageSource.msgId), false));
            if (this.startTicks != 0) this.damageTaken += amount;
        }
        return super.hurt(damageSource, amount);

    }

    @Override
    public void convertToMinion(PlayerEntity lord) {
        super.convertToMinion(lord);
    }

    @Override
    protected void actuallyHurt(DamageSource damageSrc, float damageAmount) {
        if (damageSrc.isBypassInvul()) {
            super.actuallyHurt(damageSrc, damageAmount);
        }
    }

    @Override
    protected ActionResultType mobInteract(PlayerEntity player, Hand hand) { //processInteract
        if (!this.level.isClientSide && hand == Hand.MAIN_HAND) {
            if (startTicks == 0) {
                player.displayClientMessage(new StringTextComponent("Start recording"), false);
                this.startTicks = this.tickCount;
            } else {
                player.displayClientMessage(new StringTextComponent("Damage: " + damageTaken + " - DPS: " + (damageTaken / ((float) (this.tickCount - this.startTicks)) * 20f)), false);
                this.remove();
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    protected void registerGoals() {

    }
}