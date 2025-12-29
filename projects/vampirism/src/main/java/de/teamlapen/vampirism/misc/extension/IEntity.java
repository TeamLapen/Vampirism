package de.teamlapen.vampirism.misc.extension;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.phys.Vec3;

public interface IEntity {

    void setEyeHeight(float eyeHeight);

    Vec3 invokeCollide(Vec3 pVec);

    EntityDimensions getDimensions();

    void setDimensions(EntityDimensions dimensions);
}
