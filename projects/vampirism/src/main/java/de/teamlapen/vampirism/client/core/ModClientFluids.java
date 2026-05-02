package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.extensions.FluidExtensions;
import de.teamlapen.vampirism.common.core.ModFluids;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public class ModClientFluids {


    public static final FluidModel.Unbaked BLOOD_MODEL = new FluidModel.Unbaked(new Material(VIdentifier.mod("block/blood_still")), new Material(VIdentifier.mod("block/blood_flow")),null,null);

    public static void registerFluidModels(RegisterFluidModelsEvent event) {
        event.register(BLOOD_MODEL, ModFluids.BLOOD.get());
        event.register(BLOOD_MODEL, ModFluids.FLOWING_BLOOD.get());
    }

    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(FluidExtensions.BLOOD, ModFluids.BLOOD_TYPE.get());
    }
}
