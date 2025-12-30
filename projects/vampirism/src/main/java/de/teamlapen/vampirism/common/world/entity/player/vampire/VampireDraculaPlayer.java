package de.teamlapen.vampirism.common.world.entity.player.vampire;

import de.teamlapen.vampirism.api.world.entity.player.vampire.IDraculaPlayer;
import de.teamlapen.vampirism.common.world.entity.player.vampire.properties.DraculaData;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public interface VampireDraculaPlayer extends IDraculaPlayer {

    DraculaData draculaData();

    @Override
    default boolean isDracula() {
        return draculaData().isDracula();
    }

    @Override
    default void makeDracula() {
        draculaData().makeDracula();
    }

    @Override
    default boolean wingsFunctionalOpen() {
        return switch (this.getWingsState()) {
            case OPEN, OPENING, FLYING -> true;
            case CLOSING, CLOSED -> false;
        };
    }

    @Override
    default boolean wingsVisualOpen() {
        return this.getWingsState() != WingsState.CLOSED;
    }

    @Override
    default boolean openWings() {
        return draculaData().openWings();
    }

    @Override
    default void toggleWings() {
        draculaData().toggleWings();
    }

    @Override
    default void closeWings() {
        draculaData().closeWings();
    }

    @Override
    default void swingWings() {
        DraculaData draculaData = draculaData();
        Player player = asEntity();
        if (draculaData.getWingsState() != WingsState.FLYING) {
            return;
        }
        Vec3 lookAngle = player.getLookAngle();
        Vec3 deltaMovement = player.getDeltaMovement();
        double speed = deltaMovement.lengthSqr();
        double speedMultiplier = Math.clamp(speed, 0, 1);

        var newDeltaMovement = deltaMovement
                .add(new Vec3(
                                lookAngle.x * 0.1 + (lookAngle.x * 1.5 - deltaMovement.x) * 0.5,
                                lookAngle.y * 0.1 + (lookAngle.y * 1.5 - deltaMovement.y) * 0.5,
                                lookAngle.z * 0.1 + (lookAngle.z * 1.5 - deltaMovement.z) * 0.5
                        ).scale(speedMultiplier)
                ).add(0, 0.1, 0);

        if (newDeltaMovement.lengthSqr() > 1) {
            newDeltaMovement = newDeltaMovement.normalize().scale(deltaMovement.length());
        }

        player.setDeltaMovement(newDeltaMovement);
        draculaData.flyAnimationState().fastForward(5, 1);
    }

    @Override
    default AnimationState flyAnimationState() {
        return draculaData().flyAnimationState();
    }

    @Override
    default AnimationState growAnimationState() {
        return draculaData().growAnimationState();
    }

    @Override
    default WingsState getWingsState() {
        return draculaData().getWingsState();
    }
}
