package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.client.extensions.FluidExtensions;
import de.teamlapen.vampirism.common.core.ModFluids;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public class ModClientFluids {

    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(FluidExtensions.BLOOD, ModFluids.BLOOD_TYPE.get());
    }
}
