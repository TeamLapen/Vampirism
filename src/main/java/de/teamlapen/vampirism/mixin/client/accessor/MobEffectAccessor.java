package de.teamlapen.vampirism.mixin.client.accessor;

import net.minecraft.world.effect.MobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MobEffect.class)
public interface MobEffectAccessor {

    @Accessor("effectRenderer")
    Object getEffectRenderer();

    @Accessor("effectRenderer")
    void setEffectRenderer(Object effectRenderer);
}
