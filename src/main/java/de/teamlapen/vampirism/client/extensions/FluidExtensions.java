package de.teamlapen.vampirism.client.extensions;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.NotNull;

public class FluidExtensions {

    public static final IClientFluidTypeExtensions BLOOD = new IClientFluidTypeExtensions() {

        @Override
        public @NotNull ResourceLocation getStillTexture() {
            return VResourceLocation.mod("block/blood_still");
        }

        @Override
        public @NotNull ResourceLocation getFlowingTexture() {
            return VResourceLocation.mod("block/blood_flow");
        }

        @Override
        public int getTintColor() {
            return 0xEEFF1111;
        }
    };

    public static final IClientFluidTypeExtensions IMPURE_BLOOD = new IClientFluidTypeExtensions() {

        @Override
        public @NotNull ResourceLocation getStillTexture() {
            return VResourceLocation.mod("block/impure_blood_still");
        }

        @Override
        public @NotNull ResourceLocation getFlowingTexture() {
            return VResourceLocation.mod("block/impure_blood_flow");
        }

        @Override
        public int getTintColor() {
            return 0xEEFF1111;
        }
    };
}
