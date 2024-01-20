package de.teamlapen.vampirism.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.transformer.meta.MixinInner;

@Mixin(Entity.class)
public interface EntityAccessor {

    @Accessor("eyeHeight")
    float getEyeHeight();

    @Accessor("eyeHeight")
    void setEyeHeight(float eyeHeight);
}
