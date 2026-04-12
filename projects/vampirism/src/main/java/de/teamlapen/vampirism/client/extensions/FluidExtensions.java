package de.teamlapen.vampirism.client.extensions;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;

public class FluidExtensions {

    public static final IClientFluidTypeExtensions BLOOD = new IClientFluidTypeExtensions() {


        @Override
        public Identifier getStillTexture() {
            return VIdentifier.mod("block/blood_still");
        }

        @Override
        public Identifier getFlowingTexture() {
            return VIdentifier.mod("block/blood_flow");
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
        public void modifyFogRender(Camera camera, @Nullable FogEnvironment environment, float renderDistance, float partialTick, FogData fogData) {
            fogData.renderDistanceStart = -1;
            fogData.renderDistanceEnd = 5f;
        }
    };
}
