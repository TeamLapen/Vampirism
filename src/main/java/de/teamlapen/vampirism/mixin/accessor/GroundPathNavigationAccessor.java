package de.teamlapen.vampirism.mixin.accessor;

import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GroundPathNavigation.class)
public interface GroundPathNavigationAccessor {

    @Accessor("avoidSun")
    boolean getAvoidSun();
}
