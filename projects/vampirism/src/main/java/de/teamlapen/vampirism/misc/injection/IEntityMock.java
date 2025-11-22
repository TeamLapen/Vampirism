package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IEntity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.phys.Vec3;

public interface IEntityMock extends IEntity {
    @Override
    default void setEyeHeight(float eyeHeight) {

    }

    @Override
    default Vec3 invokeCollide(Vec3 pVec) {
        return null;
    }

    @Override
    default EntityDimensions getDimensions() {
        return null;
    }

    @Override
    default void setDimensions(EntityDimensions dimensions) {

    }
}
