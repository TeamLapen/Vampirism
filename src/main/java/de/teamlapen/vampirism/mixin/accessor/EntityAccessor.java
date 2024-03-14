package de.teamlapen.vampirism.mixin.accessor;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {

    @Accessor("eyeHeight")
    float getEyeHeight();

    @Accessor("eyeHeight")
    void setEyeHeight(float eyeHeight);

    @Invoker("collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3")
    Vec3 invoke_collide(Vec3 pVec);

    @Accessor("dimensions")
    EntityDimensions getDimensions();

    @Accessor("dimensions")
    void setDimensions(EntityDimensions dimensions);

    @Accessor("random")
    RandomSource getRandom();
}
