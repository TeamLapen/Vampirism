package de.teamlapen.vampirism.misc.mixin.accessor;

import de.teamlapen.vampirism.misc.extension.IEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor extends IEntity {

    @Override
    @Accessor("eyeHeight")
    void setEyeHeight(float eyeHeight);

    @Override
    @Invoker("collide")
    Vec3 invokeCollide(Vec3 pVec);

    @Override
    @Accessor("dimensions")
    EntityDimensions getDimensions();

    @Override
    @Accessor("dimensions")
    void setDimensions(EntityDimensions dimensions);

}
