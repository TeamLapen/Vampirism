package de.teamlapen.vampirism.misc.mixin.accessor;

import de.teamlapen.vampirism.misc.extension.IGroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GroundPathNavigation.class)
public interface GroundPathNavigationAccessor extends IGroundPathNavigation {

    @Override
    @Accessor("avoidSun")
    boolean getAvoidSun();
}
