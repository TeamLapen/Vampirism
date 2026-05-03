package de.teamlapen.vampirism.client.extensions;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.Nullable;

public class FluidExtensions {

    public static final IClientFluidTypeExtensions BLOOD = new IClientFluidTypeExtensions() {

        @Override
        public void modifyFogRender(Camera camera, @Nullable FogEnvironment environment, float renderDistance, float partialTick, FogData fogData) {
            fogData.renderDistanceStart = -1;
            fogData.renderDistanceEnd = 5f;
        }
    };
}
