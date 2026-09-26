package de.teamlapen.vampirism.common.world.fluids;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;

public class BloodFluidType extends FluidType {

    /**
     * Acceleration from movement input per tick. Must stay above the fluid's motionScale (current push per tick) so entities can still swim against the flow, just slowly.
     */
    private static final float SWIM_SPEED = 0.012F;
    /**
     * Horizontal velocity kept per tick. Lower than water (~0.8) because blood is thicker.
     */
    private static final float HORIZONTAL_DRAG = 0.65F;

    public BloodFluidType(Properties properties) {
        super(properties);
    }

    @Override
    public boolean move(LivingEntity entity, Vec3 movementVector, double gravity) {
        boolean isFalling = entity.getDeltaMovement().y <= 0.0;
        double oldY = entity.getY();
        float speed = SWIM_SPEED *(float) entity.getAttributeValue(NeoForgeMod.SWIM_SPEED);
        entity.moveRelative(speed, movementVector);
        entity.move(MoverType.SELF, entity.getDeltaMovement());
        Vec3 movement = entity.getDeltaMovement();
        if (entity.horizontalCollision && entity.onClimbable()) {
            movement = new Vec3(movement.x, 0.2, movement.z);
        }
        movement = movement.multiply(HORIZONTAL_DRAG, 0.8F, HORIZONTAL_DRAG);
        entity.setDeltaMovement(entity.getFluidFallingAdjustedMovement(gravity, isFalling, movement));

        movement = entity.getDeltaMovement();
        if (entity.horizontalCollision && entity.isFree(movement.x, movement.y + 0.6F - entity.getY() + oldY, movement.z)) {
            entity.setDeltaMovement(movement.x, 0.3F, movement.z);
        }
        return true;
    }
}
