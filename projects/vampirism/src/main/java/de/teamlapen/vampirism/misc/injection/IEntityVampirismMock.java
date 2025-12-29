package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IEntity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.phys.Vec3;

@Deprecated
public interface IEntityVampirismMock extends IEntity {
    @Override
    default void setEyeHeight(float eyeHeight) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default Vec3 invokeCollide(Vec3 pVec) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default EntityDimensions getDimensions() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default void setDimensions(EntityDimensions dimensions) {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
