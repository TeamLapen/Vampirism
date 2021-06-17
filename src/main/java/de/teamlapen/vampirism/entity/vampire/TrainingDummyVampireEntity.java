package de.teamlapen.vampirism.entity.vampire;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;


public class TrainingDummyVampireEntity extends BasicVampireEntity {

    private final EntityPredicate PREDICATE = new EntityPredicate().allowInvulnerable().setSkipAttackChecks().setLineOfSiteRequired();
    private int startTicks = 0;
    private float damageTaken = 0;

    public TrainingDummyVampireEntity(EntityType<? extends BasicVampireEntity> type, World world) {
        super(type, world);
        this.disableImobConversion();
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float amount) {
        if (!this.world.isRemote) {
            this.world.getTargettablePlayersWithinAABB(PREDICATE, this, this.getBoundingBox().grow(40)).forEach(p -> p.sendStatusMessage(new StringTextComponent("Damage " + amount + " from " + damageSource.damageType), false));
            if (this.startTicks != 0) this.damageTaken += amount;
        }
        return super.attackEntityFrom(damageSource, amount);

    }

    @Override
    public void convertToMinion(PlayerEntity lord) {
        super.convertToMinion(lord);
    }

    @Override
    protected void damageEntity(DamageSource damageSrc, float damageAmount) {
        if (damageSrc.canHarmInCreative()) {
            super.damageEntity(damageSrc, damageAmount);
        }
    }

    @Override
    protected ActionResultType func_230254_b_(PlayerEntity player, Hand hand) { //processInteract
        if (!this.world.isRemote && hand == Hand.MAIN_HAND) {
            if (startTicks == 0) {
                player.sendStatusMessage(new StringTextComponent("Start recording"), false);
                this.startTicks = this.ticksExisted;
            } else {
                player.sendStatusMessage(new StringTextComponent("Damage: " + damageTaken + " - DPS: " + (damageTaken / ((float) (this.ticksExisted - this.startTicks)) * 20f)), false);
                this.remove();
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    protected void registerGoals() {

    }
}
