package de.teamlapen.vampirism.client.extensions;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.joml.Vector4f;

public class FluidExtensions {

    public static final IClientFluidTypeExtensions BLOOD = new IClientFluidTypeExtensions() {

        @Override
        public ResourceLocation getStillTexture() {
            return VResourceLocation.mod("block/blood_still");
        }

        @Override
        public ResourceLocation getFlowingTexture() {
            return VResourceLocation.mod("block/blood_flow");
        }

        @Override
        public int getTintColor() {
            return 0xEEFF1111;
        }

        @Override
        public Vector4f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f fluidFogColor) {
            return new Vector4f(0.2F, 0.0F, 0.0F, 1.0F);
        }

        @Override
        public FogParameters modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, FogParameters fogParameters) {
            return new FogParameters(-1.0F, 5.0F, fogParameters.shape(), fogParameters.red(), fogParameters.green(), fogParameters.blue(), fogParameters.alpha());
        }
    };

    public static final IClientFluidTypeExtensions IMPURE_BLOOD = new IClientFluidTypeExtensions() {

        @Override
        public ResourceLocation getStillTexture() {
            return VResourceLocation.mod("block/impure_blood_still");
        }

        @Override
        public ResourceLocation getFlowingTexture() {
            return VResourceLocation.mod("block/impure_blood_flow");
        }

        @Override
        public int getTintColor() {
            return 0xEEFF1111;
        }
    };
}
