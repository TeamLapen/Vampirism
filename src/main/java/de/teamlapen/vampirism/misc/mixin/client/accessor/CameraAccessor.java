package de.teamlapen.vampirism.misc.mixin.client.accessor;

import de.teamlapen.vampirism.misc.extension.client.ICamera;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraAccessor extends ICamera {

    @Override
    @Invoker("move")
    void invokeMove(float zoom, float dy, float dx);
}
